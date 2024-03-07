/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.googlepay

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PaymentMethodDataRequest(
    /** Description. */
    @SerialName("description")
    val description: String? = null,

    /** Information. */
    @SerialName("info")
    val info: InfoRequest? = null,

    /** Tokenization data. */
    @SerialName("tokenizationData")
    val tokenizationData: TokenizationDataRequest? = null,

    /** Type. */
    @SerialName("type")
    val type: String? = null
)