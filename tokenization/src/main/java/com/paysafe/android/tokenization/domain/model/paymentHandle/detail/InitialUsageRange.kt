/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.detail

/**
 * This is the length of time between the first use of this shipping address and the current transaction.
 */
enum class InitialUsageRange {
    CURRENT_TRANSACTION,
    LESS_THAN_THIRTY_DAYS,
    THIRTY_TO_SIXTY_DAYS,
    MORE_THAN_SIXTY_DAYS
}