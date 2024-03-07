/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization

import android.app.Activity
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.tokenization.domain.model.paymentHandle.CardRequest
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandle
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleRequest

class PSTokenization(
    psApiClient: PSApiClient
) : PSTokenizationService {

    internal companion object {
        fun createPSTokenizationController(
            psApiClient: PSApiClient
        ): PSTokenizationController = PSTokenizationController(psApiClient)

    }

    private val controller = createPSTokenizationController(psApiClient)

    @JvmSynthetic
    override suspend fun tokenize(
        paymentHandleRequest: PaymentHandleRequest
    ): PSResult<PaymentHandle> = controller.tokenize(paymentHandleRequest)

    @JvmSynthetic
    override suspend fun tokenize(
        activity: Activity,
        paymentHandleRequest: PaymentHandleRequest,
        cardRequest: CardRequest
    ): PSResult<PaymentHandle> = controller.tokenize(activity, paymentHandleRequest, cardRequest)

    @JvmSynthetic
    override suspend fun refreshToken(
        paymentHandle: PaymentHandle
    ): PSResult<PaymentHandle> = controller.refreshToken(paymentHandle)
}