/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.repository

import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.tokenization.domain.model.cardadapter.AuthenticationRequest
import com.paysafe.android.tokenization.domain.model.cardadapter.AuthenticationResponse
import com.paysafe.android.tokenization.domain.model.cardadapter.FinalizeAuthenticationResponse

internal interface CardAdapterAuthRepository {

    suspend fun startAuthentication(
        authenticationRequest: AuthenticationRequest,
        deviceFingerprintingId: String
    ): PSResult<AuthenticationResponse>

    suspend fun finalizeAuthentication(
        paymentHandleId: String,
        authenticationId: String
    ): PSResult<FinalizeAuthenticationResponse>
}