/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.data.api

import com.paysafe.android.core.data.api.PaysafeApi
import com.paysafe.android.core.data.api.withBody
import com.paysafe.android.core.data.entity.PSApiRequest
import com.paysafe.android.core.data.entity.PSApiRequestType
import com.paysafe.android.core.domain.service.PSHttpClient
import com.paysafe.android.hostedfields.data.entity.cardAdapter.AuthenticationRequestSerializable
import com.paysafe.android.hostedfields.data.entity.cardAdapter.AuthenticationResponseSerializable
import com.paysafe.android.hostedfields.data.entity.cardAdapter.FinalizeAuthenticationResponseSerializable


internal class CardAdapterAuthApi(httpClient: PSHttpClient) : PaysafeApi(httpClient) {

    suspend fun startAuthentication(
        requestBody: AuthenticationRequestSerializable,
        paymentHandleId: String
    ) = makeRequest<AuthenticationResponseSerializable>(
        PSApiRequest(
            requestType = PSApiRequestType.POST,
            path = "cardadapter/v1/paymenthandles/$paymentHandleId/authentications",
        ).withBody(requestBody, AuthenticationRequestSerializable.serializer())
    )

    suspend fun finalizeAuthentication(paymentHandleId: String, authenticationId: String) =
        makeRequest<FinalizeAuthenticationResponseSerializable>(
            PSApiRequest(
                requestType = PSApiRequestType.POST,
                path = "cardadapter/v1/paymenthandles/$paymentHandleId/authentications/${authenticationId}/finalize",
            )
        )
}