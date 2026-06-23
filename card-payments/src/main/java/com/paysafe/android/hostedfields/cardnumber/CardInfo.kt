package com.paysafe.android.hostedfields.cardnumber

import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType

internal data class CardInfo(
    val type: PSCreditCardType,
    val pattern: String,
    val maxLength: Int
)