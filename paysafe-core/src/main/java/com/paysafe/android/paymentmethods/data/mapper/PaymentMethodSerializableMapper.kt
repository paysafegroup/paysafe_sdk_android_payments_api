/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paymentmethods.data.mapper

import com.paysafe.android.paymentmethods.data.entity.AccountConfigurationSerializable
import com.paysafe.android.paymentmethods.data.entity.CardTypeCategorySerializable
import com.paysafe.android.paymentmethods.data.entity.CardTypeConfigSerializable
import com.paysafe.android.paymentmethods.data.entity.GooglePaySerializable
import com.paysafe.android.paymentmethods.data.entity.GooglePaymentMethodSerializable
import com.paysafe.android.paymentmethods.data.entity.PaymentMethodSerializable
import com.paysafe.android.paymentmethods.data.entity.PaymentMethodTypeSerializable
import com.paysafe.android.paymentmethods.data.entity.PaymentMethodsResponse
import com.paysafe.android.paymentmethods.domain.model.AccountConfiguration
import com.paysafe.android.paymentmethods.domain.model.GoogleAuthMethod
import com.paysafe.android.paymentmethods.domain.model.GooglePayConfig
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardTypeCategory
import com.paysafe.android.paymentmethods.domain.model.PaymentMethod
import com.paysafe.android.paymentmethods.domain.model.PaymentMethodType


internal fun PaymentMethodsResponse.toDomain() = paymentMethods.orEmpty()
    .filterNotNull()
    .map { it.toDomain() }

internal fun PaymentMethodSerializable.toDomain() = PaymentMethod(
    accountId = accountId,
    currencyCode = currencyCode,
    paymentMethod = paymentMethod?.toDomain(),
    accountConfiguration = accountConfiguration?.toDomain(),
)

internal fun AccountConfigurationSerializable.toDomain() = AccountConfiguration(
    cardTypeConfig = cardTypeConfig?.toDomain(),
    isGooglePay = isGooglePay,
    googlePayConfig = googlePay?.toDomain(),
    clientId = clientId
)

internal fun GooglePaySerializable.toDomain() = GooglePayConfig(
    merchantId = merchantId,
    merchantName = merchantName,
    paymentMethods = paymentMethods?.map { it.toDomain() } ?: listOf(GoogleAuthMethod.PAN_ONLY),
)

internal fun GooglePaymentMethodSerializable.toDomain() = when (this) {
    GooglePaymentMethodSerializable.CARDS -> GoogleAuthMethod.PAN_ONLY
    GooglePaymentMethodSerializable.TOKENIZED_CARDS -> GoogleAuthMethod.CRYPTOGRAM_3DS
}

internal fun CardTypeConfigSerializable.toDomain() = mapOf(
    PSCreditCardType.AMEX to am,
    PSCreditCardType.MASTERCARD to mc,
    PSCreditCardType.VISA to vi,
    PSCreditCardType.DISCOVER to di,
    PSCreditCardType.JCB to jc,
    PSCreditCardType.MAESTRO to md,
    PSCreditCardType.SOLO to so,
    PSCreditCardType.VISA_DEBIT to vd,
    PSCreditCardType.VISA_ELECTRON to ve
).filter { it.value != null }.mapValues { it.value!!.toDomain() }

internal fun CardTypeCategorySerializable.toDomain() = when (this) {
    CardTypeCategorySerializable.CREDIT -> PSCreditCardTypeCategory.CREDIT
    CardTypeCategorySerializable.DEBIT -> PSCreditCardTypeCategory.DEBIT
    CardTypeCategorySerializable.BOTH -> PSCreditCardTypeCategory.BOTH
}

// As the SDK Grows it will add more payment methods as for now the only supported ones are CARD and VENMO
internal fun PaymentMethodTypeSerializable?.toDomain() = when (this) {
    PaymentMethodTypeSerializable.CARD -> PaymentMethodType.CARD
    PaymentMethodTypeSerializable.VENMO -> PaymentMethodType.VENMO
    else -> PaymentMethodType.UNSUPPORTED
}
