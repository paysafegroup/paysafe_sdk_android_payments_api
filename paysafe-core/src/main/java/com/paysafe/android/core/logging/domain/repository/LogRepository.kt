/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging.domain.repository

import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.logging.domain.model.LogRequest
import com.paysafe.android.core.logging.domain.model.LogThreeDSRequest

/**
 * Log repository interface that defines the possibility to log an event.
 */
internal interface LogRepository {

    /**
     * Method signature to define how to log an event to mobile endpoint.
     *
     * @param logRequest Information about [LogRequest].
     * @return Paysafe result wrapper object [PSResult].
     */
    suspend fun logMobileEvent(logRequest: LogRequest): PSResult<Unit>

    /**
     * Method signature to define how to log an event to threedsecure endpoint.
     *
     * @param logThreeDSRequest Information about [LogThreeDSRequest].
     * @return Paysafe result wrapper object [PSResult].
     */
    suspend fun logThreeDSecureEvent(logThreeDSRequest: LogThreeDSRequest): PSResult<Unit>

}
