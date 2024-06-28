/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.detail

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShippingDetailsUsageSerializable(
    /** Card holder name match. */
    @SerialName("cardHolderNameMatch")
    val cardHolderNameMatch: Boolean? = null,

    /** Initial usage date. */
    @SerialName("initialUsageDate")
    val initialUsageDate: String? = null,

    /** Initial usage range. */
    @SerialName("initialUsageRange")
    val initialUsageRange: InitialUsageRangeSerializable? = null
)