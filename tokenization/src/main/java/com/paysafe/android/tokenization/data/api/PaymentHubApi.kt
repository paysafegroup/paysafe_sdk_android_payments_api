/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.api

import com.paysafe.android.core.data.api.PaysafeApi
import com.paysafe.android.core.data.api.withBody
import com.paysafe.android.core.data.entity.PSApiRequest
import com.paysafe.android.core.data.entity.PSApiRequestType
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.domain.service.PSHttpClient
import com.paysafe.android.tokenization.data.entity.paymentHandle.PaymentHandleRequestSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.PaymentHandleResponseSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.PaymentHandleStatusRequest
import com.paysafe.android.tokenization.data.entity.paymentHandle.PaymentHandleStatusResponse
import com.paysafe.android.tokenization.domain.model.paymentHandle.SimulatorType
import com.paysafe.android.tokenization.domain.repository.UniversallyUniqueId

private const val INVOCATION_ID_HEADER = "invocationId"
private const val SIMULATOR_HEADER = "Simulator"

/**
 * Structure to organize API calls to payment hub endpoints.
 *
 * @property httpClient Paysafe http client to perform web requests.
 */
internal class PaymentHubApi(
    httpClient: PSHttpClient,
    private val invocationId: UniversallyUniqueId
) : PaysafeApi(httpClient) {
    /**
     * Executes POST request to get payment handle response.
     *
     * @param requestBody Payment handle request data.
     * @return Paysafe result wrapper object with payment handle response.
     */
    suspend fun requestPaymentHandle(
        requestBody: PaymentHandleRequestSerializable,
        simulatorType: SimulatorType = SimulatorType.EXTERNAL,
        doBeforeRequest: (String) -> Unit
    ): PSResult<PaymentHandleResponseSerializable?> {
        val invocationId = invocationId.generate()
        doBeforeRequest(invocationId)
        return makeRequest<PaymentHandleResponseSerializable>(
            PSApiRequest(
                requestType = PSApiRequestType.POST,
                path = "paymenthub/v1/singleusepaymenthandles",
                headers = mapOf(
                    INVOCATION_ID_HEADER to invocationId,
                    SIMULATOR_HEADER to simulatorType.toString()
                ),
                simulator = simulatorType.toCoreModuleSimulatorType()
            ).withBody(requestBody, PaymentHandleRequestSerializable.serializer())
        )
    }

    /**
     * Executes POST request to get payment handle status response.
     *
     * @param requestBody Payment handle status request data.
     * @return Paysafe result wrapper object with payment handle status response.
     */
    suspend fun requestPaymentHandleStatus(
        requestBody: PaymentHandleStatusRequest
    ) = makeRequest<PaymentHandleStatusResponse>(
        PSApiRequest(
            requestType = PSApiRequestType.POST,
            path = "paymenthub/v1/singleusepaymenthandles/search"
        ).withBody(requestBody, PaymentHandleStatusRequest.serializer())
    )
}
