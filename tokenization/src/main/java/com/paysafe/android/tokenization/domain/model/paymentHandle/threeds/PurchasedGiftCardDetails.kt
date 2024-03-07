/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.threeds

data class PurchasedGiftCardDetails(
    /** Amount. */
    val amount: Int? = null,

    /** Count. */
    val count: Int? = null,

    /** Currency. */
    val currency: String? = null
)