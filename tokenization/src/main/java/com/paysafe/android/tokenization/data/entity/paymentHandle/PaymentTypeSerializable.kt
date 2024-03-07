/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class PaymentTypeSerializable {
    @SerialName("CARD")
    CARD,

    @SerialName("PAYPAL")
    PAYPAL
}