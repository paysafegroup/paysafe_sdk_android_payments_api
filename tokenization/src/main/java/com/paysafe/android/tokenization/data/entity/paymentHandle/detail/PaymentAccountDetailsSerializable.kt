/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.detail

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentAccountDetailsSerializable(
    /** Created range. */
    @SerialName("createdRange")
    val createdRange: CreatedRangeSerializable? = null,

    /** Created date. */
    @SerialName("createdDate")
    val createdDate: String? = null
)