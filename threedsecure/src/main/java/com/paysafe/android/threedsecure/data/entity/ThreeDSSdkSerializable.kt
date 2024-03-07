/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.threedsecure.data.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ThreeDSSdkSerializable(
    @SerialName("type")
    val type: String? = null,

    @SerialName("version")
    val version: String? = null
)