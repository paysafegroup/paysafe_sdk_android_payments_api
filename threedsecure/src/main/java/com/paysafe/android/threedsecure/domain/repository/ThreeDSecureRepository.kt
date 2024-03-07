/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.threedsecure.domain.repository

import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.threedsecure.domain.model.ThreeDSJwt
import com.paysafe.android.threedsecure.domain.model.ThreeDSJwtParams

internal interface ThreeDSecureRepository {

    suspend fun getThreeDSJwt(
        threeDSJwtParams: ThreeDSJwtParams
    ): PSResult<ThreeDSJwt>

    suspend fun finalizeThreeDSAuthentication(
        authenticationId: String,
        serverJwt: String,
        accountId: String,
    ): PSResult<Unit>
}