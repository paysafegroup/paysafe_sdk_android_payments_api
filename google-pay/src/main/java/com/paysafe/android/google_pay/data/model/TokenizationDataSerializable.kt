/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.google_pay.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TokenizationDataSerializable(
    @SerialName("token")
    val token: String? = null,

    @SerialName("type")
    val type: String? = null
)