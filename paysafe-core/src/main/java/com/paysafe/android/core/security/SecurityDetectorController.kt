/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.security

import android.os.Build
import com.paysafe.android.core.util.LocalLog
import java.io.File

private val suPaths = arrayOf(
    "/data/local/",
    "/data/local/bin/",
    "/data/local/xbin/",
    "/sbin/",
    "/su/bin/",
    "/system/bin/",
    "/system/bin/.ext/",
    "/system/bin/failsafe/",
    "/system/sd/xbin/",
    "/system/usr/we-need-root/",
    "/system/xbin/",
    "/cache/",
    "/data/",
    "/dev/"
)


const val BINARY_SU = "su"
const val BINARY_MAGISK = "magisk"

internal class SecurityDetectorController {

    fun checkIsEmulator() =
        isEmulatorFromBuild()
                || isEmulatorFromAbi()

    fun checkIsRootedDevice() =
        checkForBinary(BINARY_SU)
                || checkSuExists()
                || checkForBinary(BINARY_MAGISK)

    internal fun isEmulatorFromBuild(): Boolean {
        val model = Build.MODEL?.lowercase() ?: ""
        val manufacturer = Build.MANUFACTURER?.lowercase() ?: ""
        val device = Build.DEVICE?.lowercase() ?: ""
        val hardware = Build.HARDWARE?.lowercase() ?: ""

        LocalLog.d("SecurityDetectorController", "build mod: $model")
        LocalLog.d("SecurityDetectorController", "build manu: $manufacturer")
        LocalLog.d("SecurityDetectorController", "build dev: $device")
        LocalLog.d("SecurityDetectorController", "build hard: $hardware")
        return when {
            model.isNotEmpty() && model.containsAny("sdk", "emulator") -> true
            manufacturer.isNotEmpty() && manufacturer.containsAny("unknown", "genymobile") -> true
            device.isNotEmpty() && device.containsAny(
                "generic",
                "emu64",
                "motion_phone_arm64"
            ) -> true

            hardware.isNotEmpty() && device.containsAny("goldfish", "vbox") -> true
            else -> false
        }
    }

    internal fun isEmulatorFromAbi(): Boolean {
        val abi = Build.SUPPORTED_ABIS?.firstOrNull() ?: ""
        LocalLog.d("SecurityDetectorController", "abi: $abi")
        return abi.isNotEmpty() && abi.containsAny("x86", "x86_64")
    }

    internal fun checkForBinary(fileName: String): Boolean {
        val searchPaths = getPaths(suPaths)
        searchPaths.forEach { path ->
            val fullPath = "$path$fileName"
            if (fileExists(fullPath)) {
                LocalLog.d("SecurityDetectorController", "$fullPath binary detected!")
                return true
            }
        }
        return false
    }


    internal fun checkSuExists(): Boolean {
        val command = arrayOf("which", BINARY_SU)
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(command)
            process.inputStream.bufferedReader().use { it.readLine() != null }
        } catch (t: Throwable) {
            false
        } finally {
            process?.destroy()
        }
    }

    internal fun getPaths(pathList: Array<String>): List<String> {
        val paths = pathList.toMutableList()
        val systemPaths = System.getenv("PATH") ?: return paths

        systemPaths.split(":").forEach { rawPath ->
            val normalizedPath = if (!rawPath.endsWith("/")) "$rawPath/" else rawPath
            if (normalizedPath !in paths) {
                paths.add(normalizedPath)
            }
        }

        return paths
    }
}

internal fun String.containsAny(vararg stringList: String) = stringList.any { this.contains(it) }

internal fun fileExists(filePath: String): Boolean = File(filePath).exists()
