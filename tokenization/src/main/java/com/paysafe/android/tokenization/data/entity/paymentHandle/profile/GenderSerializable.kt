/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class GenderSerializable {

    @SerialName("M")
    MALE,

    @SerialName("F")
    FEMALE

}
