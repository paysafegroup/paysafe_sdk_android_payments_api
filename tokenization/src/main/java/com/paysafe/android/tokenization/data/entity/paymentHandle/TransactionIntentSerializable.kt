/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class TransactionIntentSerializable {
    @SerialName("GOODS_OR_SERVICE_PURCHASE")
    GOODS_OR_SERVICE_PURCHASE,

    @SerialName("CHECK_ACCEPTANCE")
    CHECK_ACCEPTANCE,

    @SerialName("ACCOUNT_FUNDING")
    ACCOUNT_FUNDING,

    @SerialName("QUASI_CASH_TRANSACTION")
    QUASI_CASH_TRANSACTION,

    @SerialName("PREPAID_ACTIVATION")
    PREPAID_ACTIVATION
}