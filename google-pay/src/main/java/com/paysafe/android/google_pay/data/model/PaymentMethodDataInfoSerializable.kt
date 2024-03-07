/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.google_pay.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PaymentMethodDataInfoSerializable(
    @SerialName("billingAddress")
    val billingAddress: BillingAddressSerializable? = null,

    @SerialName("cardDetails")
    val cardDetails: String? = null,

    @SerialName("cardNetwork")
    val cardNetwork: String? = null
)