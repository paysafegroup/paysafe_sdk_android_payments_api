/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.googlepay

/**
 * Information for Google Pay.
 */
data class GoogleCardInfo(
    /** Billing address. */
    val billingAddress: GoogleBillingAddress? = null,

    /** Card details. */
    val cardDetails: String? = null,

    /** This describes the type of card used for the request. */
    val cardNetwork: String? = null
)