/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle

import com.paysafe.android.tokenization.domain.model.paymentHandle.googlepay.GooglePayPaymentToken
import com.paysafe.android.tokenization.domain.model.paymentHandle.paypal.PayPalRequest
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.Profile
import com.paysafe.android.tokenization.domain.model.paymentHandle.threeds.ThreeDS

/**
 * Wrapper with all the parameters available for a tokenize payment.
 */
data class PaymentHandleRequest(

    override val amount: Int,

    override val currencyCode: String,

    override val transactionType: TransactionType,

    override val merchantRefNum: String,

    override val billingDetails: BillingDetails? = null,

    override val profile: Profile? = null,

    override val accountId: String,

    override val merchantDescriptor: MerchantDescriptor? = null,

    override val shippingDetails: ShippingDetails? = null,

    /** Payment type for handle. */
    val paymentType: PaymentType? = null,

    /** Single use customer token. */
    val singleUseCustomerToken: String? = null,

    /** Payment handle token. */
    val paymentHandleTokenFrom: String? = null,

    /** Render type to display specific payment challenges. */
    val renderType: RenderType? = null,

    /** ThreeDS. */
    val threeDS: ThreeDS? = null,

    /** Google Pay additional data. */
    val googlePayPaymentToken: GooglePayPaymentToken? = null,

    /** PayPal additional data. */
    val payPalRequest: PayPalRequest? = null,

    /** List of return links information for payment. */
    val returnLinks: List<PaymentHandleReturnLink>? = null

) : PSTokenizeOptions
