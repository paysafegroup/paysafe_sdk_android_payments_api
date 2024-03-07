/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.data.error

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PSErrorResponseSerializable(
    @SerialName("error")
    val error: PSErrorSerializable? = null
)