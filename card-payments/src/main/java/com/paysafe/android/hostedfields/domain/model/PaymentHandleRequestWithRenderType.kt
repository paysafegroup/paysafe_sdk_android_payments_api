/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.domain.model

import com.paysafe.android.tokenization.domain.model.paymentHandle.BillingDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.MerchantDescriptor
import com.paysafe.android.tokenization.domain.model.paymentHandle.PSTokenizeOptions
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleReturnLink
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentType
import com.paysafe.android.tokenization.domain.model.paymentHandle.ShippingDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.SimulatorType
import com.paysafe.android.tokenization.domain.model.paymentHandle.TransactionType
import com.paysafe.android.tokenization.domain.model.paymentHandle.googlepay.GooglePayPaymentToken
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.Profile
import com.paysafe.android.tokenization.domain.model.paymentHandle.threeds.ThreeDS
import com.paysafe.android.tokenization.domain.model.paymentHandle.venmo.VenmoRequest

/**
 * Wrapper with all the parameters available for a tokenize payment.
 */
data class PaymentHandleRequestWithRenderType(

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

    /** ThreeDS. */
    val threeDS: ThreeDS? = null,

    val renderType: RenderType? = null,

    /** Google Pay additional data. */
    val googlePayPaymentToken: GooglePayPaymentToken? = null,

    val simulatorType: SimulatorType = SimulatorType.EXTERNAL,

    /** Venmo additinal data */
    val venmoRequest: VenmoRequest ? = null,

    /** List of return links information for payment. */
    val returnLinks: List<PaymentHandleReturnLink>? = null

) : PSTokenizeOptions



