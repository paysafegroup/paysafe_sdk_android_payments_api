/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.data.entity.cardAdapter


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class FinalizeAuthenticationResponseSerializable(

    @SerialName("status")
    val status: String? = null

)