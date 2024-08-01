/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.exception

import com.paysafe.android.core.domain.exception.PaysafeException
import com.paysafe.android.core.exception.genericDisplayMessage

internal const val INVALID_ACCOUNT_ID_FOR_PAYMENT_METHOD = 9061
internal const val INVALID_ACCOUNT_ID_PARAMETER = 9101
internal const val CURRENCY_CODE_INVALID_ISO = 9055
internal const val NO_AVAILABLE_PAYMENT_METHODS = 9085
internal const val IMPROPERLY_CREATED_MERCHANT_ACCOUNT_CONFIG = 9073
internal const val SPECIFIED_HOSTED_FIELD_WITH_INVALID_VALUE = 9003
internal const val UNSUPPORTED_CARD_BRAND = 9125
internal const val TOKENIZATION_ALREADY_IN_PROGRESS = 9136
internal const val NO_VIEWS_IN_CARD_FORM_CONTROLLER = 9170
internal const val SDK_NOT_INITIALIZED = 9202
internal const val GENERIC_API_ERROR = 9014
internal const val PAYMENT_HANDLE_CREATION_FAILED = 9131


internal fun PaysafeException.errorName(): String = when (code) {
    INVALID_ACCOUNT_ID_FOR_PAYMENT_METHOD,
    CURRENCY_CODE_INVALID_ISO,
    IMPROPERLY_CREATED_MERCHANT_ACCOUNT_CONFIG,
    TOKENIZATION_ALREADY_IN_PROGRESS -> "CoreError"

    INVALID_ACCOUNT_ID_PARAMETER,
    NO_AVAILABLE_PAYMENT_METHODS,
    SPECIFIED_HOSTED_FIELD_WITH_INVALID_VALUE,
    UNSUPPORTED_CARD_BRAND,
    NO_VIEWS_IN_CARD_FORM_CONTROLLER -> "CardFormError"

    else -> "CardFormError"
}


internal fun invalidAccountIdForPaymentMethodException(correlationId: String) = PaysafeException(
    code = INVALID_ACCOUNT_ID_FOR_PAYMENT_METHOD,
    displayMessage = genericDisplayMessage(INVALID_ACCOUNT_ID_FOR_PAYMENT_METHOD),
    detailedMessage = "Invalid account id for CARD.",
    correlationId = correlationId
)

internal fun invalidAccountIdParameterException(correlationId: String) = PaysafeException(
    code = INVALID_ACCOUNT_ID_PARAMETER,
    displayMessage = genericDisplayMessage(INVALID_ACCOUNT_ID_PARAMETER),
    detailedMessage = "Invalid accountId parameter.",
    correlationId = correlationId
)

internal fun currencyCodeInvalidIsoException(correlationId: String) = PaysafeException(
    code = CURRENCY_CODE_INVALID_ISO,
    displayMessage = genericDisplayMessage(CURRENCY_CODE_INVALID_ISO),
    detailedMessage = "Invalid currency parameter.",
    correlationId = correlationId
)

internal fun noAvailablePaymentMethodsException(correlationId: String) = PaysafeException(
    code = NO_AVAILABLE_PAYMENT_METHODS,
    displayMessage = genericDisplayMessage(NO_AVAILABLE_PAYMENT_METHODS),
    detailedMessage = "There are no available payment methods for this API key.",
    correlationId = correlationId
)

internal fun improperlyCreatedMerchantAccountConfigException(
    correlationId: String
) = PaysafeException(
    code = IMPROPERLY_CREATED_MERCHANT_ACCOUNT_CONFIG,
    displayMessage = genericDisplayMessage(IMPROPERLY_CREATED_MERCHANT_ACCOUNT_CONFIG),
    detailedMessage = "Account not configured correctly.",
    correlationId = correlationId
)

internal fun specifiedHostedFieldWithInvalidValueException(
    vararg fields: String,
    correlationId: String
) = PaysafeException(
    code = SPECIFIED_HOSTED_FIELD_WITH_INVALID_VALUE,
    displayMessage = genericDisplayMessage(SPECIFIED_HOSTED_FIELD_WITH_INVALID_VALUE),
    detailedMessage = fieldsHaveInvalidValue(*fields),
    correlationId = correlationId
)

internal fun tokenizationAlreadyInProgressException(correlationId: String) = PaysafeException(
    code = TOKENIZATION_ALREADY_IN_PROGRESS,
    displayMessage = genericDisplayMessage(TOKENIZATION_ALREADY_IN_PROGRESS),
    detailedMessage = "Tokenization is already in progress.",
    correlationId = correlationId
)

internal fun unsupportedCardBrandException(correlationId: String) = PaysafeException(
    code = UNSUPPORTED_CARD_BRAND,
    displayMessage = genericDisplayMessage(UNSUPPORTED_CARD_BRAND),
    detailedMessage = "Unsupported card brand used.",
    correlationId = correlationId
)

internal fun noViewsInCardFormControllerException(correlationId: String) = PaysafeException(
    code = NO_VIEWS_IN_CARD_FORM_CONTROLLER,
    displayMessage = genericDisplayMessage(NO_VIEWS_IN_CARD_FORM_CONTROLLER),
    detailedMessage = "PSCardFormController doesn't own any views.",
    correlationId = correlationId
)

internal fun sdkNotInitializedException() = PaysafeException(
    code = SDK_NOT_INITIALIZED,
    displayMessage = genericDisplayMessage(SDK_NOT_INITIALIZED),
    detailedMessage = "PaysafeSDK is not initialized.",
    correlationId = ""// no correlationId in this case
)

private fun fieldsHaveInvalidValue(vararg fields: String): String {
    return if (fields.isEmpty())
        ""
    else
        fields.joinToString(separator = ", ", prefix = "Invalid fields: ", postfix = ".")
}

internal fun genericApiErrorException(correlationId: String) = PaysafeException(
    code = GENERIC_API_ERROR,
    displayMessage = genericDisplayMessage(GENERIC_API_ERROR),
    detailedMessage = "Unhandled error occurred.",
    correlationId = correlationId
)

internal fun paymentHandleCreationFailedException(status: String, correlationId: String) =
    PaysafeException(
        code = PAYMENT_HANDLE_CREATION_FAILED,
        displayMessage = genericDisplayMessage(PAYMENT_HANDLE_CREATION_FAILED),
        detailedMessage = "Status of the payment handle is $status.",
        correlationId = correlationId
    )