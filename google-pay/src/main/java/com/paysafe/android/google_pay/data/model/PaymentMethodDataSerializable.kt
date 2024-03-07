/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.google_pay.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PaymentMethodDataSerializable(
    @SerialName("description")
    val description: String? = null,

    @SerialName("info")
    val info: PaymentMethodDataInfoSerializable? = null,

    @SerialName("tokenizationData")
    val tokenizationData: TokenizationDataSerializable? = null,

    @SerialName("type")
    val type: String? = null
)