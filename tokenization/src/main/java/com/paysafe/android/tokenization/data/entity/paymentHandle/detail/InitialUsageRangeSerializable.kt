/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.detail

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class InitialUsageRangeSerializable {
    @SerialName("CURRENT_TRANSACTION")
    CURRENT_TRANSACTION,

    @SerialName("LESS_THAN_THIRTY_DAYS")
    LESS_THAN_THIRTY_DAYS,

    @SerialName("THIRTY_TO_SIXTY_DAYS")
    THIRTY_TO_SIXTY_DAYS,

    @SerialName("MORE_THAN_SIXTY_DAYS")
    MORE_THAN_SIXTY_DAYS
}