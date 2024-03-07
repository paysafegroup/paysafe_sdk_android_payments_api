/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging.data.api

import com.paysafe.android.core.data.api.PaysafeApi
import com.paysafe.android.core.data.api.withBody
import com.paysafe.android.core.data.entity.PSApiRequest
import com.paysafe.android.core.data.entity.PSApiRequestType
import com.paysafe.android.core.domain.service.PSHttpClient
import com.paysafe.android.core.logging.data.entity.LogRequestSerializable
import com.paysafe.android.core.logging.data.entity.LogThreeDSRequestSerializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class LogApi(httpClient: PSHttpClient) : PaysafeApi(httpClient) {

    suspend fun logMobileEvent(
        request: LogRequestSerializable
    ) = makeRequest<Unit>(
        PSApiRequest(
            requestType = PSApiRequestType.POST,
            path = "mobile/api/v1/log"
        ).apply {
            body = Json.encodeToString(listOf(request))
        }
    )

    suspend fun logThreeDSecureEvent(
        requestBody: LogThreeDSRequestSerializable
    ) = makeRequest<Unit>(
        PSApiRequest(
            requestType = PSApiRequestType.POST,
            path = "threedsecure/v2/log"
        ).withBody(requestBody, LogThreeDSRequestSerializable.serializer())
    )

}
