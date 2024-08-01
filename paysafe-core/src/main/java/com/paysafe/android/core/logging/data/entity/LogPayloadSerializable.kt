/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging.data.entity

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = LogPayloadSerializableSerializer::class)
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

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LogPayloadSerializable::class)
object LogPayloadSerializableSerializer :
    JsonContentPolymorphicSerializer<LogPayloadSerializable>(LogPayloadSerializable::class) {
    public override fun selectDeserializer(element: JsonElement): DeserializationStrategy<LogPayloadSerializable> {
        // get type based on the values provided to SerialName for data class
        val type = element.jsonObject["type"]?.jsonPrimitive?.content
        return when (type) {
            "Message" -> LogPayloadSerializable.Message.serializer()
            "PayloadMessage" -> LogPayloadSerializable.PayloadMessage.serializer()
            else -> throw Exception("ERROR: Serializer not implemented")
        }
    }
}