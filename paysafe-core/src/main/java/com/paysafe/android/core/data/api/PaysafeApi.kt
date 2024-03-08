/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.data.api

import com.paysafe.android.core.data.entity.PSApiRequest
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.domain.service.PSHttpClient
import com.paysafe.android.core.exception.errorCommunicatingWithServerException
import com.paysafe.android.core.exception.errorName
import com.paysafe.android.core.exception.responseCannotBeHandledException
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

/**
 * Decode a generic request body for an endpoint call.
 *
 * @param requestBody Generic definition type for a body used in a request call.
 * @param requestBodySerializer Serializer for request body.
 */
fun <T> PSApiRequest.withBody(
    requestBody: T, requestBodySerializer: KSerializer<T>
) = also {
    body = Json.encodeToString(requestBodySerializer, requestBody)
}

/**
 * Abstract class for Paysafe API calls configuration, execution and response processing.
 *
 * @property httpClient Http client used for Paysafe APIs.
 */
abstract class PaysafeApi(val httpClient: PSHttpClient) {

    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    /**
     * Generic utility method to perform API calls and process responses.
     *
     * @param apiRequest Paysafe API request object to perform call with http client.
     * @return A generic data type with response wrapped in a Paysafe result object.
     */
    suspend inline fun <reified T> makeRequest(
        apiRequest: PSApiRequest,
    ): PSResult<T?> {
        val response = this.httpClient.internalMakeRequest(apiRequest)
        return when (response) {
            is PSResult.Success -> handleSuccessResponse<T>(response)

            is PSResult.Failure -> response
        }
    }

    inline fun <reified T> handleSuccessResponse(response: PSResult.Success<String>) =
        try {
            if (T::class.isInstance(Unit)) {
                PSResult.Success()
            } else {
                var responseObj: T? = null
                response.value?.let {
                    responseObj = json.decodeFromString<T>(it)
                }
                PSResult.Success(responseObj)
            }
        } catch (iae: IllegalArgumentException) {
            handleRequestIllegalArgumentException()
        } catch (ex: Exception) {
            handleRequestException()
        }

    fun handleRequestIllegalArgumentException(): PSResult.Failure {
        val paysafeException = responseCannotBeHandledException(httpClient.getCorrelationId())
        httpClient.logErrorEvent(paysafeException.errorName(), paysafeException)
        return PSResult.Failure(paysafeException)
    }

    fun handleRequestException(): PSResult.Failure {
        val paysafeException = errorCommunicatingWithServerException(httpClient.getCorrelationId())
        httpClient.logErrorEvent(paysafeException.errorName(), paysafeException)
        return PSResult.Failure(paysafeException)
    }

}
