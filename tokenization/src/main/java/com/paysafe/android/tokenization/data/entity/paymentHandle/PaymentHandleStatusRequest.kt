/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Structure to organize payment handle token for status request.
 */
@Serializable
data class PaymentHandleStatusRequest(
    /** Payment handle token. */
    @SerialName("paymentHandleToken")
    val paymentHandleToken: String? = null
)