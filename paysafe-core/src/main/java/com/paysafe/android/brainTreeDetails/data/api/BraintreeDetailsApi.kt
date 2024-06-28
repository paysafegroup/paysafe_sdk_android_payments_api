package com.paysafe.android.brainTreeDetails.data.api

import com.paysafe.android.brainTreeDetails.data.entity.BrainTreeDetailsResponse
import com.paysafe.android.brainTreeDetails.domain.models.BraintreeDetailsRequest
import com.paysafe.android.core.data.api.PaysafeApi
import com.paysafe.android.core.data.entity.PSApiRequest
import com.paysafe.android.core.data.entity.PSApiRequestType
import com.paysafe.android.core.domain.service.PSHttpClient
import kotlinx.serialization.encodeToString


internal class BraintreeDetailsApi(httpClient: PSHttpClient) : PaysafeApi(httpClient) {
    suspend fun getBrainTreeDetails(request: BraintreeDetailsRequest) = makeRequest<BrainTreeDetailsResponse>(
        PSApiRequest(
            requestType = PSApiRequestType.GET,
            path = "alternatepayments/venmo/v1/hostedSession/braintreeDetails",
            queryParams = mapOf(
                "payment_method_nonce" to request.paymentMethodNonce,
                "payment_method_payerInfo" to json.encodeToString(request.paymentMethodPayerInfo),
                "payment_method_jwtToken" to request.paymentMethodJwtToken,
                "payment_method_deviceData" to json.encodeToString(request.paymentMethodDeviceData),
                "errorCode" to request.errorCode
            )
        )
    )
}