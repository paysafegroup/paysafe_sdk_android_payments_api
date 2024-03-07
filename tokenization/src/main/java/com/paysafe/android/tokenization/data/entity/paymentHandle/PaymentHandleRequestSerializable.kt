/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle

import com.paysafe.android.tokenization.data.entity.paymentHandle.googlepay.GooglePayRequest
import com.paysafe.android.tokenization.data.entity.paymentHandle.paypal.PayPalRequestSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.profile.ProfileSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.request.BillingDetailsRequestSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.request.CardRequestSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.request.ShippingDetailsSerializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Structure to organize payment handle request, important data structure that links relevant information.
 */
@Serializable
internal data class PaymentHandleRequestSerializable(

    /** Merchant reference number for payment. */
    @SerialName("merchantRefNum")
    val merchantRefNum: String? = null,

    /** Transaction type for payment. */
    @SerialName("transactionType")
    val transactionType: TransactionTypeSerializable? = null,

    /** Credit card data for payment handle. */
    @SerialName("card")
    val card: CardRequestSerializable? = null,

    /** Identification account for payment. */
    @SerialName("accountId")
    val accountId: String? = null,

    /** Payment type for handle request. */
    @SerialName("paymentType")
    val paymentType: PaymentTypeSerializable? = null,

    /** Payment amount. */
    @SerialName("amount")
    val amount: Int? = null,

    /** Currency code associated for payment. */
    @SerialName("currencyCode")
    val currencyCode: String? = null,

    /** List of return links information for payment. */
    @SerialName("returnLinks")
    val returnLinks: List<ReturnLinkSerializable>? = null,

    /** Profile request. */
    @SerialName("profile")
    val profile: ProfileSerializable? = null,

    /** Three DS for payment. */
    @SerialName("threeDs")
    val threeDS: ThreeDSSerializable? = null,

    /** Billing details for payment. */
    @SerialName("billingDetails")
    val billingDetails: BillingDetailsRequestSerializable? = null,

    /** Merchant description for payment. */
    @SerialName("merchantDescriptor")
    val merchantDescriptor: MerchantDescriptorSerializable? = null,

    /** Shipping details for payment. */
    @SerialName("shippingDetails")
    val shippingDetails: ShippingDetailsSerializable? = null,

    /** Single use customer token. */
    @SerialName("singleUseCustomerToken")
    val singleUseCustomerToken: String? = null,

    /** Payment handle token. */
    @SerialName("paymentHandleTokenFrom")
    val paymentHandleTokenFrom: String? = null,

    /** Google Pay. */
    @SerialName("googlePay")
    val googlePay: GooglePayRequest? = null,

    @SerialName("paypal")
    val payPal: PayPalRequestSerializable? = null

)