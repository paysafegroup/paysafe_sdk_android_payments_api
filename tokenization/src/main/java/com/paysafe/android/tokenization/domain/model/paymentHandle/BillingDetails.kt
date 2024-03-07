/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle

/**
 * Billing details.
 */
data class BillingDetails(

    /** Country. */
    val country: String,

    /** Zip. */
    val zip: String,

    /** State. */
    val state: String? = null,

    /** City. */
    val city: String? = null,

    /** Street. */
    val street: String? = null,

    /** Street 1. */
    val street1: String? = null,

    /** Street 2. */
    val street2: String? = null,

    /** Phone. */
    val phone: String? = null,

    /** Nickname. */
    val nickName: String? = null

)
