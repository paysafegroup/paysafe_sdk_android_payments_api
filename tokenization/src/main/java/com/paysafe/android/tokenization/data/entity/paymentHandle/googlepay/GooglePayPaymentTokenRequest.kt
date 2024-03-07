/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.googlepay

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GooglePayPaymentTokenRequest(
    /** Api version. */
    @SerialName("apiVersion")
    val apiVersion: Int? = null,

    /** Api version minor. */
    @SerialName("apiVersionMinor")
    val apiVersionMinor: Int? = null,

    /** Payment method data. */
    @SerialName("paymentMethodData")
    val paymentMethodData: PaymentMethodDataRequest? = null
)