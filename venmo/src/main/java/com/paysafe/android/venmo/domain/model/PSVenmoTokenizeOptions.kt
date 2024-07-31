/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.venmo.domain.model

import com.paysafe.android.tokenization.domain.model.paymentHandle.BillingDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.MerchantDescriptor
import com.paysafe.android.tokenization.domain.model.paymentHandle.PSTokenizeOptions
import com.paysafe.android.tokenization.domain.model.paymentHandle.ShippingDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.SimulatorType
import com.paysafe.android.tokenization.domain.model.paymentHandle.TransactionType
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.Profile
import com.paysafe.android.tokenization.domain.model.paymentHandle.venmo.VenmoRequest

/**
 * Tokenization parameters needed for a Venmo payment.
 */
data class PSVenmoTokenizeOptions(

    override val amount: Int,

    override val currencyCode: String,

    override val transactionType: TransactionType,

    override val merchantRefNum: String,

    override val billingDetails: BillingDetails? = null,

    override val profile: Profile? = null,

    override val accountId: String,

    override val merchantDescriptor: MerchantDescriptor? = null,

    override val shippingDetails: ShippingDetails? = null,

    override val simulator: SimulatorType = SimulatorType.EXTERNAL,

    /** The details of the Venmo account used for the transaction.  */
    val venmoRequest: VenmoRequest? = null,

    val customUrlScheme: String? = null

) : PSTokenizeOptions
