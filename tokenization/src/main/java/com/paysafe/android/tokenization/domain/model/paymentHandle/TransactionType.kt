/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle

/**
 * This specifies the transaction type for which the Payment Handle is created. Possible values are:
 *
 * PAYMENT: Payment Handle is created to continue the Payment.
 * STANDALONE_CREDIT, ORIGINAL_CREDIT and VERIFICATION
 */
enum class TransactionType {
    PAYMENT,
    STANDALONE_CREDIT,
    ORIGINAL_CREDIT,
    VERIFICATION
}