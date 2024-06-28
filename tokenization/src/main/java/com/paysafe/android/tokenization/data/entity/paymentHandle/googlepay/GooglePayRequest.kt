/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.googlepay

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GooglePayRequest(
    /** Google Pay payment token. */
    @SerialName("googlePayPaymentToken")
    val googlePayPaymentToken: GooglePayPaymentTokenRequest? = null
)