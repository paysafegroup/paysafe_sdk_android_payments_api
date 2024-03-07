/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class LogClientInfoSerializable(

    @SerialName("correlationId")
    val correlationId: String,

    @SerialName("apikey")
    val apiKey: String,

    @SerialName("integrationType")
    val integrationType: LogIntegrationTypeSerializable,

    @SerialName("version")
    val version: String,

    @SerialName("appName")
    val appName: String

)
