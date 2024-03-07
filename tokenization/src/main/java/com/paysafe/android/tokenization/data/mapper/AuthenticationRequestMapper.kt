/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.mapper

import com.paysafe.android.tokenization.data.entity.cardadapter.AuthenticationRequestSerializable
import com.paysafe.android.tokenization.domain.model.cardadapter.AuthenticationRequest

internal fun AuthenticationRequest.toData(
    deviceFingerprintingId: String
) = AuthenticationRequestSerializable(
    deviceFingerprintingId = deviceFingerprintingId,
    merchantRefNum = merchantRefNum,
    process = process
)
