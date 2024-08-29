/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.data.mapper

import com.paysafe.android.hostedfields.data.entity.cardAdapter.AuthenticationResponseSerializable
import com.paysafe.android.hostedfields.data.entity.cardAdapter.FinalizeAuthenticationResponseSerializable
import com.paysafe.android.hostedfields.domain.model.cardadapter.AuthenticationResponse
import com.paysafe.android.hostedfields.domain.model.cardadapter.FinalizeAuthenticationResponse
import com.paysafe.android.tokenization.domain.model.cardadapter.AuthenticationStatus

internal fun AuthenticationResponseSerializable.toDomain() = AuthenticationResponse(
    sdkChallengePayload = sdkChallengePayload,
    status = AuthenticationStatus.fromString(status)
)

internal fun FinalizeAuthenticationResponseSerializable.toDomain() = FinalizeAuthenticationResponse(
    status = AuthenticationStatus.fromString(status)
)