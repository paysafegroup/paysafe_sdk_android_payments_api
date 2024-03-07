/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging

import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.domain.service.PSHttpClient
import com.paysafe.android.core.logging.data.api.LogApi
import com.paysafe.android.core.logging.data.repository.LogRepositoryImpl
import com.paysafe.android.core.logging.domain.model.LogClientInfo
import com.paysafe.android.core.logging.domain.model.LogEvent
import com.paysafe.android.core.logging.domain.model.LogPayload
import com.paysafe.android.core.logging.domain.model.LogRequest
import com.paysafe.android.core.logging.domain.model.LogThreeDSEventType
import com.paysafe.android.core.logging.domain.model.LogThreeDSRequest

internal class LogServiceImpl(
    private val apiKey: String,
    private val correlationId: String,
    httpClient: PSHttpClient,
) : LogService {

    private val logApi = LogApi(httpClient)
    private val logRepository = LogRepositoryImpl(logApi)

    override suspend fun logEvent(logEvent: LogEvent): PSResult<Unit> =
        if (!logEvent.is3DSEvent)
            logMobileEvent(logEvent)
        else
            logThreeDSecureEvent(logEvent)

    private suspend fun logMobileEvent(logEvent: LogEvent): PSResult<Unit> {
        val logRequest = with(logEvent) {
            LogRequest(
                type = type,
                clientInfo = LogClientInfo(
                    apiKey = apiKey,
                    correlationId = correlationId,
                    integrationType = integrationType
                ),
                payload = when (logEvent) {
                    is LogEvent.InfoMessage -> LogPayload.InfoMessage(
                        message = logEvent.message
                    )

                    is LogEvent.ErrorMessage -> with(logEvent.errorMessage) {
                        LogPayload.ErrorMessage(
                            code = code,
                            detailedMessage = detailedMessage,
                            displayMessage = displayMessage,
                            name = name,
                            message = message
                        )
                    }
                }
            )
        }
        return logRepository.logMobileEvent(logRequest)
    }

    private suspend fun logThreeDSecureEvent(logEvent: LogEvent): PSResult<Unit> {
        val eventType: LogThreeDSEventType
        val eventMessage: String
        when (logEvent) {
            is LogEvent.InfoMessage -> {
                eventType = LogThreeDSEventType.SUCCESS
                eventMessage = logEvent.message
            }

            is LogEvent.ErrorMessage -> {
                eventType = LogThreeDSEventType.INTERNAL_SDK_ERROR
                eventMessage = logEvent.errorMessage.detailedMessage
            }
        }
        val logThreeDSRequest = LogThreeDSRequest(
            eventType = eventType,
            eventMessage = eventMessage
        )
        return logRepository.logThreeDSecureEvent(logThreeDSRequest)
    }

}
