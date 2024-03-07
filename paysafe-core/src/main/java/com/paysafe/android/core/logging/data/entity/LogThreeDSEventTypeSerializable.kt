/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class LogThreeDSEventTypeSerializable {

    @SerialName("INTERNAL_SDK_ERROR")
    INTERNAL_SDK_ERROR,

    @SerialName("VALIDATION_ERROR")
    VALIDATION_ERROR,

    @SerialName("SUCCESS")
    SUCCESS,

    @SerialName("NETWORK_ERROR")
    NETWORK_ERROR

}
