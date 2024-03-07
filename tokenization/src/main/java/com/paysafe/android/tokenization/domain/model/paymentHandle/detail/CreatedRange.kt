/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.detail

/**
 * This is the length of time between the most recent change to the cardholder’s account information
 * and the API call of the current transaction.
 */
enum class CreatedRange {
    DURING_TRANSACTION,
    NO_ACCOUNT,
    LESS_THAN_THIRTY_DAYS,
    THIRTY_TO_SIXTY_DAYS,
    MORE_THAN_SIXTY_DAYS
}