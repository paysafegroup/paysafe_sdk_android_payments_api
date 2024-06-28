package com.paysafe.android.brainTreeDetails.data.repository

import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.brainTreeDetails.data.api.BraintreeDetailsApi
import com.paysafe.android.brainTreeDetails.data.entity.BrainTreeDetailsResponse
import com.paysafe.android.brainTreeDetails.domain.models.BraintreeDetailsRequest
import com.paysafe.android.brainTreeDetails.domain.repository.BrainTreeDetailsRepository

internal class BrainTreeDetailsRepositoryImpl(
    private val brainTreeDetailsApi: BraintreeDetailsApi
) : BrainTreeDetailsRepository {
    override suspend fun getBrainTreeDetails(braintreeDetailsRequest: BraintreeDetailsRequest): PSResult<BrainTreeDetailsResponse> {
        val result = when (val response = brainTreeDetailsApi.getBrainTreeDetails(braintreeDetailsRequest)) {
            is PSResult.Success -> {
                try {
                    val result = response.value
                    PSResult.Success(result)
                } catch (ex: Exception) {
                    PSResult.Failure(ex)
                }
            }

            is PSResult.Failure -> response
        }
        return result
    }
}
