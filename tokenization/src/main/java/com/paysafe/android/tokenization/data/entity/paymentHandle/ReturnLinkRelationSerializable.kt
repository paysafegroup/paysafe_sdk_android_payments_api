/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ReturnLinkRelationSerializable {
    @SerialName("default")
    DEFAULT,

    @SerialName("on_completed")
    ON_COMPLETED,

    @SerialName("on_failed")
    ON_FAILED,

    @SerialName("on_cancelled")
    ON_CANCELLED
}