/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.mapper

import com.paysafe.android.tokenization.data.entity.cardadapter.AuthenticationResponseSerializable
import com.paysafe.android.tokenization.data.entity.cardadapter.FinalizeAuthenticationResponseSerializable
import com.paysafe.android.tokenization.domain.model.cardadapter.AuthenticationResponse
import com.paysafe.android.tokenization.domain.model.cardadapter.AuthenticationStatus
import com.paysafe.android.tokenization.domain.model.cardadapter.FinalizeAuthenticationResponse

internal fun AuthenticationResponseSerializable.toDomain() = AuthenticationResponse(
    sdkChallengePayload = sdkChallengePayload,
    status = AuthenticationStatus.fromString(status)
)

internal fun FinalizeAuthenticationResponseSerializable.toDomain() = FinalizeAuthenticationResponse(
    status = AuthenticationStatus.fromString(status)
)