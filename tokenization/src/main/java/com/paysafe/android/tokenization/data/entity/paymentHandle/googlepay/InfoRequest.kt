/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.googlepay

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InfoRequest(
    /** Billing address. */
    @SerialName("billingAddress")
    val billingAddress: BillingAddressRequest? = null,

    /** Card details. */
    @SerialName("cardDetails")
    val cardDetails: String? = null,

    /** Card network. */
    @SerialName("cardNetwork")
    val cardNetwork: String? = null
)