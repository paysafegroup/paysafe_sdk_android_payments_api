/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging.data.mapper

import com.paysafe.android.core.logging.data.entity.LogClientInfoSerializable
import com.paysafe.android.core.logging.data.entity.LogErrorMessageSerializable
import com.paysafe.android.core.logging.data.entity.LogIntegrationTypeSerializable
import com.paysafe.android.core.logging.data.entity.LogPayloadSerializable
import com.paysafe.android.core.logging.data.entity.LogRequestSerializable
import com.paysafe.android.core.logging.data.entity.LogThreeDSEventTypeSerializable
import com.paysafe.android.core.logging.data.entity.LogThreeDSRequestSerializable
import com.paysafe.android.core.logging.data.entity.LogThreeDSSdkSerializable
import com.paysafe.android.core.logging.data.entity.LogTypeSerializable
import com.paysafe.android.core.logging.domain.model.LogClientInfo
import com.paysafe.android.core.logging.domain.model.LogIntegrationType
import com.paysafe.android.core.logging.domain.model.LogPayload
import com.paysafe.android.core.logging.domain.model.LogRequest
import com.paysafe.android.core.logging.domain.model.LogThreeDSEventType
import com.paysafe.android.core.logging.domain.model.LogThreeDSRequest
import com.paysafe.android.core.logging.domain.model.LogThreeDSSdk
import com.paysafe.android.core.logging.domain.model.LogType

internal fun LogType.toData() = when (this) {
    LogType.CONVERSION -> LogTypeSerializable.CONVERSION
    LogType.ERROR -> LogTypeSerializable.ERROR
    LogType.WARNING -> LogTypeSerializable.WARNING
}

internal fun LogIntegrationType.toData() = when (this) {
    LogIntegrationType.PAYMENTS_API -> LogIntegrationTypeSerializable.PAYMENTS_API
    LogIntegrationType.STANDALONE_3DS -> LogIntegrationTypeSerializable.STANDALONE_3DS
}

internal fun LogClientInfo.toData() = LogClientInfoSerializable(
    correlationId = correlationId,
    apiKey = apiKey,
    integrationType = integrationType.toData(),
    version = version,
    appName = appName
)

internal fun LogPayload.toData() = when (this) {
    is LogPayload.InfoMessage -> LogPayloadSerializable.Message(
        message = message
    )

    is LogPayload.ErrorMessage -> LogPayloadSerializable.PayloadMessage(
        message = LogErrorMessageSerializable(
            code = code,
            detailedMessage = detailedMessage,
            displayMessage = displayMessage,
            name = name,
            message = message
        )
    )
}

internal fun LogRequest.toData() =
    LogRequestSerializable(
        type = type.toData(),
        clientInfo = clientInfo.toData(),
        payload = payload.toData()
    )

internal fun LogThreeDSEventType.toData(): LogThreeDSEventTypeSerializable = when (this) {
    LogThreeDSEventType.INTERNAL_SDK_ERROR -> LogThreeDSEventTypeSerializable.INTERNAL_SDK_ERROR
    LogThreeDSEventType.VALIDATION_ERROR -> LogThreeDSEventTypeSerializable.VALIDATION_ERROR
    LogThreeDSEventType.SUCCESS -> LogThreeDSEventTypeSerializable.SUCCESS
    LogThreeDSEventType.NETWORK_ERROR -> LogThreeDSEventTypeSerializable.NETWORK_ERROR
}

internal fun LogThreeDSSdk.toData() = LogThreeDSSdkSerializable(
    type = type,
    version = version
)

internal fun LogThreeDSRequest.toData() = LogThreeDSRequestSerializable(
    eventType = eventType.toData(),
    eventMessage = eventMessage,
    sdk = sdk.toData()
)
