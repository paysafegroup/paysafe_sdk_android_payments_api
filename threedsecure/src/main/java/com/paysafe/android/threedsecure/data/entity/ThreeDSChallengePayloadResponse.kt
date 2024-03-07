/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.threedsecure.data.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ThreeDSChallengePayloadResponse(
    @SerialName("accountId")
    val accountId: String? = null,

    @SerialName("acsUrl")
    val acsUrl: String? = null,

    @SerialName("cardBin")
    val cardBin: String? = null,

    @SerialName("cardType")
    val cardType: String? = null,

    @SerialName("id")
    val id: String? = null,

    @SerialName("payload")
    val payload: String? = null,

    @SerialName("threeDSecureVersion")
    val threeDSecureVersion: String? = null,

    @SerialName("transactionId")
    val transactionId: String? = null
)