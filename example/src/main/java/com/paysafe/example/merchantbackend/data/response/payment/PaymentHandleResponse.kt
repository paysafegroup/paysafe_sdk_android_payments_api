/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.merchantbackend.data.response.payment

import com.google.gson.annotations.SerializedName
import com.paysafe.example.merchantbackend.data.domain.payment.PaymentHandle
import com.paysafe.example.merchantbackend.data.response.card.CardResponse
import com.paysafe.example.merchantbackend.data.response.card.toDomain

data class PaymentHandleResponse(

    /** Identification for payment handle response. */
    @SerializedName("id")
    val id: String? = null,

    /** Status for payment handle response. */
    @SerializedName("status")
    val status: String? = null,

    /** Usage associated for payment. */
    @SerializedName("usage")
    val usage: String? = null,

    /** Payment type for handle request. */
    @SerializedName("paymentType")
    val paymentType: PaymentTypeResponse? = null,

    /** Token returned in payment handle. */
    @SerializedName("paymentHandleToken")
    val paymentHandleToken: String? = null,

    /** Credit card data for payment handle. */
    @SerializedName("card")
    val card: CardResponse? = null,

    @SerializedName("billingDetailsId")
    val billingDetailsId: String? = null,

    @SerializedName("multiUsePaymentHandleId")
    val multiUsePaymentHandleId: String? = null
)

fun PaymentHandleResponse.toDomain() = PaymentHandle(
    id = this.id,
    status = this.status,
    usage = this.usage,
    paymentType = this.paymentType?.toDomain(),
    paymentHandleToken = this.paymentHandleToken,
    card = this.card?.toDomain(),
    billingDetailsId = this.billingDetailsId,
    multiUsePaymentHandleId = this.multiUsePaymentHandleId
)