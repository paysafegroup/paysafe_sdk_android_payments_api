/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.repository

import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.tokenization.domain.model.paymentHandle.CardRequest
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandle
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleRequest
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleStatus

/**
 * Payment hub repository interface, where it's defined the method to create a payment handle and status.
 */
internal interface PaymentHubRepository {
    /**
     * Method signature to define how to create a payment handle.
     *
     * @param paymentHandleRequest Payment handle request parameters object.
     * @param cardRequest Card data input.
     * @return Paysafe result wrapper object with payment handle model.
     */
    suspend fun createPaymentHandle(
        paymentHandleRequest: PaymentHandleRequest,
        cardRequest: CardRequest? = null,
        doBeforeRequest: (String, String) -> Unit
    ): PSResult<PaymentHandle?>

    /**
     * Method signature to define how to create a payment handle status.
     *
     * @param paymentHandleToken Payment handle token.
     * @return Paysafe result wrapper object with payment handle status model.
     */
    suspend fun getPaymentHandleStatus(paymentHandleToken: String): PSResult<PaymentHandleStatus?>
}