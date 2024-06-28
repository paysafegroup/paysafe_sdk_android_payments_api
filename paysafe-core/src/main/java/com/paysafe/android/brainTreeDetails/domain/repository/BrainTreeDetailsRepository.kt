package com.paysafe.android.brainTreeDetails.domain.repository


import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.brainTreeDetails.data.entity.BrainTreeDetailsResponse
import com.paysafe.android.brainTreeDetails.domain.models.BraintreeDetailsRequest


internal fun interface BrainTreeDetailsRepository {
    suspend fun getBrainTreeDetails(braintreeDetailsRequest: BraintreeDetailsRequest): PSResult<BrainTreeDetailsResponse>
}