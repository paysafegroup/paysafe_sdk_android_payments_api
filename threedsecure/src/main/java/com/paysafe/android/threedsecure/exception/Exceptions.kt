/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.threedsecure.exception

import com.paysafe.android.core.domain.exception.PaysafeException
import com.paysafe.android.core.exception.genericDisplayMessage

internal const val JWT_IS_NULL = 9180
internal const val DEVICE_FINGERPRINT_IS_NULL = 9188
internal const val INIT_3DS_BASIC = 9189
internal const val INIT_3DS_COMPLEX = 9190
internal const val CHALLENGE_SERVER_JWT_IS_NULL = 9191
internal const val CHALLENGE_AUTH_ID_IS_NULL = 9192
internal const val CHALLENGE_ACCOUNT_ID_IS_NULL = 9193
internal const val CHALLENGE_3DS_SESSION_FAILURE = 9198
internal const val CHALLENGE_3DS_TIMEOUT = 9199
internal const val CHALLENGE_3DS_USER_CANCELLED = 9200
internal const val CHALLENGE_3DS_FAILED_VALIDATION = 9201
internal const val GENERIC_API_ERROR = 9014


internal fun PaysafeException.errorName(): String = when (code) {
    GENERIC_API_ERROR -> "APIError"

    JWT_IS_NULL,
    DEVICE_FINGERPRINT_IS_NULL,
    INIT_3DS_BASIC,
    INIT_3DS_COMPLEX,
    CHALLENGE_SERVER_JWT_IS_NULL,
    CHALLENGE_AUTH_ID_IS_NULL,
    CHALLENGE_ACCOUNT_ID_IS_NULL,
    CHALLENGE_3DS_SESSION_FAILURE,
    CHALLENGE_3DS_TIMEOUT,
    CHALLENGE_3DS_USER_CANCELLED,
    CHALLENGE_3DS_FAILED_VALIDATION -> "3DSError"

    else -> "3DSError"
}


internal fun jwtIsNullException(correlationId: String) = PaysafeException(
    code = JWT_IS_NULL,
    displayMessage = genericDisplayMessage(JWT_IS_NULL),
    detailedMessage = "JWT is null.",
    correlationId = correlationId
)

internal fun deviceFingerprintIsNullException(correlationId: String) = PaysafeException(
    code = DEVICE_FINGERPRINT_IS_NULL,
    displayMessage = genericDisplayMessage(DEVICE_FINGERPRINT_IS_NULL),
    detailedMessage = "Device fingerprint is null.",
    correlationId = correlationId
)

internal fun init3DsBasicException(correlationId: String) = PaysafeException(
    code = INIT_3DS_BASIC,
    displayMessage = genericDisplayMessage(INIT_3DS_BASIC),
    detailedMessage = "Error at initializing Cardinal 3DS SDK.",
    correlationId = correlationId
)

internal fun init3DSComplexException(
    errorNumber: Int,
    errorDescription: String,
    correlationId: String
) = PaysafeException(
    code = INIT_3DS_COMPLEX,
    displayMessage = genericDisplayMessage(INIT_3DS_COMPLEX),
    detailedMessage = "Error at initializing Cardinal 3DS SDK: code: ${errorNumber}, message: $errorDescription.",
    correlationId = correlationId
)

internal fun challengeServerJwtIsNullException(correlationId: String) = PaysafeException(
    code = CHALLENGE_SERVER_JWT_IS_NULL,
    displayMessage = genericDisplayMessage(CHALLENGE_SERVER_JWT_IS_NULL),
    detailedMessage = "ServerJwt is null.",
    correlationId = correlationId
)

internal fun challengeAuthIdIsNullException(correlationId: String) = PaysafeException(
    code = CHALLENGE_AUTH_ID_IS_NULL,
    displayMessage = genericDisplayMessage(CHALLENGE_AUTH_ID_IS_NULL),
    detailedMessage = "AuthenticationId is null.",
    correlationId = correlationId
)

internal fun challengeAccountIdIsNullException(correlationId: String) = PaysafeException(
    code = CHALLENGE_ACCOUNT_ID_IS_NULL,
    displayMessage = genericDisplayMessage(CHALLENGE_ACCOUNT_ID_IS_NULL),
    detailedMessage = "AccountId is null.",
    correlationId = correlationId
)

internal fun challenge3DSSessionFailureException(
    errorNumber: Int,
    correlationId: String
) = PaysafeException(
    code = CHALLENGE_3DS_SESSION_FAILURE,
    displayMessage = genericDisplayMessage(CHALLENGE_3DS_SESSION_FAILURE),
    detailedMessage = "Error code: $errorNumber.",
    correlationId = correlationId
)

internal fun challenge3DSTimeoutException(correlationId: String) = PaysafeException(
    code = CHALLENGE_3DS_TIMEOUT,
    displayMessage = genericDisplayMessage(CHALLENGE_3DS_TIMEOUT),
    detailedMessage = "3DS challenge timeout.",
    correlationId = correlationId
)

internal fun challenge3DSUserCancelledException(correlationId: String) = PaysafeException(
    code = CHALLENGE_3DS_USER_CANCELLED,
    displayMessage = genericDisplayMessage(CHALLENGE_3DS_USER_CANCELLED),
    detailedMessage = "User cancelled 3DS challenge.",
    correlationId = correlationId
)

internal fun challenge3DSFailedValidationException(correlationId: String) = PaysafeException(
    code = CHALLENGE_3DS_FAILED_VALIDATION,
    displayMessage = genericDisplayMessage(CHALLENGE_3DS_FAILED_VALIDATION),
    detailedMessage = "JWT is not validated.",
    correlationId = correlationId
)

internal fun genericApiErrorException(correlationId: String) = PaysafeException(
    code = GENERIC_API_ERROR,
    displayMessage = genericDisplayMessage(GENERIC_API_ERROR),
    detailedMessage = "Unhandled error occurred.",
    correlationId = correlationId
)