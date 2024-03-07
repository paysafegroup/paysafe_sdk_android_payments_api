/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paymentmethods.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class CardTypeCategorySerializable {
    @SerialName("CREDIT")
    CREDIT,

    @SerialName("DEBIT")
    DEBIT,

    @SerialName("BOTH")
    BOTH
}