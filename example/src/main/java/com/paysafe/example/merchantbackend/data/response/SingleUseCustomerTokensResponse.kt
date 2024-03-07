/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.merchantbackend.data.response

import com.google.gson.annotations.SerializedName
import com.paysafe.example.merchantbackend.data.domain.SingleUseCustomerTokens
import com.paysafe.example.merchantbackend.data.response.payment.PaymentHandleResponse
import com.paysafe.example.merchantbackend.data.response.payment.toDomain

data class SingleUseCustomerTokensResponse(
    @SerializedName("singleUseCustomerToken")
    val singleUseCustomerToken: String? = null,

    @SerializedName("paymentHandles")
    val paymentHandles: List<PaymentHandleResponse>? = null
)

fun SingleUseCustomerTokensResponse.toDomain() = SingleUseCustomerTokens(
    singleUseCustomerToken = this.singleUseCustomerToken,
    paymentHandles = this.paymentHandles?.map { it.toDomain() }
)