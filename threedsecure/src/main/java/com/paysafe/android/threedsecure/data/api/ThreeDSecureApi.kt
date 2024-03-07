/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.threedsecure.data.api

import com.paysafe.android.core.data.api.PaysafeApi
import com.paysafe.android.core.data.api.withBody
import com.paysafe.android.core.data.entity.PSApiRequest
import com.paysafe.android.core.data.entity.PSApiRequestType
import com.paysafe.android.core.domain.service.PSHttpClient
import com.paysafe.android.threedsecure.data.entity.FinalizeThreeDSAuthenticationRequest
import com.paysafe.android.threedsecure.data.entity.ThreeDSJwtRequest
import com.paysafe.android.threedsecure.data.entity.ThreeDSJwtResponse

internal class ThreeDSecureApi(httpClient: PSHttpClient) : PaysafeApi(httpClient) {

    suspend fun getThreeDSJwt(requestBody: ThreeDSJwtRequest) =
        makeRequest<ThreeDSJwtResponse>(
            PSApiRequest(
                requestType = PSApiRequestType.POST,
                path = "threedsecure/v2/jwt",
            ).withBody(requestBody, ThreeDSJwtRequest.serializer())
        )

    suspend fun finalizeThreeDSAuthentication(
        requestBody: FinalizeThreeDSAuthenticationRequest,
        accountId: String,
        authenticationId: String
    ) = makeRequest<Unit>(
            PSApiRequest(
                requestType = PSApiRequestType.POST,
                path = "threedsecure/v2/accounts/$accountId/authentications/${authenticationId}/finalize",
            ).withBody(requestBody, FinalizeThreeDSAuthenticationRequest.serializer())
        )

}