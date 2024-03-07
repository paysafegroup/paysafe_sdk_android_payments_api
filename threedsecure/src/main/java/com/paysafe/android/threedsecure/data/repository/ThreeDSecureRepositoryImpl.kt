/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.threedsecure.data.repository

import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.threedsecure.data.api.ThreeDSecureApi
import com.paysafe.android.threedsecure.data.entity.FinalizeThreeDSAuthenticationRequest
import com.paysafe.android.threedsecure.data.mapper.toData
import com.paysafe.android.threedsecure.data.mapper.toDomain
import com.paysafe.android.threedsecure.domain.model.ThreeDSJwt
import com.paysafe.android.threedsecure.domain.model.ThreeDSJwtParams
import com.paysafe.android.threedsecure.domain.repository.ThreeDSecureRepository
import com.paysafe.android.threedsecure.exception.errorName
import com.paysafe.android.threedsecure.exception.genericApiErrorException

internal class ThreeDSecureRepositoryImpl(
    private val threeDSecureApi: ThreeDSecureApi,
    private val psApiClient: PSApiClient
) : ThreeDSecureRepository {

    override suspend fun getThreeDSJwt(
        threeDSJwtParams: ThreeDSJwtParams
    ): PSResult<ThreeDSJwt> {
        val request = threeDSJwtParams.toData()
        val response = threeDSecureApi.getThreeDSJwt(request)
        val result = when (response) {
            is PSResult.Success -> {
                try {
                    val value = response.value?.toDomain()
                    PSResult.Success(value)
                } catch (ex: Exception) {
                    val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
                    psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException, true)
                    PSResult.Failure(paysafeException)
                }
            }

            is PSResult.Failure -> response
        }
        return result
    }

    override suspend fun finalizeThreeDSAuthentication(
        authenticationId: String,
        serverJwt: String,
        accountId: String,
    ): PSResult<Unit> {
        val request = FinalizeThreeDSAuthenticationRequest(serverJwt)
        val response = threeDSecureApi.finalizeThreeDSAuthentication(
            requestBody = request,
            accountId = accountId,
            authenticationId = authenticationId
        )
        val result = when (response) {
            is PSResult.Success -> {
                try {
                    PSResult.Success()
                } catch (ex: Exception) {
                    val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
                    psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException, true)
                    PSResult.Failure(paysafeException)
                }
            }

            is PSResult.Failure -> response
        }
        return result
    }
}