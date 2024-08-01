/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.domain.model.cardadapter

import com.paysafe.android.tokenization.domain.model.cardadapter.AuthenticationStatus

data class FinalizeAuthenticationResponse(
    val status: AuthenticationStatus?
)
