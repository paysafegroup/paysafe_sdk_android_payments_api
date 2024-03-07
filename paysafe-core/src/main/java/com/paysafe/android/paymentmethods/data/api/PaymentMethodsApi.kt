/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paymentmethods.data.api

import com.paysafe.android.core.data.api.PaysafeApi
import com.paysafe.android.core.data.entity.PSApiRequest
import com.paysafe.android.core.data.entity.PSApiRequestType
import com.paysafe.android.core.domain.service.PSHttpClient
import com.paysafe.android.paymentmethods.data.entity.PaymentMethodsResponse

internal class PaymentMethodsApi(httpClient: PSHttpClient) : PaysafeApi(httpClient) {
    suspend fun getPaymentMethods(code: String) = makeRequest<PaymentMethodsResponse>(
        PSApiRequest(
            requestType = PSApiRequestType.GET,
            path = "paymenthub/v1/paymentmethods",
            queryParams = mapOf("currencyCode" to code)
        )
    )
}