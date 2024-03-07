/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.merchantbackend.data.domain.payment

import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType
import com.paysafe.example.merchantbackend.data.domain.card.Card
import com.paysafe.example.merchantbackend.data.domain.card.toUI
import com.paysafe.example.savedcard.UiSavedCardData

data class PaymentHandle(
    /** Identification for payment handle response. */
    val id: String? = null,

    /** Status for payment handle response. */
    val status: String? = null,

    /** Usage associated for payment. */
    val usage: String? = null,

    /** Payment type for handle request. */
    val paymentType: PaymentType? = null,

    /** Token returned in payment handle. */
    val paymentHandleToken: String? = null,

    /** Credit card data for payment handle. */
    val card: Card? = null,

    val billingDetailsId: String? = null,

    val multiUsePaymentHandleId: String? = null
)

fun PaymentHandle.toUI(singleUseCustomerToken: String?) = UiSavedCardData(
    cardBrandRes(card?.cardType ?: ""),
    cardBrandType(card?.cardType ?: ""),
    card?.lastDigits ?: "0000",
    card?.holderName ?: "Holder Name",
    card?.cardExpiry?.month ?: "12",
    card?.cardExpiry?.year ?: "2099",
    card?.cardExpiry?.toUI() ?: "12-2099",
    paymentHandleToken ?: "Cmfy9rokKZRyFmI",
    singleUseCustomerToken ?: "SP5PhDcXzlI8qEoP"
)

private fun cardBrandRes(cardType: String) = when (cardType) {
    PSCreditCardType.MASTERCARD.value -> com.paysafe.android.hostedfields.R.drawable.ic_cc_mastercard
    PSCreditCardType.VISA.value -> com.paysafe.android.hostedfields.R.drawable.ic_cc_visa
    PSCreditCardType.AMEX.value -> com.paysafe.android.hostedfields.R.drawable.ic_cc_amex
    else -> com.paysafe.android.hostedfields.R.drawable.ic_cc_discover
}

private fun cardBrandType(cardType: String) = when (cardType) {
    PSCreditCardType.MASTERCARD.value -> PSCreditCardType.MASTERCARD
    PSCreditCardType.VISA.value -> PSCreditCardType.VISA
    PSCreditCardType.AMEX.value -> PSCreditCardType.AMEX
    PSCreditCardType.DISCOVER.value -> PSCreditCardType.DISCOVER
    else -> PSCreditCardType.UNKNOWN
}