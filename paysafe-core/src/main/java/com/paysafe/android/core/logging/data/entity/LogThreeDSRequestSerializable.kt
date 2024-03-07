/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class LogThreeDSRequestSerializable(

    @SerialName("eventType")
    val eventType: LogThreeDSEventTypeSerializable,

    @SerialName("eventMessage")
    val eventMessage: String,

    @SerialName("sdk")
    val sdk: LogThreeDSSdkSerializable

)
