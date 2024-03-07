/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.merchantbackend.data.response.card

import com.google.gson.annotations.SerializedName
import com.paysafe.example.merchantbackend.data.domain.card.Card

data class CardResponse(

    /** Credit card expiration. */
    @SerializedName("cardExpiry")
    val cardExpiry: CardExpiryResponse? = null,

    /** Credit card holder name. */
    @SerializedName("holderName")
    val holderName: String? = null,

    /** Credit card type. */
    @SerializedName("cardType")
    val cardType: String? = null,

    /** Card bank identification number. */
    @SerializedName("cardBin")
    val cardBin: String? = null,

    /** Credit card last digits. */
    @SerializedName("lastDigits")
    val lastDigits: String? = null,

    /** Credit card category. */
    @SerializedName("cardCategory")
    val cardCategory: String? = null,

    /** Status. */
    @SerializedName("status")
    val status: String? = null
)

fun CardResponse.toDomain() = Card(
    cardExpiry = this.cardExpiry?.toDomain(),
    holderName = this.holderName,
    cardType = this.cardType,
    cardBin = this.cardBin,
    lastDigits = this.lastDigits,
    cardCategory = this.cardCategory,
    status = this.status
)