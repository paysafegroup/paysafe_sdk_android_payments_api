/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.googlepay

data class GoogleTokenizationData(
    /** Token. */
    val token: String? = null,

    /** Type. */
    val type: String? = null
)