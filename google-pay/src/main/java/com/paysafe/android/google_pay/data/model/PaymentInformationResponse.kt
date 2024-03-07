/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.google_pay.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PaymentInformationResponse(
    @SerialName("apiVersion")
    val apiVersion: Int? = null,

    @SerialName("apiVersionMinor")
    val apiVersionMinor: Int? = null,

    @SerialName("paymentMethodData")
    val paymentMethodData: PaymentMethodDataSerializable? = null,
)