/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paymentmethods.data.repository

import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.paymentmethods.data.api.PaymentMethodsApi
import com.paysafe.android.paymentmethods.data.mapper.toDomain
import com.paysafe.android.paymentmethods.domain.model.PaymentMethod
import com.paysafe.android.paymentmethods.domain.model.PaymentMethodType
import com.paysafe.android.paymentmethods.domain.repository.PaymentMethodsRepository

internal class PaymentMethodsRepositoryImpl(
    private val paymentMethodsApi: PaymentMethodsApi
) : PaymentMethodsRepository {
    override suspend fun getPaymentMethodList(currencyCode: String): PSResult<List<PaymentMethod>> {
        val response = paymentMethodsApi.getPaymentMethods(currencyCode)
        val result = when (response) {
            is PSResult.Success -> {
                try {
                    val supportedPaymentMethods =
                        removeUnsupportedPaymentMethods(response.value?.toDomain())
                    PSResult.Success(supportedPaymentMethods)
                } catch (ex: Exception) {
                    PSResult.Failure(ex)
                }
            }

            is PSResult.Failure -> response
        }
        return result
    }

    private fun removeUnsupportedPaymentMethods(paymentMethods: List<PaymentMethod>?) =
        paymentMethods?.filter { it.paymentMethod != PaymentMethodType.UNSUPPORTED }
}