/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.venmo.domain.model

/**
 * Structure of the Venmo configuration.
 */
data class PSVenmoConfig(

    /** Currency code. */
    val currencyCode: String,

    /** Account id for Venmo payment. */
    val accountId: String,

)
