/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.google_pay.domain.model

import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType

enum class GoogleCardNetwork {
    AMEX,
    DISCOVER,
    JCB,
    MASTERCARD,
    VISA;

    companion object {
        fun fromPSCardType(cardType: PSCreditCardType) = when (cardType) {
            PSCreditCardType.VISA,
            PSCreditCardType.VISA_DEBIT,
            PSCreditCardType.VISA_ELECTRON -> VISA

            PSCreditCardType.MASTERCARD -> MASTERCARD
            PSCreditCardType.AMEX -> AMEX
            PSCreditCardType.DISCOVER -> DISCOVER
            PSCreditCardType.JCB -> JCB
            else -> null
        }
    }
}