/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Structure to organize credit card information request data.
 */
@Serializable
data class CardRequestSerializable(

    /** Credit card number. */
    @SerialName("cardNum")
    val cardNum: String? = null,

    /** Credit card expiration. */
    @SerialName("cardExpiry")
    val cardExpiry: CardExpiryRequestSerializable? = null,

    /** Credit card verification value. */
    @SerialName("cvv")
    val cvv: String? = null,

    /** Credit card holder name. */
    @SerialName("holderName")
    val holderName: String? = null

)
