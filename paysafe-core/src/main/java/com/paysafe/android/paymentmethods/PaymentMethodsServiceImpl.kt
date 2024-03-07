/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paymentmethods

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.entity.PSResultCallback
import com.paysafe.android.core.data.entity.resultAsCallback
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.core.exception.errorName
import com.paysafe.android.core.exception.failedToLoadAvailableMethodsException
import com.paysafe.android.paymentmethods.data.api.PaymentMethodsApi
import com.paysafe.android.paymentmethods.data.repository.PaymentMethodsRepositoryImpl
import com.paysafe.android.paymentmethods.domain.model.PaymentMethod
import com.paysafe.android.paymentmethods.domain.repository.PaymentMethodsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentMethodsServiceImpl(
    private val psApiClient: PSApiClient,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PaymentMethodsService {

    private val paymentMethodsApi = PaymentMethodsApi(psApiClient)
    private val paymentMethodsRepository: PaymentMethodsRepository =
        PaymentMethodsRepositoryImpl(paymentMethodsApi)

    override fun getPaymentMethods(
        lifecycleOwner: LifecycleOwner,
        currencyCode: String,
        callback: PSResultCallback<List<PaymentMethod>>
    ) {
        lifecycleOwner.lifecycleScope.launch(ioDispatcher) {
            val result = getPaymentMethods(currencyCode)
            withContext(mainDispatcher) {
                resultAsCallback(result, callback)
            }
        }
    }

    override suspend fun getPaymentMethods(currencyCode: String): PSResult<List<PaymentMethod>> {
        val start = System.currentTimeMillis()
        val result = paymentMethodsRepository.getPaymentMethodList(currencyCode)
        val duration = System.currentTimeMillis() - start
        logPaymentMethodDurationEvent(duration)
        if (result is PSResult.Failure) {
            val paysafeException =
                failedToLoadAvailableMethodsException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
        }
        return result
    }

    private fun logPaymentMethodDurationEvent(duration: Long) {
        val message = "Payment method configuration was successfully loaded in $duration ms"
        psApiClient.logEvent(message)
    }

}
