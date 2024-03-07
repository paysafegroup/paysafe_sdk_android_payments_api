/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle

/**
 * Payment handle status data model.
 */
data class PaymentHandleStatus(
    /** Payment handle token. */
    val paymentHandleToken: String? = null,

    /** Status for payment handle token. */
    val status: PaymentHandleTokenStatus? = null
)