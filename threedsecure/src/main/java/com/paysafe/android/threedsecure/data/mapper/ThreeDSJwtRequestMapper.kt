/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.threedsecure.data.mapper

import com.paysafe.android.threedsecure.data.entity.ThreeDSCardSerializable
import com.paysafe.android.threedsecure.data.entity.ThreeDSJwtRequest
import com.paysafe.android.threedsecure.data.entity.ThreeDSSdkSerializable
import com.paysafe.android.threedsecure.domain.model.ThreeDSCard
import com.paysafe.android.threedsecure.domain.model.ThreeDSJwtParams
import com.paysafe.android.threedsecure.domain.model.ThreeDSSdk

internal fun ThreeDSJwtParams.toData() = ThreeDSJwtRequest(
    accountId = this.accountId,
    threeDSCardSerializable = this.threeDSCard?.toData(),
    threeDSSdkSerializable = this.threeDSSdk?.toData()
)


internal fun ThreeDSSdk.toData() = ThreeDSSdkSerializable(
    type = this.type?.value,
    version = this.version
)

internal fun ThreeDSCard.toData() = ThreeDSCardSerializable(
    cardBin = this.cardBin,
)