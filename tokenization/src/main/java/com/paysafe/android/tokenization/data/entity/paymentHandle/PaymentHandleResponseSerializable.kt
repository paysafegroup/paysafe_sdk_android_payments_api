/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle

import com.paysafe.android.tokenization.data.entity.paymentHandle.response.CardResponseSerializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Structure to organize payment handle response, important data structure that links relevant information.
 */
@Serializable
data class PaymentHandleResponseSerializable(

    /** Identification for payment handle response. */
    @SerialName("id")
    val id: String? = null,

    /** Merchant reference number for payment. */
    @SerialName("merchantRefNum")
    val merchantRefNum: String,

    /** Credit card data for payment handle. */
    @SerialName("card")
    val card: CardResponseSerializable? = null,

    /** Identification account for payment. */
    @SerialName("accountId")
    val accountId: String? = null,

    /** Token returned in payment handle. */
    @SerialName("paymentHandleToken")
    val paymentHandleToken: String,

    /** Status for payment handle response. */
    @SerialName("status")
    val status: String,

    @SerialName("gatewayResponse")
    val gatewayResponse: GatewayResponseSerializable? = null

)