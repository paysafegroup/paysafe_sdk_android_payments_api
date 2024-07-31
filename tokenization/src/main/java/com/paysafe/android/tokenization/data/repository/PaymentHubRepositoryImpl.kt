/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.repository

import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.tokenization.data.api.PaymentHubApi
import com.paysafe.android.tokenization.data.entity.paymentHandle.PaymentHandleStatusRequest
import com.paysafe.android.tokenization.data.mapper.toData
import com.paysafe.android.tokenization.data.mapper.toDomain
import com.paysafe.android.tokenization.domain.model.paymentHandle.CardRequest
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandle
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleRequest
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleStatus
import com.paysafe.android.tokenization.domain.repository.PaymentHubRepository
import com.paysafe.android.tokenization.exception.errorName
import com.paysafe.android.tokenization.exception.genericApiErrorException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Payment hub repository concrete implementation working with a payment hub api.
 */
internal class PaymentHubRepositoryImpl(
    private val paymentHubApi: PaymentHubApi,
    private val psApiClient: PSApiClient
) : PaymentHubRepository {

    /**
     * Create a payment handle using a payment hub api.
     *
     * @param paymentHandleRequest Payment handle request parameters object.
     * @param cardRequest Card data input.
     * @return Paysafe result wrapper object with payment handle model.
     */
    override suspend fun createPaymentHandle(
        paymentHandleRequest: PaymentHandleRequest,
        cardRequest: CardRequest?,
        doBeforeRequest: (String, String) -> Unit
    ): PSResult<PaymentHandle> {
        val requestSerializable = paymentHandleRequest.toData(cardRequest)
        val response = paymentHubApi.requestPaymentHandle(requestSerializable, simulatorType = paymentHandleRequest.simulatorType) { invocationId ->
            doBeforeRequest(
                Json.encodeToString(requestSerializable), invocationId
            )
        }
        val result = when (response) {
            is PSResult.Success -> {
                try {
                    val value = response.value?.toDomain()
                    PSResult.Success(value)
                } catch (ex: Exception) {
                    val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
                    psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                    PSResult.Failure(paysafeException)
                }
            }

            is PSResult.Failure -> response
        }
        return result
    }

    /**
     * Create a payment handle status using a payment hub api and token.
     *
     * @param paymentHandleToken Payment handle token.
     * @return Paysafe result wrapper object with payment handle status domain model.
     */
    override suspend fun getPaymentHandleStatus(paymentHandleToken: String): PSResult<PaymentHandleStatus> {
        val response = paymentHubApi.requestPaymentHandleStatus(
            PaymentHandleStatusRequest(paymentHandleToken)
        )
        val result = when (response) {
            is PSResult.Success -> {
                try {
                    val value = response.value?.toDomain()
                    PSResult.Success(value)
                } catch (ex: Exception) {
                    val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
                    psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                    PSResult.Failure(paysafeException)
                }
            }
            is PSResult.Failure -> response
        }
        return result
    }
}