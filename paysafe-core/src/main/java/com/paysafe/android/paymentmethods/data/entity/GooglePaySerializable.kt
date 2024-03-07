/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paymentmethods.data.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GooglePaySerializable(
    @SerialName("merchantId")
    val merchantId: String? = null,

    @SerialName("merchantName")
    val merchantName: String? = null,

    @SerialName("paymentMethods")
    val paymentMethods: List<GooglePaymentMethodSerializable>? = null
)