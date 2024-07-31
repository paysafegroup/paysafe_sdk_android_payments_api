/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.domain.mapper

import com.paysafe.android.hostedfields.domain.model.PSCardTokenizeOptions
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleRequest
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentType

internal fun PSCardTokenizeOptions.toPaymentHandleRequest() = PaymentHandleRequest(
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
    singleUseCustomerToken = singleUseCustomerToken,
    paymentHandleTokenFrom = paymentHandleTokenFrom,
    renderType = renderType,
    simulatorType = simulator,
    threeDS = threeDS
)
