package com.paysafe.android.brainTreeDetails.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceData(
    @SerialName("correlation_id")
    val correlationId: String = ""
)