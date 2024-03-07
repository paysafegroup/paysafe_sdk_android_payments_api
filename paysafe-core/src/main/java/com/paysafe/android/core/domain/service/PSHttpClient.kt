/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.domain.service

import com.paysafe.android.core.data.entity.PSApiRequest
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.domain.exception.PaysafeException
import com.paysafe.android.core.domain.model.config.PSEnvironment

/**
 * Interface definition for a Paysafe http client, and how the request will be performed.
 */
interface PSHttpClient {

    /**
     * Interface definition for a Paysafe http client, and how the request will be performed.
     */
    suspend fun internalMakeRequest(
        apiRequest: PSApiRequest,
    ): PSResult<String>

    fun getCorrelationId(): String

    fun logErrorEvent(
        name: String,
        psException: PaysafeException,
        is3DSEvent: Boolean = false
    )

    val apiKey: String
    val environment: PSEnvironment
}