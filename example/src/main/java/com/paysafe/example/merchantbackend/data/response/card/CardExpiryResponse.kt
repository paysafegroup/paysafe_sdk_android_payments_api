/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.merchantbackend.data.response.card

import com.google.gson.annotations.SerializedName
import com.paysafe.example.merchantbackend.data.domain.card.CardExpiry

data class CardExpiryResponse(

    /** Expiration month. */
    @SerializedName("month")
    val month: String,

    /** Expiration year. */
    @SerializedName("year")
    val year: String
)

fun CardExpiryResponse.toDomain() = CardExpiry(
    month = this.month,
    year = this.year
)
