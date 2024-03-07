/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class LogIntegrationTypeSerializable {

    @SerialName("PAYMENTS_API")
    PAYMENTS_API,

    @SerialName("STANDALONE_3DS")
    STANDALONE_3DS

}