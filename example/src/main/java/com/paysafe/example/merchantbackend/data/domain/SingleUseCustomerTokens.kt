/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.merchantbackend.data.domain

import com.paysafe.example.merchantbackend.data.domain.payment.PaymentHandle

data class SingleUseCustomerTokens(
    val singleUseCustomerToken: String? = null,
    val paymentHandles: List<PaymentHandle>? = null
)