/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paymentmethods.domain.repository

import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.paymentmethods.domain.model.PaymentMethod

internal fun interface PaymentMethodsRepository {
    suspend fun getPaymentMethodList(currencyCode: String): PSResult<List<PaymentMethod>>
}