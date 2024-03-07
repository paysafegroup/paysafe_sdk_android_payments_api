/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class ProfileLocaleSerializable {
    @SerialName("ca_en")
    CA_EN,

    @SerialName("en_US")
    EN_US,

    @SerialName("fr_CA")
    FR_CA,

    @SerialName("v")
    EN_GB
}