/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.venmo.domain.mapper

import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleRequest
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleReturnLink
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentType
import com.paysafe.android.venmo.domain.model.PSVenmoTokenizeOptions

internal fun PSVenmoTokenizeOptions.toPaymentHandleRequest(
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
    paymentType = PaymentType.VENMO,
    venmoRequest = venmoRequest,
    simulatorType = simulator,
    returnLinks = returnLinks,
)
