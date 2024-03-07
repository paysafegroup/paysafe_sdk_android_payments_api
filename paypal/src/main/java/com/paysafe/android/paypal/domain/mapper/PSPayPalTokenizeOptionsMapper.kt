/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paypal.domain.mapper

import com.paysafe.android.paypal.domain.model.PSPayPalTokenizeOptions
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleRequest
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleReturnLink
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentType

internal fun PSPayPalTokenizeOptions.toPaymentHandleRequest(
    returnLinks: List<PaymentHandleReturnLink>
) = PaymentHandleRequest(
    amount = amount,
    currencyCode = currencyCode,
    transactionType = transactionType,
    merchantRefNum = merchantRefNum,
    billingDetails = billingDetails,
    profile = profile,
    accountId = accountId,
    merchantDescriptor = merchantDescriptor,
    shippingDetails = shippingDetails,
    paymentType = PaymentType.PAYPAL,
    payPalRequest = payPalRequest,
    returnLinks = returnLinks,
)
