/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paymentmethods.data.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PaymentMethodsResponse(
    @SerialName("paymentMethods")
    val paymentMethods: List<PaymentMethodSerializable?>? = null
)