/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paypal.domain.model

import com.paysafe.android.tokenization.domain.model.paymentHandle.BillingDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.MerchantDescriptor
import com.paysafe.android.tokenization.domain.model.paymentHandle.PSTokenizeOptions
import com.paysafe.android.tokenization.domain.model.paymentHandle.ShippingDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.TransactionType
import com.paysafe.android.tokenization.domain.model.paymentHandle.paypal.PayPalRequest
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.Profile

/**
 * Tokenization parameters needed for a PayPal payment.
 */
data class PSPayPalTokenizeOptions(

    override val amount: Int,

    override val currencyCode: String,

    override val transactionType: TransactionType,

    override val merchantRefNum: String,

    override val billingDetails: BillingDetails? = null,

    override val profile: Profile? = null,

    override val accountId: String,

    override val merchantDescriptor: MerchantDescriptor? = null,

    override val shippingDetails: ShippingDetails? = null,

    /** The details of the PayPal account used for the transaction.  */
    val payPalRequest: PayPalRequest? = null

) : PSTokenizeOptions
