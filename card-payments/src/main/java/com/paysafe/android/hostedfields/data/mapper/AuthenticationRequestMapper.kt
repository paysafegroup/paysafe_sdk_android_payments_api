/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.data.mapper

import com.paysafe.android.hostedfields.domain.model.cardadapter.AuthenticationRequest

internal fun AuthenticationRequest.toData(
    deviceFingerprintingId: String
) = com.paysafe.android.hostedfields.data.entity.cardAdapter.AuthenticationRequestSerializable(
    deviceFingerprintingId = deviceFingerprintingId,
    merchantRefNum = merchantRefNum,
    process = process
)
