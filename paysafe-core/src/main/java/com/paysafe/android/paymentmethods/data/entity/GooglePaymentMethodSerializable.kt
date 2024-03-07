/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paymentmethods.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class GooglePaymentMethodSerializable {
    @SerialName("CARDS")
    CARDS,

    @SerialName("TOKENIZED_CARDS")
    TOKENIZED_CARDS
}