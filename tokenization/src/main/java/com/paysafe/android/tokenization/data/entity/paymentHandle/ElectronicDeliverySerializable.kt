/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ElectronicDeliverySerializable(
    /** Is electronic delivery. */
    @SerialName("isElectronicDelivery")
    val isElectronicDelivery: Boolean,

    /** Email. */
    @SerialName("email")
    val email: String
)