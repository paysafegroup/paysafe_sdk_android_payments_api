/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle

/**
 * This is the link type that allows different endpoints to be targeted depending
 * on the end state of the transaction.
 */
enum class ReturnLinkRelation {
    DEFAULT,
    ON_COMPLETED,
    ON_FAILED,
    ON_CANCELLED
}