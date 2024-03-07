/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class LogErrorMessageSerializable(

    @SerialName("code")
    val code: String,

    @SerialName("detailedMessage")
    val detailedMessage: String,

    @SerialName("displayMessage")
    val displayMessage: String,

    @SerialName("name")
    val name: String,

    @SerialName("message")
    val message: String

)
