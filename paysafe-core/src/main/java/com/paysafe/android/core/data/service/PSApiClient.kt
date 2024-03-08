/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.data.service

import com.paysafe.android.core.BuildConfig
import com.paysafe.android.core.data.entity.PSApiRequest
import com.paysafe.android.core.data.entity.PSApiRequestType
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.error.PSErrorResponseSerializable
import com.paysafe.android.core.data.mapper.toDomain
import com.paysafe.android.core.domain.exception.PaysafeException
import com.paysafe.android.core.domain.model.config.PSEnvironment
import com.paysafe.android.core.domain.service.PSHttpClient
import com.paysafe.android.core.exception.PS_ERROR_3DS_API_KEY_ERRORS
import com.paysafe.android.core.exception.PS_ERROR_AUTH_FAILED_API_CODE
import com.paysafe.android.core.exception.PS_ERROR_CURRENCY_CODE_INVALID_ISO
import com.paysafe.android.core.exception.PS_ERROR_INVALID_AMOUNT
import com.paysafe.android.core.exception.PS_ERROR_INVALID_COUNTRY
import com.paysafe.android.core.exception.apiClientResponseHttpErrorException
import com.paysafe.android.core.exception.currencyCodeInvalidIsoException
import com.paysafe.android.core.exception.defaultPSErrorException
import com.paysafe.android.core.exception.errorCommunicatingWithServerException
import com.paysafe.android.core.exception.errorName
import com.paysafe.android.core.exception.genericApiErrorException
import com.paysafe.android.core.exception.invalidAmountException
import com.paysafe.android.core.exception.invalidApiKeyException
import com.paysafe.android.core.exception.invalidApiKeyParameterException
import com.paysafe.android.core.exception.invalidCountryException
import com.paysafe.android.core.exception.noConnectionToServerException
import com.paysafe.android.core.exception.responseCannotBeHandledException
import com.paysafe.android.core.logging.PSLogger
import com.paysafe.android.core.logging.domain.model.LogErrorMessage
import com.paysafe.android.core.logging.domain.model.LogEvent
import com.paysafe.android.core.logging.domain.model.LogIntegrationType
import com.paysafe.android.core.logging.domain.model.LogType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Headers.Companion.toHeaders
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import java.net.HttpURLConnection
import java.util.UUID
import java.util.concurrent.TimeUnit

private val jsonMediaType = "application/json".toMediaType()
private const val EMPTY_JSON = "{}"
private const val CORRELATION_ID_HEADER = "X-INTERNAL-CORRELATION-ID"
private const val SDK_VERSION_HEADER = "X-App-Version"
private const val SDK_SOURCE_HEADER = "X-TransactionSource"
private const val SDK_SOURCE_ANDROID = "AndroidSDKV2"
private const val CALL_TIMEOUT = 15L

/**
 * Paysafe api client.
 *
 * @property apiKey Paysafe client configuration.
 * @property environment Environment to initialize this client, TEST or PROD.
 */
class PSApiClient internal constructor(
    override val apiKey: String,
    override val environment: PSEnvironment
) : PSHttpClient {

    var customSDKSource: String? = null

    private val correlationId = UUID.randomUUID().toString()
    private val loggingInterceptorEnabled = true
    private val baseUrl = environment.url.toHttpUrlOrNull()
    private val psLogger: PSLogger = PSLogger(apiKey, correlationId, this@PSApiClient)


    private val okHttpClient = createOkHttpClient {
        callTimeout(CALL_TIMEOUT, TimeUnit.SECONDS)
        addInterceptor(ApiConfigInterceptor(apiKey))
        if (loggingInterceptorEnabled) addLoggingInterceptor(this)
    }

    init {
        logInitializeEvent()
    }

    override suspend fun internalMakeRequest(apiRequest: PSApiRequest): PSResult<String> {
        if (baseUrl == null) {
            val paysafeException = errorCommunicatingWithServerException(correlationId)
            return PSResult.Failure(paysafeException)
        }

        val request = prepareRequest(baseUrl, apiRequest)
        try {
            val response = okHttpClient.newCall(request).execute()
            val responseBodyString = response.body?.string()
            return if (response.isSuccessful) {
                PSResult.Success(responseBodyString)
            } else {
                errorInternalResponse(
                    responseBodyString = responseBodyString,
                    responseCode = response.code,
                    is3DSRequest = is3DSRequest(apiRequest.path)
                )
            }
        } catch (io: IOException) {
            val paysafeException = noConnectionToServerException(correlationId)
            return PSResult.Failure(paysafeException)
        } catch (ex: Exception) {
            val paysafeException = genericApiErrorException(correlationId)
            logErrorEvent(paysafeException.errorName(), paysafeException)
            return PSResult.Failure(paysafeException)
        }
    }

    override fun getCorrelationId() = correlationId

    override fun logErrorEvent(
        name: String,
        psException: PaysafeException,
        is3DSEvent: Boolean
    ) = psLogger.logEvent(
        LogEvent.ErrorMessage(
            type = LogType.CONVERSION,
            integrationType = LogIntegrationType.PAYMENTS_API,
            errorMessage = LogErrorMessage(
                name = name,
                code = psException.code.toString(),
                message = psException.message ?: "",
                displayMessage = psException.displayMessage,
                detailedMessage = psException.detailedMessage
            ),
            is3DSEvent = is3DSEvent
        )
    )

    fun logEvent(message: String) = psLogger.logEvent(
        LogEvent.InfoMessage(
            type = LogType.CONVERSION,
            integrationType = LogIntegrationType.PAYMENTS_API,
            message = message
        )
    )

    private fun errorInternalResponse(
        responseBodyString: String?,
        responseCode: Int,
        is3DSRequest: Boolean
    ): PSResult.Failure {
        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            val paysafeException = invalidApiKeyParameterException(correlationId)
            logErrorEvent(paysafeException.errorName(), paysafeException)
            return PSResult.Failure(paysafeException)
        }

        val errorResponse = responseBodyString?.let {
            val json = Json {
                ignoreUnknownKeys = true
            }
            json.decodeFromString<PSErrorResponseSerializable>(it)
        }

        val psError = errorResponse?.error?.toDomain()

        if (psError != null) {
            return when {
                psError.code == PS_ERROR_AUTH_FAILED_API_CODE -> {
                    val paysafeException = if (is3DSRequest)
                        invalidApiKeyParameterException(correlationId)
                    else
                        invalidApiKeyException(correlationId)
                    logErrorEvent(paysafeException.errorName(), paysafeException, is3DSRequest)
                    PSResult.Failure(paysafeException)
                }

                psError.code == PS_ERROR_CURRENCY_CODE_INVALID_ISO -> {
                    val paysafeException = currencyCodeInvalidIsoException(correlationId)
                    logErrorEvent(paysafeException.errorName(), paysafeException, is3DSRequest)
                    PSResult.Failure(paysafeException)
                }

                is3DSRequest && psError.code == PS_ERROR_INVALID_AMOUNT -> {
                    val paysafeException = invalidAmountException(correlationId)
                    logErrorEvent(paysafeException.errorName(), paysafeException, is3DSEvent = true)
                    PSResult.Failure(paysafeException)
                }

                is3DSRequest && psError.code == PS_ERROR_INVALID_COUNTRY -> {
                    val paysafeException = invalidCountryException(correlationId)
                    logErrorEvent(paysafeException.errorName(), paysafeException, is3DSEvent = true)
                    PSResult.Failure(paysafeException)
                }

                is3DSRequest && psError.code in PS_ERROR_3DS_API_KEY_ERRORS -> {
                    val paysafeException = invalidApiKeyParameterException(correlationId)
                    logErrorEvent(paysafeException.errorName(), paysafeException, is3DSEvent = true)
                    PSResult.Failure(paysafeException)
                }

                psError.code == 0 && psError.details.isEmpty() -> {
                    val paysafeException = responseCannotBeHandledException(correlationId)
                    logErrorEvent(paysafeException.errorName(), paysafeException)
                    PSResult.Failure(paysafeException)
                }

                else -> {
                    val paysafeException =
                        defaultPSErrorException(psError.code, psError.message, correlationId)
                    logErrorEvent(paysafeException.errorName(), paysafeException)
                    PSResult.Failure(paysafeException)
                }
            }
        }

        val paysafeException = apiClientResponseHttpErrorException(
            httpErrorCode = responseCode,
            correlationId = correlationId
        )
        logErrorEvent(paysafeException.errorName(), paysafeException)
        return PSResult.Failure(paysafeException)
    }

    private fun is3DSRequest(path: String) = path.contains("threedsecure/v2")

    private fun prepareRequest(httpUrl: HttpUrl, apiRequest: PSApiRequest) = createRequest {
        url(createUrl(httpUrl, apiRequest))
        val headersMap = prepareHeaders(apiRequest)
        headers(headersMap.toHeaders())
        when (apiRequest.requestType) {
            PSApiRequestType.GET -> get()
            PSApiRequestType.POST -> post(
                (apiRequest.body ?: EMPTY_JSON).toRequestBody(jsonMediaType)
            )
        }
    }

    private fun prepareHeaders(apiRequest: PSApiRequest) = apiRequest.headers + mapOf(
        CORRELATION_ID_HEADER to correlationId,
        SDK_VERSION_HEADER to BuildConfig.APP_VERSION,
        SDK_SOURCE_HEADER to (customSDKSource ?: SDK_SOURCE_ANDROID)
    )

    private fun createUrl(httpUrl: HttpUrl, apiRequest: PSApiRequest) =
        httpUrl.newBuilder().apply {
            addPathSegments(apiRequest.path)
            apiRequest.queryParams.forEach { entry ->
                addQueryParameter(entry.key, entry.value)
            }
        }.build()

    private fun addLoggingInterceptor(okHttpClientBuilder: OkHttpClient.Builder) {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        }
        okHttpClientBuilder.addInterceptor(loggingInterceptor)
    }

    private fun createOkHttpClient(block: OkHttpClient.Builder.() -> Unit) =
        OkHttpClient.Builder().apply(block).build()

    private fun createRequest(block: Request.Builder.() -> Unit) =
        Request.Builder().apply(block).build()

    private fun logInitializeEvent() {
        val message = "Options object passed on PSApiClient initialize: ${
            Json.encodeToString(LogEventContent(environment))
        }"
        logEvent(message)
    }

}

private class ApiConfigInterceptor(
    private val apiKey: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestWithApiKey = originalRequest.newBuilder()
            .header("Authorization", "Basic $apiKey")
            .build()
        return chain.proceed(requestWithApiKey)
    }
}

@Serializable
private data class LogEventContent(
    @SerialName("environment")
    val environment: PSEnvironment,
)