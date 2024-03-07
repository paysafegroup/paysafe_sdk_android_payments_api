/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.googlepay

data class GooglePaymentMethodData(
    /** Description. */
    val description: String? = null,

    /** Info. */
    val cardInfo: GoogleCardInfo? = null,

    /** Tokenization data. */
    val tokenizationData: GoogleTokenizationData? = null,

    /** Type. */
    val type: String? = null
)