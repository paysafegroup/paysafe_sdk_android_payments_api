/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.data.entity.cardAdapter


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AuthenticationRequestSerializable(

    @SerialName("deviceFingerprintingId")
    val deviceFingerprintingId: String,

    @SerialName("merchantRefNum")
    val merchantRefNum: String,

    @SerialName("process")
    val process: Boolean? = null

)