/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.threedsecure.data.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ThreeDSJwtResponse(
    @SerialName("accountId")
    val accountId: String? = null,

    @SerialName("card")
    val card: ThreeDSCardSerializable? = null,

    @SerialName("deviceFingerprintingId")
    val deviceFingerprintingId: String? = null,

    @SerialName("id")
    val id: String? = null,

    @SerialName("jwt")
    val jwt: String? = null,

    @SerialName("sdk")
    val sdk: ThreeDSSdkSerializable? = null
)