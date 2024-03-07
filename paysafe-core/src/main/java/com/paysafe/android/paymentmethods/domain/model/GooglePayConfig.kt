/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paymentmethods.domain.model

data class GooglePayConfig(
    val merchantId: String? = null,
    val merchantName: String? = null,
    val paymentMethods: List<GoogleAuthMethod>
)