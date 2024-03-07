/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.exception

import com.paysafe.android.core.domain.exception.PaysafeException
import com.paysafe.android.core.domain.exception.PaysafeRuntimeError

internal const val PS_ERROR_CURRENCY_CODE_INVALID_ISO = 5001
internal const val PS_ERROR_INVALID_AMOUNT = 5003
internal const val PS_ERROR_INVALID_COUNTRY = 5010
internal const val PS_ERROR_AUTH_FAILED_API_CODE = 5279
internal val PS_ERROR_3DS_API_KEY_ERRORS = listOf(5000, 5001, 5275, 5276, 5278, 5280)

internal const val NO_CONNECTION_TO_SERVER = 9001
internal const val RESPONSE_CANNOT_BE_HANDLED = 9002
internal const val INVALID_API_KEY = 9013
internal const val GENERIC_API_ERROR = 9014
internal const val INVALID_AMOUNT = 9054
internal const val CURRENCY_CODE_INVALID_ISO = 9055
internal const val INVALID_COUNTRY = 9068
internal const val FAILED_TO_LOAD_AVAILABLE_METHODS = 9084
internal const val API_KEY_IS_EMPTY = 9167
internal const val GENERAL_ERROR_COMMUNICATING_WITH_SERVER = 9168
internal const val IS_ROOTED_OR_EMULATOR_DEVICE = 9203
internal const val SDK_NOT_INITIALIZED = 9202
internal const val HTTP_ERROR = 9206


fun PaysafeException.errorName(): String = when (code) {
    NO_CONNECTION_TO_SERVER,
    RESPONSE_CANNOT_BE_HANDLED,
    GENERAL_ERROR_COMMUNICATING_WITH_SERVER,
    GENERIC_API_ERROR,
    HTTP_ERROR -> "APIError"

    API_KEY_IS_EMPTY,
    INVALID_API_KEY,
    CURRENCY_CODE_INVALID_ISO,
    FAILED_TO_LOAD_AVAILABLE_METHODS,
    INVALID_COUNTRY,
    INVALID_AMOUNT,
    IS_ROOTED_OR_EMULATOR_DEVICE -> "CoreError"

    else -> "APIError"
}


fun genericDisplayMessage(
    errorCode: Int
) = "There was an error (${errorCode}), please contact our support."

internal fun apiKeyIsEmptyException() = PaysafeException(
    code = API_KEY_IS_EMPTY,
    displayMessage = genericDisplayMessage(API_KEY_IS_EMPTY),
    detailedMessage = "The API Key should not be empty.",
    correlationId = ""// no correlationId in this case
)

internal fun noConnectionToServerException(correlationId: String) = PaysafeException(
    code = NO_CONNECTION_TO_SERVER,
    displayMessage = genericDisplayMessage(NO_CONNECTION_TO_SERVER),
    detailedMessage = "No connection to server.",
    correlationId = correlationId
)

fun responseCannotBeHandledException(correlationId: String) = PaysafeException(
    code = RESPONSE_CANNOT_BE_HANDLED,
    displayMessage = genericDisplayMessage(RESPONSE_CANNOT_BE_HANDLED),
    detailedMessage = "Error communicating with server.",
    correlationId = correlationId
)

fun errorCommunicatingWithServerException(correlationId: String) = PaysafeException(
    code = GENERAL_ERROR_COMMUNICATING_WITH_SERVER,
    displayMessage = genericDisplayMessage(GENERAL_ERROR_COMMUNICATING_WITH_SERVER),
    detailedMessage = "Error communicating with server.",
    correlationId = correlationId
)

internal fun invalidApiKeyException(correlationId: String = "") = PaysafeException(
    code = INVALID_API_KEY,
    displayMessage = genericDisplayMessage(INVALID_API_KEY),
    detailedMessage = "Invalid API key.",
    correlationId = correlationId
)

internal fun invalidApiKeyParameterException(correlationId: String) = PaysafeException(
    code = INVALID_API_KEY,
    displayMessage = genericDisplayMessage(INVALID_API_KEY),
    detailedMessage = "Invalid apiKey parameter.",
    correlationId = correlationId
)

internal fun currencyCodeInvalidIsoException(correlationId: String) = PaysafeException(
    code = CURRENCY_CODE_INVALID_ISO,
    displayMessage = genericDisplayMessage(CURRENCY_CODE_INVALID_ISO),
    detailedMessage = "Invalid currency parameter.",
    correlationId = correlationId
)

internal fun failedToLoadAvailableMethodsException(correlationId: String) = PaysafeException(
    code = FAILED_TO_LOAD_AVAILABLE_METHODS,
    displayMessage = genericDisplayMessage(FAILED_TO_LOAD_AVAILABLE_METHODS),
    detailedMessage = "Failed to load available payment methods.",
    correlationId = correlationId
)

internal fun genericApiErrorException(correlationId: String) = PaysafeException(
    code = GENERIC_API_ERROR,
    displayMessage = genericDisplayMessage(GENERIC_API_ERROR),
    detailedMessage = "Unhandled error occurred.",
    correlationId = correlationId
)

internal fun isRootedOrEmulatorDeviceError(correlationId: String) = PaysafeRuntimeError(
    code = IS_ROOTED_OR_EMULATOR_DEVICE,
    displayMessage = genericDisplayMessage(IS_ROOTED_OR_EMULATOR_DEVICE),
    detailedMessage = "The PROD environment cannot be used in a simulator, emulator, rooted or jailbroken devices.",
    correlationId = correlationId
)

internal fun invalidAmountException(correlationId: String) = PaysafeException(
    code = INVALID_AMOUNT,
    displayMessage = genericDisplayMessage(INVALID_AMOUNT),
    detailedMessage = "Amount should be a number greater than 0 no longer than 11 characters.",
    correlationId = correlationId
)

internal fun invalidCountryException(correlationId: String) = PaysafeException(
    code = INVALID_COUNTRY,
    displayMessage = genericDisplayMessage(INVALID_COUNTRY),
    detailedMessage = "Invalid country parameter.",
    correlationId = correlationId
)

internal fun apiClientResponseHttpErrorException(
    httpErrorCode: Int,
    correlationId: String
) = PaysafeException(
    code = HTTP_ERROR,
    displayMessage = genericDisplayMessage(HTTP_ERROR),
    detailedMessage = "Error: HTTP Error $httpErrorCode.",
    correlationId = correlationId
)

internal fun sdkNotInitializedException() = PaysafeException(
    code = SDK_NOT_INITIALIZED,
    displayMessage = genericDisplayMessage(SDK_NOT_INITIALIZED),
    detailedMessage = "PaysafeSDK is not initialized.",
    correlationId = ""// no correlationId in this case
)

internal fun defaultPSErrorException(
    code: Int,
    detailedMessage: String,
    correlationId: String
) = PaysafeException(
    code = code,
    displayMessage = genericDisplayMessage(code),
    detailedMessage = detailedMessage,
    correlationId = correlationId
)
