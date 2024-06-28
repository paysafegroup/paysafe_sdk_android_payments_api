/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.venmo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VenmoRequestSerializable(

    @SerialName("consumerId")
    val consumerId: String? = null,
)
