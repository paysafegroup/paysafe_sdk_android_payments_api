/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.merchantbackend.data.response.payment

import com.google.gson.annotations.SerializedName
import com.paysafe.example.merchantbackend.data.domain.payment.PaymentType

enum class PaymentTypeResponse {
    @SerializedName("CARD")
    CARD,

    @SerializedName("PAYPAL")
    PAYPAL
}

fun PaymentTypeResponse.toDomain() = when (this) {
    PaymentTypeResponse.CARD -> PaymentType.CARD
    PaymentTypeResponse.PAYPAL -> PaymentType.CARD
}
