/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.detail

data class PaymentAccountDetails(
    /** Created date. */
    val createdDate: String? = null,

    /** Created range. */
    val createdRange: CreatedRange? = null
)