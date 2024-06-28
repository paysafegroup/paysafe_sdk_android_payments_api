/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class AuthenticationPurposeSerializable {
    @SerialName("PAYMENT_TRANSACTION")
    PAYMENT_TRANSACTION,

    @SerialName("RECURRING_TRANSACTION")
    RECURRING_TRANSACTION,

    @SerialName("INSTALMENT_TRANSACTION")
    INSTALMENT_TRANSACTION,

    @SerialName("ADD_CARD")
    ADD_CARD,

    @SerialName("MAINTAIN_CARD")
    MAINTAIN_CARD,

    @SerialName("EMV_TOKEN_VERIFICATION")
    EMV_TOKEN_VERIFICATION
}