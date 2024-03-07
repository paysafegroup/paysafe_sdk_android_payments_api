/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.threedsecure.data.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ThreeDSJwtRequest(
    @SerialName("accountId")
    val accountId: String? = null,

    @SerialName("card")
    val threeDSCardSerializable: ThreeDSCardSerializable? = null,

    @SerialName("sdk")
    val threeDSSdkSerializable: ThreeDSSdkSerializable? = null
)