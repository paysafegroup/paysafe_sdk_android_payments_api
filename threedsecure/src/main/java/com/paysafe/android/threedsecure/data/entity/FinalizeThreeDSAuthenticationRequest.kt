/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.threedsecure.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class FinalizeThreeDSAuthenticationRequest(
    @SerialName("payload")
    val payload: String?
)
