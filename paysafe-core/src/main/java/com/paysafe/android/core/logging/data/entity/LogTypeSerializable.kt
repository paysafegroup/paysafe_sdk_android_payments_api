/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class LogTypeSerializable {

    @SerialName("CONVERSION")
    CONVERSION,

    @SerialName("ERROR")
    ERROR,

    @SerialName("WARN")
    WARNING

}
