/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.threeds

data class BillingCycle(
    /** End date. */
    val endDate: String? = null,

    /** Frequency. */
    val frequency: Int? = null
)