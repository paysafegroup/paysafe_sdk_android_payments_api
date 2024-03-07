/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BillingCycleSerializable(
    @SerialName("endDate")
    val endDate: String? = null,

    @SerialName("frequency")
    val frequency: Int? = null
)
