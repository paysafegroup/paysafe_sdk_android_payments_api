/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging.data.repository

import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.logging.data.api.LogApi
import com.paysafe.android.core.logging.data.mapper.toData
import com.paysafe.android.core.logging.domain.model.LogRequest
import com.paysafe.android.core.logging.domain.model.LogThreeDSRequest
import com.paysafe.android.core.logging.domain.repository.LogRepository

internal class LogRepositoryImpl(private val logApi: LogApi) : LogRepository {

    override suspend fun logMobileEvent(logRequest: LogRequest): PSResult<Unit> {
        val requestSerializable = logRequest.toData()
        return when (val response = logApi.logMobileEvent(requestSerializable)) {
            is PSResult.Success -> {
                try {
                    val value = response.value
                    PSResult.Success(value)
                } catch (ex: Exception) {
                    PSResult.Failure(ex)
                }
            }

            is PSResult.Failure -> response
        }
    }

    override suspend fun logThreeDSecureEvent(logThreeDSRequest: LogThreeDSRequest): PSResult<Unit> {
        val requestSerializable = logThreeDSRequest.toData()
        return when (val response = logApi.logThreeDSecureEvent(requestSerializable)) {
            is PSResult.Success -> {
                try {
                    val value = response.value
                    PSResult.Success(value)
                } catch (ex: Exception) {
                    PSResult.Failure(ex)
                }
            }

            is PSResult.Failure -> response
        }
    }

}