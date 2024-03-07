/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle

/**
 * Card for payment handle request data model.
 */
data class CardRequest(

    /** Card number. */
    val cardNum: String? = null,

    /** Card expiration date. */
    val cardExpiry: CardExpiryRequest? = null,

    /** Card verification value. */
    val cvv: String? = null,

    /** Card holder name. */
    val holderName: String? = null

)
