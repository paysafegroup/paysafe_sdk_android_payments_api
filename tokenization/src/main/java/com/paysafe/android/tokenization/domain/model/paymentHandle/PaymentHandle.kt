/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle

/**
 * Payment handle data model.
 */
data class PaymentHandle(
    /** Identification account for payment. */
    val accountId: String? = null,

    /** Card bank identification number. */
    val cardBin: String? = null,

    /** Network token bank identification number. */
    val networkTokenBin: String? = null,

    /** Identification for payment handle. */
    val id: String? = null,

    /** Merchant reference number for payment. */
    val merchantRefNum: String,

    /** Token in payment handle. */
    val paymentHandleToken: String,

    /** Status for payment handle. */
    val status: String,

    /** OrderId for PayPal */
    val payPalOrderId: String? = null
)
