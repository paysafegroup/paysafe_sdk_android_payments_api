/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.merchantbackend.data.domain.card

data class CardExpiry(

    /** Expiration month. */
    val month: String,

    /** Expiration year. */
    val year: String
)

fun CardExpiry.toUI() = "$month-$year"