/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization

import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.tokenization.domain.model.paymentHandle.CardRequest
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandle
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleRequest


interface PSTokenizationService {
    @JvmSynthetic
    suspend fun tokenize(
        paymentHandleRequest: PaymentHandleRequest,
        cardRequest: CardRequest? = null
    ): PSResult<PaymentHandle>

    @JvmSynthetic
    suspend fun refreshToken(
        paymentHandle: PaymentHandle
    ): PSResult<PaymentHandle>
}