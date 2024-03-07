/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.detail

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PriorThreeDSAuthenticationSerializable(
    /** Data. */
    @SerialName("data")
    val data: String? = null,

    /** 3DS authentication method. */
    @SerialName("method")
    val method: ThreeDSAuthenticationSerializable? = null,

    /** Id. */
    @SerialName("id")
    val id: String? = null,

    /** Time. */
    @SerialName("time")
    val time: String? = null
)