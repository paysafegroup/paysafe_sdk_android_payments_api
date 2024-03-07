/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Structure to organize credit card information data.
 */
@Serializable
internal data class CardResponseSerializable(

    /** Card bank identification number. */
    @SerialName("cardBin")
    val cardBin: String? = null,

    /** Network token for payment handle. */
    @SerialName("networkToken")
    val networkToken: NetworkTokenResponseSerializable? = null

)