/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.mapper

import com.paysafe.android.tokenization.data.entity.paymentHandle.PaymentHandleResponseSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.PaymentHandleStatusResponse
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandle
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleStatus
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleTokenStatus

fun PaymentHandleResponseSerializable.toDomain() = PaymentHandle(
    accountId = accountId,
    cardBin = card?.cardBin,
    networkTokenBin = card?.networkToken?.networkTokenBin,
    id = id,
    merchantRefNum = merchantRefNum,
    paymentHandleToken = paymentHandleToken,
    status = status,
    gatewayResponse = gatewayResponse
)

fun paymentHandleTokenStatusToDomain(status: String): PaymentHandleTokenStatus =
    when (status) {
        "INITIATED" -> PaymentHandleTokenStatus.INITIATED
        "PAYABLE" -> PaymentHandleTokenStatus.PAYABLE
        "EXPIRED" -> PaymentHandleTokenStatus.EXPIRED
        "COMPLETED" -> PaymentHandleTokenStatus.COMPLETED
        "PROCESSING" -> PaymentHandleTokenStatus.PROCESSING
        else -> PaymentHandleTokenStatus.FAILED
    }

internal fun PaymentHandleStatusResponse.toDomain() = PaymentHandleStatus(
    paymentHandleToken = paymentHandleToken,
    status = status?.run { paymentHandleTokenStatusToDomain(this) }
)
