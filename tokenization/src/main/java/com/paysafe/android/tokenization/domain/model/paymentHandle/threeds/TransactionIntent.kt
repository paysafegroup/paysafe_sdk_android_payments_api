/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.threeds

/**
 * This identifies the type of transaction being authenticated. This element is required only in
 * certain markets, e.g., Brazil.
 */
enum class TransactionIntent {
    GOODS_OR_SERVICE_PURCHASE,
    CHECK_ACCEPTANCE,
    ACCOUNT_FUNDING,
    QUASI_CASH_TRANSACTION,
    PREPAID_ACTIVATION
}