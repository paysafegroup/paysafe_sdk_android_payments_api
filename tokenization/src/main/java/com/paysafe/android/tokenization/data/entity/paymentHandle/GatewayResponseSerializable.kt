/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GatewayResponseSerializable(

    @SerialName("id")
    val orderId: String? = null,

    @SerialName("sessionToken")
    val sessionToken: String? = null,

    @SerialName("clientToken")
    val clientToken: String? = null,

    @SerialName("processor")
    val processor: String? = null

)
