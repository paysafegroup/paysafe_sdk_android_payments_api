/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.merchantbackend

import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.example.merchantbackend.api.MerchantBackendService
import com.paysafe.example.merchantbackend.api.Retrofit
import com.paysafe.example.merchantbackend.data.domain.SingleUseCustomerTokens
import com.paysafe.example.merchantbackend.data.response.toDomain
import com.paysafe.example.util.Consts.PROFILE_ID

class MerchantBackendRepositoryImpl : MerchantBackendRepository {

    private val service = Retrofit.buildRetrofit().create(MerchantBackendService::class.java)

    override suspend fun requestSingleUseCustomerTokens(): PSResult<SingleUseCustomerTokens> {
        val response = service.requestSingleUseCustomerTokens(PROFILE_ID)

        return if (response.isSuccessful) {
            PSResult.Success(response.body()?.toDomain())
        } else PSResult.Failure(Exception(response.message()))
    }
}