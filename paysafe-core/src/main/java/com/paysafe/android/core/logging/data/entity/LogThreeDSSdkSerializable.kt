/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class LogThreeDSSdkSerializable(

    @SerialName("type")
    val type: String,

    @SerialName("version")
    val version: String

)
