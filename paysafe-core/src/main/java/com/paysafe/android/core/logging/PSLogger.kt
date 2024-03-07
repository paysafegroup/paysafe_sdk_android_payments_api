/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging

import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.domain.service.PSHttpClient
import com.paysafe.android.core.logging.domain.model.LogEvent
import com.paysafe.android.core.util.LocalLog
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Paysafe logger that encapsulates the possibility to log events using the same coroutine scope
 */
class PSLogger(
    apiKey: String,
    correlationId: String,
    httpClient: PSHttpClient,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val logService: LogService = LogServiceImpl(apiKey, correlationId, httpClient)
) : CoroutineScope by CoroutineScope(SupervisorJob() + ioDispatcher) {

    /**
     * Definition to log an event
     *
     * @param logEvent the [LogEvent] that will be logged
     */
    fun logEvent(logEvent: LogEvent) {
        LocalLog.d("PSLogger", "logEvent: $logEvent")
        launch(coroutineContext) {
            when (val result = logService.logEvent(logEvent)) {
                is PSResult.Success -> LocalLog.d("PSLogger", "Success for event: $logEvent")
                is PSResult.Failure -> LocalLog.d(
                    "PSLogger",
                    "Failure with ${result.exception.message} for event: $logEvent"
                )
            }
        }
    }

}
