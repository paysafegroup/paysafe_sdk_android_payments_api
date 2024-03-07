/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paymentmethods

import androidx.lifecycle.LifecycleOwner
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.entity.PSResultCallback
import com.paysafe.android.paymentmethods.domain.model.PaymentMethod

/**
 * Payment methods interface that defines the methods to retrieve the payment methods available.
 */
interface PaymentMethodsService {

    /**
     * Definition to be able to get payment methods
     *
     * @param lifecycleOwner Helper for coroutine execution.
     * @param currencyCode The currency code for which to retrieve the payment methods.
     * @param callback Object to process success and errors methods.
     */
    fun getPaymentMethods(
        lifecycleOwner: LifecycleOwner,
        currencyCode: String,
        callback: PSResultCallback<List<PaymentMethod>>
    )

    /**
     * Definition to be able to get payment methods
     *
     * @param currencyCode The currency code for which to retrieve the payment methods.
     * @return Paysafe result wrapper object [PSResult] with the list of [PaymentMethod]s.
     */
    @JvmSynthetic
    suspend fun getPaymentMethods(currencyCode: String): PSResult<List<PaymentMethod>>

}