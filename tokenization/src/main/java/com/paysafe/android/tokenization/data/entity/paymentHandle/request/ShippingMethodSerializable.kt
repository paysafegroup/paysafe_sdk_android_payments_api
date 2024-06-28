/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ShippingMethodSerializable {
    @SerialName("N")
    NEXT_DAY_OR_OVERNIGHT,

    @SerialName("T")
    TWO_DAY_SERVICE,

    @SerialName("C")
    LOWEST_COST,

    @SerialName("O")
    OTHER
}