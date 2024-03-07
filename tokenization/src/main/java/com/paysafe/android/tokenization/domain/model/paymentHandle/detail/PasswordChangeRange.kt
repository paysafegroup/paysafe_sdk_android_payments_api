/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.detail

enum class PasswordChangeRange {
    MORE_THAN_SIXTY_DAYS,
    NO_CHANGE,
    DURING_TRANSACTION,
    LESS_THAN_THIRTY_DAYS,
    THIRTY_TO_SIXTY_DAYS
}