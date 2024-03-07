/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.exception

import com.paysafe.android.core.domain.exception.PaysafeException
import com.paysafe.android.core.exception.genericDisplayMessage

internal const val RESPONSE_CANNOT_BE_HANDLED = 9002
internal const val PAYMENT_HANDLE_CREATION_FAILED = 9131
internal const val AMOUNT_SHOULD_BE_POSITIVE = 9054
internal const val INVALID_DYNAMIC_DESCRIPTOR = 9098
internal const val INVALID_MERCHANT_DESCRIPTOR_PHONE = 9099
internal const val PROFILE_FIRST_NAME_INVALID = 9112
internal const val PROFILE_LAST_NAME_INVALID = 9113
internal const val PROFILE_EMAIL_INVALID = 9119
internal const val GENERIC_API_ERROR = 9014


internal fun PaysafeException.errorName(): String = when (code) {
    RESPONSE_CANNOT_BE_HANDLED,
    GENERIC_API_ERROR -> "APIError"

    PAYMENT_HANDLE_CREATION_FAILED -> "CoreError"

    AMOUNT_SHOULD_BE_POSITIVE,
    INVALID_DYNAMIC_DESCRIPTOR,
    INVALID_MERCHANT_DESCRIPTOR_PHONE,
    PROFILE_FIRST_NAME_INVALID,
    PROFILE_LAST_NAME_INVALID,
    PROFILE_EMAIL_INVALID -> "CardFormError"

    else -> "CardFormError"
}


internal fun responseCannotBeHandledException(correlationId: String) = PaysafeException(
    code = RESPONSE_CANNOT_BE_HANDLED,
    displayMessage = genericDisplayMessage(RESPONSE_CANNOT_BE_HANDLED),
    detailedMessage = "Error communicating with server.",
    correlationId = correlationId
)

internal fun paymentHandleCreationFailedException(status: String, correlationId: String) =
    PaysafeException(
        code = PAYMENT_HANDLE_CREATION_FAILED,
        displayMessage = genericDisplayMessage(PAYMENT_HANDLE_CREATION_FAILED),
        detailedMessage = "Status of the payment handle is $status.",
        correlationId = correlationId
    )

internal fun amountShouldBePositiveException(correlationId: String) = PaysafeException(
    code = AMOUNT_SHOULD_BE_POSITIVE,
    displayMessage = genericDisplayMessage(AMOUNT_SHOULD_BE_POSITIVE),
    detailedMessage = "Amount should be a number greater than 0 no longer than 11 characters.",
    correlationId = correlationId
)

internal fun invalidDynamicDescriptorException(correlationId: String) = PaysafeException(
    code = INVALID_DYNAMIC_DESCRIPTOR,
    displayMessage = genericDisplayMessage(INVALID_DYNAMIC_DESCRIPTOR),
    detailedMessage = "Invalid parameter in merchantDescriptor.dynamicDescriptor.",
    correlationId = correlationId
)

internal fun invalidMerchantDescriptorPhoneException(correlationId: String) = PaysafeException(
    code = INVALID_MERCHANT_DESCRIPTOR_PHONE,
    displayMessage = genericDisplayMessage(INVALID_MERCHANT_DESCRIPTOR_PHONE),
    detailedMessage = "Invalid parameter in merchantDescriptor.phone.",
    correlationId = correlationId
)

internal fun profileFirstNameInvalidException(correlationId: String) = PaysafeException(
    code = PROFILE_FIRST_NAME_INVALID,
    displayMessage = genericDisplayMessage(PROFILE_FIRST_NAME_INVALID),
    detailedMessage = "Profile firstName should be valid.",
    correlationId = correlationId
)

internal fun profileLastNameInvalidException(correlationId: String) = PaysafeException(
    code = PROFILE_LAST_NAME_INVALID,
    displayMessage = genericDisplayMessage(PROFILE_LAST_NAME_INVALID),
    detailedMessage = "Profile lastName should be valid.",
    correlationId = correlationId
)

internal fun profileEmailInvalidException(correlationId: String) = PaysafeException(
    code = PROFILE_EMAIL_INVALID,
    displayMessage = genericDisplayMessage(PROFILE_EMAIL_INVALID),
    detailedMessage = "Profile email should be valid.",
    correlationId = correlationId
)

internal fun genericApiErrorException(correlationId: String) = PaysafeException(
    code = GENERIC_API_ERROR,
    displayMessage = genericDisplayMessage(GENERIC_API_ERROR),
    detailedMessage = "Unhandled error occurred.",
    correlationId = correlationId
)