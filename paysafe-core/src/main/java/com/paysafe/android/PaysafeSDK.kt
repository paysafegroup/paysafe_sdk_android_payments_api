/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android

import com.paysafe.android.core.BuildConfig
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.core.domain.exception.PaysafeException
import com.paysafe.android.core.domain.exception.PaysafeRuntimeError
import com.paysafe.android.core.domain.exception.toPaysafeException
import com.paysafe.android.core.domain.model.config.PSEnvironment
import com.paysafe.android.core.exception.apiKeyIsEmptyException
import com.paysafe.android.core.exception.errorName
import com.paysafe.android.core.exception.invalidApiKeyException
import com.paysafe.android.core.exception.isRootedOrEmulatorDeviceError
import com.paysafe.android.core.exception.sdkNotInitializedException
import com.paysafe.android.core.security.SecurityDetector
import com.paysafe.android.core.security.SecurityDetectorImpl
import com.paysafe.android.core.util.LocalLog
import com.paysafe.android.core.util.isApiKeyEmpty
import com.paysafe.android.core.util.isApiKeyInvalid
import java.util.UUID

object PaysafeSDK {

    private lateinit var psApiClient: PSApiClient

    @Throws(PaysafeException::class, PaysafeRuntimeError::class)
    fun setup(
        apiKey: String,
        environment: PSEnvironment = PSEnvironment.TEST
    ) {
        validateKey(apiKey)
        if (!isInitialized())
            psApiClient = createPSApiClient(apiKey, environment)
        validateRunsOnSecureEnvironment(environment)
    }

    fun getMerchantReferenceNumber() = UUID.randomUUID().toString()

    fun isInitialized() = this::psApiClient.isInitialized

    @Throws(PaysafeException::class)
    fun getPSApiClient(): PSApiClient =
        if (isInitialized())
            psApiClient
        else
            throw sdkNotInitializedException()

    internal fun validateKey(apiKey: String) {
        if (isApiKeyEmpty(apiKey))
            throw apiKeyIsEmptyException()
        if (isApiKeyInvalid(apiKey))
            throw invalidApiKeyException()
    }

    internal fun createPSApiClient(
        apiKey: String,
        environment: PSEnvironment
    ) = PSApiClient(apiKey, environment)

    internal fun validateRunsOnSecureEnvironment(
        environment: PSEnvironment,
        securityDetector: SecurityDetector = SecurityDetectorImpl()
    ) {
        if (environment != PSEnvironment.PROD)
            return
        val isSafeToRun = !securityDetector.isEmulator() && !securityDetector.isRootedDevice()
        if (isSafeToRun)
            return
        val paysafeRuntimeError = isRootedOrEmulatorDeviceError(getPSApiClient().getCorrelationId())
        if (BuildConfig.DEBUG) {
            val paysafeException = paysafeRuntimeError.toPaysafeException()
            getPSApiClient().logErrorEvent(
                name = paysafeException.errorName(),
                psException = paysafeException
            )
            throw paysafeRuntimeError
        } else {
            LocalLog.e("PaysafeSDKError", paysafeRuntimeError.detailedMessage)
        }
    }
}