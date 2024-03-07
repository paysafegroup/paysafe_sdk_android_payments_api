/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paypal.exception

import com.paysafe.android.core.domain.exception.PaysafeException
import com.paysafe.android.core.exception.genericDisplayMessage

internal const val INVALID_ACCOUNT_ID_FOR_PAYMENT_METHOD = 9061
internal const val PAYPAL_USER_CANCELLED = 9042
internal const val PAYPAL_FAILED_AUTHORIZATION = 9171
internal const val CURRENCY_CODE_INVALID_ISO = 9055
internal const val IMPROPERLY_CREATED_MERCHANT_ACCOUNT_CONFIG = 9073
internal const val AMOUNT_SHOULD_BE_POSITIVE = 9054
internal const val TOKENIZATION_ALREADY_IN_PROGRESS = 9136
internal const val GENERIC_API_ERROR = 9014
internal const val SDK_NOT_INITIALIZED = 9202


internal fun PaysafeException.errorName(): String = when (code) {
    GENERIC_API_ERROR -> "APIError"

    INVALID_ACCOUNT_ID_FOR_PAYMENT_METHOD,
    TOKENIZATION_ALREADY_IN_PROGRESS,
    CURRENCY_CODE_INVALID_ISO,
    IMPROPERLY_CREATED_MERCHANT_ACCOUNT_CONFIG -> "CoreError"

    PAYPAL_USER_CANCELLED,
    PAYPAL_FAILED_AUTHORIZATION -> "PayPalError"

    AMOUNT_SHOULD_BE_POSITIVE -> "CardFormError"

    else -> "PayPalError"
}


internal fun invalidAccountIdForPaymentMethodException(correlationId: String) = PaysafeException(
    code = INVALID_ACCOUNT_ID_FOR_PAYMENT_METHOD,
    displayMessage = genericDisplayMessage(INVALID_ACCOUNT_ID_FOR_PAYMENT_METHOD),
    detailedMessage = "Invalid account id for PayPal.",
    correlationId = correlationId
)

internal fun payPalUserCancelledException(correlationId: String) = PaysafeException(
    code = PAYPAL_USER_CANCELLED,
    displayMessage = genericDisplayMessage(PAYPAL_USER_CANCELLED),
    detailedMessage = "User aborted authentication.",
    correlationId = correlationId
)

internal fun payPalFailedAuthorizationException(correlationId: String) = PaysafeException(
    code = PAYPAL_FAILED_AUTHORIZATION,
    displayMessage = genericDisplayMessage(PAYPAL_FAILED_AUTHORIZATION),
    detailedMessage = "PayPal failed authorization.",
    correlationId = correlationId
)

internal fun currencyCodeInvalidIsoException(correlationId: String) = PaysafeException(
    code = CURRENCY_CODE_INVALID_ISO,
    displayMessage = genericDisplayMessage(CURRENCY_CODE_INVALID_ISO),
    detailedMessage = "Invalid currency parameter.",
    correlationId = correlationId
)

internal fun improperlyCreatedMerchantAccountConfigException(correlationId: String) =
    PaysafeException(
        code = IMPROPERLY_CREATED_MERCHANT_ACCOUNT_CONFIG,
        displayMessage = genericDisplayMessage(IMPROPERLY_CREATED_MERCHANT_ACCOUNT_CONFIG),
        detailedMessage = "Account not configured correctly.",
        correlationId = correlationId
    )

internal fun amountShouldBePositiveException(correlationId: String) = PaysafeException(
    code = AMOUNT_SHOULD_BE_POSITIVE,
    displayMessage = genericDisplayMessage(AMOUNT_SHOULD_BE_POSITIVE),
    detailedMessage = "Amount should be a number greater than 0 no longer than 11 characters.",
    correlationId = correlationId
)

internal fun tokenizationAlreadyInProgressException(correlationId: String) = PaysafeException(
    code = TOKENIZATION_ALREADY_IN_PROGRESS,
    displayMessage = genericDisplayMessage(TOKENIZATION_ALREADY_IN_PROGRESS),
    detailedMessage = "Tokenization is already in progress.",
    correlationId = correlationId
)

internal fun genericApiErrorException(correlationId: String) = PaysafeException(
    code = GENERIC_API_ERROR,
    displayMessage = genericDisplayMessage(GENERIC_API_ERROR),
    detailedMessage = "Unhandled error occurred.",
    correlationId = correlationId
)

internal fun sdkNotInitializedException() = PaysafeException(
    code = SDK_NOT_INITIALIZED,
    displayMessage = genericDisplayMessage(SDK_NOT_INITIALIZED),
    detailedMessage = "PaysafeSDK is not initialized.",
    correlationId = ""// no correlationId in this case
)