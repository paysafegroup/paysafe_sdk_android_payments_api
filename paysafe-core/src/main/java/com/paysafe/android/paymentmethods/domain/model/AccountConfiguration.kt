/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paymentmethods.domain.model

typealias CardTypeConfig = Map<PSCreditCardType, PSCreditCardTypeCategory>

data class AccountConfiguration(
    val cardTypeConfig: CardTypeConfig? = null,
    val isGooglePay: Boolean? = null,
    val googlePayConfig: GooglePayConfig? = null,
    val clientId: String? = null
) {
    fun getAvailableCardTypes() = cardTypeConfig?.keys?.toList()?.filter {
        it != PSCreditCardType.UNKNOWN
    }

    fun isGooglePayAllowCreditCards() =
        cardTypeConfig?.values?.all { it != PSCreditCardTypeCategory.DEBIT }
}