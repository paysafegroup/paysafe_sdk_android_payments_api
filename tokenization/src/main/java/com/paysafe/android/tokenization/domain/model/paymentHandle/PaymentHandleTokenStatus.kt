/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle

/**
 * Payment handle token statuses model.
 *
 * @property status Text associated with the four possible token statuses.
 */
enum class PaymentHandleTokenStatus(val status: String) {
    /** Process to get token was initiated. */
    INITIATED("INITIATED"),

    /** Token is ready, it's payable. */
    PAYABLE("PAYABLE"),

    /** Process to get token failed. */
    FAILED("FAILED"),

    /** Token has expired; request another one. */
    EXPIRED("EXPIRED"),

    PROCESSING("PROCESSING"),

    COMPLETED("COMPLETED");
}