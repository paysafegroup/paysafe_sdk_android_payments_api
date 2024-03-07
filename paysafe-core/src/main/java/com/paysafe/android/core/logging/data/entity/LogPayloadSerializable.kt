/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class LogPayloadSerializable {

    @Serializable
    @SerialName("Message")
    internal data class Message(

        @SerialName("message")
        val message: String

    ) : LogPayloadSerializable()

    @Serializable
    @SerialName("PayloadMessage")
    internal data class PayloadMessage(

        @SerialName("message")
        val message: LogErrorMessageSerializable

    ) : LogPayloadSerializable()

}
