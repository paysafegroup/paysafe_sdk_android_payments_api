package com.paysafe.android.brainTreeDetails.data


import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.paysafe.android.brainTreeDetails.data.api.BraintreeDetailsApi
import com.paysafe.android.brainTreeDetails.data.entity.BrainTreeDetailsResponse
import com.paysafe.android.brainTreeDetails.data.repository.BrainTreeDetailsRepositoryImpl
import com.paysafe.android.brainTreeDetails.domain.models.BraintreeDetailsRequest
import com.paysafe.android.brainTreeDetails.domain.repository.BrainTreeDetailsRepository
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.entity.PSResultCallback
import com.paysafe.android.core.data.entity.resultAsCallback
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.core.exception.errorName
import com.paysafe.android.core.exception.failedToLoadAvailableMethodsException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class BrainTreeDetailsServiceImpl(
    private val psApiClient: PSApiClient,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BrainTreeDetailsService {

    private val braintreeDetailsApi = BraintreeDetailsApi(psApiClient)
    private val brainTreeDetailsRepository: BrainTreeDetailsRepository =
        BrainTreeDetailsRepositoryImpl(braintreeDetailsApi)

    override fun getBraintreeDetails(
        lifecycleOwner: LifecycleOwner,
        braintreeDetailsRequest: BraintreeDetailsRequest,
        callback: PSResultCallback<BrainTreeDetailsResponse>
    ) {
        lifecycleOwner.lifecycleScope.launch(ioDispatcher) {
            val result = getBraintreeDetails(braintreeDetailsRequest)
            withContext(mainDispatcher) {
                resultAsCallback(result, callback)
            }
        }
    }

    override suspend fun getBraintreeDetails(braintreeDetailsRequest: BraintreeDetailsRequest): PSResult<BrainTreeDetailsResponse> {

        val result = withContext(ioDispatcher) {
            brainTreeDetailsRepository.getBrainTreeDetails(braintreeDetailsRequest)
        }
        if (result is PSResult.Failure) {
            val paysafeException =
                failedToLoadAvailableMethodsException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
        }
        return result
    }
}
