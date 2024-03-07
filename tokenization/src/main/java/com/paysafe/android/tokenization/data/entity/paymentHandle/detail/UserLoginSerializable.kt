/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.detail

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UserLoginSerializable(
    /** Data. */
    @SerialName("data")
    val data: String? = null,

    /** Authentication method. */
    @SerialName("authenticationMethod")
    val authenticationMethod: AuthenticationMethodSerializable? = null,

    /** Time. */
    @SerialName("time")
    val time: String? = null
)