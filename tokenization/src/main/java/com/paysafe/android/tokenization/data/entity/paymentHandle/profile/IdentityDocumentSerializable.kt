/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class IdentityDocumentSerializable(
    /** Document type. */
    @SerialName("type")
    val type: String,

    /** Document number. */
    @SerialName("documentNumber")
    val documentNumber: String
)