/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paymentmethods.data.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PaymentMethodSerializable(
    @SerialName("accountConfiguration")
    val accountConfiguration: AccountConfigurationSerializable? = null,

    @SerialName("accountId")
    val accountId: String? = null,

    @SerialName("currencyCode")
    val currencyCode: String? = null,

    @SerialName("paymentMethod")
    val paymentMethod: PaymentMethodTypeSerializable? = null
)