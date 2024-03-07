/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Structure to organize card expiration date data.
 */
@Serializable
internal data class CardExpiryRequestSerializable(

    /** Expiration month. */
    @SerialName("month")
    val month: Int?,

    /** Expiration year. */
    @SerialName("year")
    val year: Int?

)