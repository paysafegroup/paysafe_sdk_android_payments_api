/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class TransactionTypeSerializable {
    @SerialName("PAYMENT")
    PAYMENT,

    @SerialName("STANDALONE_CREDIT")
    STANDALONE_CREDIT,

    @SerialName("ORIGINAL_CREDIT")
    ORIGINAL_CREDIT,

    @SerialName("VERIFICATION")
    VERIFICATION
}