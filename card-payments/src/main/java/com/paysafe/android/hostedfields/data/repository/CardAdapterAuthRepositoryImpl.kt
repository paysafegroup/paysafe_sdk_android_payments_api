/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.data.repository

import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.hostedfields.data.api.CardAdapterAuthApi
import com.paysafe.android.hostedfields.data.mapper.toData
import com.paysafe.android.hostedfields.data.mapper.toDomain
import com.paysafe.android.hostedfields.domain.model.cardadapter.AuthenticationRequest
import com.paysafe.android.hostedfields.domain.model.cardadapter.AuthenticationResponse
import com.paysafe.android.hostedfields.domain.model.cardadapter.FinalizeAuthenticationResponse
import com.paysafe.android.hostedfields.domain.repository.CardAdapterAuthRepository
import com.paysafe.android.hostedfields.exception.errorName
import com.paysafe.android.hostedfields.exception.genericApiErrorException



internal class CardAdapterAuthRepositoryImpl(
    private val cardAdapterAuthApi: CardAdapterAuthApi,
    private val psApiClient: PSApiClient
) : CardAdapterAuthRepository {

    override suspend fun startAuthentication(
        authenticationRequest: AuthenticationRequest,
        deviceFingerprintingId: String
    ): PSResult<AuthenticationResponse> {
        val request = authenticationRequest.toData(deviceFingerprintingId)
        val response =
            cardAdapterAuthApi.startAuthentication(request, authenticationRequest.paymentHandleId)
        val result = when (response) {
            is PSResult.Success -> {
                try {
                    PSResult.Success(response.value?.toDomain())
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

    override suspend fun finalizeAuthentication(
        paymentHandleId: String,
        authenticationId: String
    ): PSResult<FinalizeAuthenticationResponse> {
        val response = cardAdapterAuthApi.finalizeAuthentication(paymentHandleId, authenticationId)
        val result = when (response) {
            is PSResult.Success -> {
                try {
                    PSResult.Success(response.value?.toDomain())
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