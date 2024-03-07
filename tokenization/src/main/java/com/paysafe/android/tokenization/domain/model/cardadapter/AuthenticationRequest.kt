/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.cardadapter

/**
 * Wrapper for the parameters needed for CardAdapter authorization.
 */
data class AuthenticationRequest(

    /** Payment handle id*/
    val paymentHandleId: String,

    /** Merchant reference number for authorization. */
    val merchantRefNum: String,

    /** This is an indicator representing whether to call authenticate end point or not. */
    val process: Boolean? = null

)