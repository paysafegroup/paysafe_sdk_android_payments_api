/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.domain.model.cardadapter

import com.paysafe.android.tokenization.domain.model.cardadapter.AuthenticationStatus

/**
 * CardAdapter authorization model.
 */
data class AuthenticationResponse(

    /** Authentication status */
    val status: AuthenticationStatus,

    /** Text for challenge payload */
    val sdkChallengePayload: String?
)
