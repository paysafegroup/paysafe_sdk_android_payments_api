/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.googlepay

data class GooglePayPaymentToken(
    /** Api version. */
    val apiVersion: Int? = null,

    /** Api version minor. */
    val apiVersionMinor: Int? = null,

    /** Payment method data. */
    val googlePaymentMethodData: GooglePaymentMethodData? = null
)