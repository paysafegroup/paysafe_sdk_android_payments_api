/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging

import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.logging.domain.model.LogEvent

/**
 * Log service interface that defines the methods to log an event.
 */
fun interface LogService {

    /**
     * Definition to log an event.
     *
     * @param logEvent The event that will be logged.
     * @return Paysafe result wrapper object [PSResult].
     */
    suspend fun logEvent(logEvent: LogEvent): PSResult<Unit>

}
