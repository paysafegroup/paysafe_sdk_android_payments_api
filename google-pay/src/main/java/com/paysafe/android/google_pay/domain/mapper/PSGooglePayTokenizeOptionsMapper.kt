/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.google_pay.domain.mapper

import com.paysafe.android.google_pay.domain.model.PSGooglePayTokenizeOptions
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleRequest
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentType
import com.paysafe.android.tokenization.domain.model.paymentHandle.googlepay.GooglePayPaymentToken

internal fun PSGooglePayTokenizeOptions.toPaymentHandleRequest(
    token: GooglePayPaymentToken,
): PaymentHandleRequest = PaymentHandleRequest(
    amount = amount,
    currencyCode = currencyCode,
    transactionType = transactionType,
    merchantRefNum = merchantRefNum,
    billingDetails = billingDetails,
    profile = profile,
    accountId = accountId,
    merchantDescriptor = merchantDescriptor,
    shippingDetails = shippingDetails,
    paymentType = PaymentType.CARD,
    googlePayPaymentToken = token,
    threeDS = threeDS
)