/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle

/**
 * Card expiration for payment handle request data model.
 */
data class CardExpiryRequest(

    /** Expiration month. */
    val month: String,

    /** Expiration year. */
    val year: String

)
