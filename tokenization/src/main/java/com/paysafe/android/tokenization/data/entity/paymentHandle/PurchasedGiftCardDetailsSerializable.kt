/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PurchasedGiftCardDetailsSerializable(
    @SerialName("amount")
    val amount: Int? = null,

    @SerialName("count")
    val count: Int? = null,

    @SerialName("currency")
    val currency: String? = null
)