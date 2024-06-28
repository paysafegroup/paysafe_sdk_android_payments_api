/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShippingDetailsSerializable(
    @SerialName("shipMethod")
    val shipMethod: ShippingMethodSerializable?,

    @SerialName("street")
    val street: String?,

    @SerialName("street2")
    val street2: String?,

    @SerialName("city")
    val city: String?,

    @SerialName("state")
    val state: String?,

    @SerialName("country")
    val countryCode: String?,

    @SerialName("zip")
    val zip: String?
)