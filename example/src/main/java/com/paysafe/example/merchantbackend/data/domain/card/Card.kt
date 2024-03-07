/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.merchantbackend.data.domain.card

data class Card(
    /** Credit card expiration. */
    val cardExpiry: CardExpiry? = null,

    /** Credit card holder name. */
    val holderName: String? = null,

    /** Credit card type. */
    val cardType: String? = null,

    /** Card bank identification number. */
    val cardBin: String? = null,

    /** Credit card last digits. */
    val lastDigits: String? = null,

    /** Credit card category. */
    val cardCategory: String? = null,

    /** Status. */
    val status: String? = null
)