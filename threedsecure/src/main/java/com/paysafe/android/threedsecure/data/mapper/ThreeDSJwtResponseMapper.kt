/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.threedsecure.data.mapper

import com.paysafe.android.threedsecure.data.entity.ThreeDSCardSerializable
import com.paysafe.android.threedsecure.data.entity.ThreeDSJwtResponse
import com.paysafe.android.threedsecure.data.entity.ThreeDSSdkSerializable
import com.paysafe.android.threedsecure.domain.model.ThreeDSCard
import com.paysafe.android.threedsecure.domain.model.ThreeDSJwt
import com.paysafe.android.threedsecure.domain.model.ThreeDSSdk
import com.paysafe.android.threedsecure.domain.model.ThreeDSSdkType

internal fun ThreeDSJwtResponse.toDomain() = ThreeDSJwt(
    accountId = this.accountId,
    card = this.card?.toDomain(),
    deviceFingerprintingId = this.deviceFingerprintingId,
    id = this.id,
    jwt = this.jwt,
    sdk = this.sdk?.toDomain()
)

internal fun ThreeDSCardSerializable.toDomain() = ThreeDSCard(
    cardBin = this.cardBin,
)

internal fun ThreeDSSdkSerializable.toDomain() = ThreeDSSdk(
    type = ThreeDSSdkType.fromType(this.type),
    version = this.version
)