/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paymentmethods.domain.model

data class PaymentMethod(
    val accountConfiguration: AccountConfiguration? = null,
    val accountId: String? = null,
    val currencyCode: String? = null,
    val paymentMethod: PaymentMethodType? = null,
)