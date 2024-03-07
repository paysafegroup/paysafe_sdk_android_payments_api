/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.merchantbackend.api

import com.paysafe.example.merchantbackend.data.response.SingleUseCustomerTokensResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface MerchantBackendService {

    @POST("paymenthub/v1/customers/{profileId}/singleusecustomertokens")
    suspend fun requestSingleUseCustomerTokens(
        @Path("profileId") profileId: String,
        @Body body: Any = Any()
    ): Response<SingleUseCustomerTokensResponse>
}