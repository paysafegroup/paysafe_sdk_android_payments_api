/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.paypal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PayPalRequestSerializable(

    @SerialName("consumerId")
    val consumerId: String,

    @SerialName("recipientDescription")
    val recipientDescription: String?,

    @SerialName("language")
    val language: PayPalLanguageSerializable?,

    @SerialName("consumerMessage")
    val consumerMessage: String?,

    @SerialName("orderDescription")
    val orderDescription: String?,

    @SerialName("shippingPreference")
    val shippingPreference: PayPalShippingPreferenceSerializable?,

    @SerialName("recipientType")
    val recipientType: String?

)
