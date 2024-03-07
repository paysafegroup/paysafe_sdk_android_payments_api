/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.detail

data class UserLogin(
    /** Data. */
    val data: String? = null,

    /** Authentication method. */
    val authenticationMethod: AuthenticationMethod? = null,

    /** Time. */
    val time: String? = null
)