/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.detail

data class ShippingDetailsUsage(
    /** Card holder name match. */
    val cardHolderNameMatch: Boolean? = null,

    /** Initial usage date. */
    val initialUsageDate: String? = null,

    /** Initial usage range. */
    val initialUsageRange: InitialUsageRange? = null
)