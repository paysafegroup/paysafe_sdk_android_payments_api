/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.google_pay.domain.model

/**
 * Structure of the GooglePay configuration.
 */
data class PSGooglePayConfig(

    /** Country code. */
    val countryCode: String,

    /** Currency code. */
    val currencyCode: String,

    /** Account id for GooglePay payment. */
    val accountId: String,

    /** True if the billing address is required for GooglePay payment. */
    val requestBillingAddress: Boolean

)