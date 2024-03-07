/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paypal.domain.model

/**
 * Structure of the PayPal configuration.
 */
data class PSPayPalConfig(

    /** Currency code. */
    val currencyCode: String,

    /** Account id for PayPal payment. */
    val accountId: String,

    /** The type to render PayPal content. */
    val renderType: PSPayPalRenderType

)
