/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Structure to organize payment handle status response.
 */
@Serializable
internal data class PaymentHandleStatusResponse(
    /** Status for payment handle response. */
    @SerialName("status")
    val status: String? = null,

    /** Payment handle token. */
    @SerialName("paymentHandleToken")
    val paymentHandleToken: String? = null
)