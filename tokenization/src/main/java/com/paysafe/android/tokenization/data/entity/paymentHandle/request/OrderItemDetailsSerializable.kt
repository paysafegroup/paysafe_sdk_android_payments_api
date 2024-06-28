/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderItemDetailsSerializable(

    @SerialName("preOrderItemAvailabilityDate")
    val preOrderItemAvailabilityDate: String? = null,

    @SerialName("preOrderPurchaseIndicator")
    val preOrderPurchaseIndicator: String? = null,

    @SerialName("reorderItemsIndicator")
    val reorderItemsIndicator: String? = null,

    @SerialName("shippingIndicator")
    val shippingIndicator: String? = null

)