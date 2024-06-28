/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.googlepay

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenizationDataRequest(
    /** Token. */
    @SerialName("token")
    val token: String? = null,

    /** Type. */
    @SerialName("type")
    val type: String? = null
)