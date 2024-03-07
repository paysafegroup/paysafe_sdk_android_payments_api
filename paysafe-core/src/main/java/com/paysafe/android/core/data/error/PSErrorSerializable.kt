/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.data.error

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PSErrorSerializable(
    @SerialName("code")
    val code: String? = null,

    @SerialName("message")
    val message: String? = null,

    @SerialName("details")
    val details: List<String>? = null

)