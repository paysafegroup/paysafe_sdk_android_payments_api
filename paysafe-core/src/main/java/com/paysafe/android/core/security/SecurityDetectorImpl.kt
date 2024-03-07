/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.security

import com.paysafe.android.core.util.LocalLog

internal class SecurityDetectorImpl(
    private val controller: SecurityDetectorController = SecurityDetectorController()
) : SecurityDetector {

    override fun isEmulator(): Boolean {
        val isEmulator = controller.checkIsEmulator()
        LocalLog.d("EmuRootChecks", "isEmu: $isEmulator")
        return isEmulator
    }

    override fun isRootedDevice(): Boolean {
        val isRootedDevice = controller.checkIsRootedDevice()
        LocalLog.d("EmuRootChecks", "is Root: $isRootedDevice")
        return isRootedDevice
    }

}