/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class LogRequestSerializable(

    @SerialName("type")
    val type: LogTypeSerializable,

    @SerialName("clientInfo")
    val clientInfo: LogClientInfoSerializable,

    @SerialName("payload")
    val payload: LogPayloadSerializable

)
