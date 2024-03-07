/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.domain.model

import com.paysafe.android.tokenization.domain.model.paymentHandle.BillingDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.MerchantDescriptor
import com.paysafe.android.tokenization.domain.model.paymentHandle.PSTokenizeOptions
import com.paysafe.android.tokenization.domain.model.paymentHandle.RenderType
import com.paysafe.android.tokenization.domain.model.paymentHandle.ShippingDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.TransactionType
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.Profile
import com.paysafe.android.tokenization.domain.model.paymentHandle.threeds.ThreeDS

/**
 * Tokenization parameters needed for a Card payment.
 */
data class PSCardTokenizeOptions(

    override val amount: Int,

    override val currencyCode: String,

    override val transactionType: TransactionType,

    override val merchantRefNum: String,

    override val billingDetails: BillingDetails? = null,

    override val profile: Profile? = null,

    override val accountId: String,

    override val merchantDescriptor: MerchantDescriptor? = null,

    override val shippingDetails: ShippingDetails? = null,

    /** ThreeDS. */
    val threeDS: ThreeDS? = null,

    /** Single use customer token. */
    val singleUseCustomerToken: String? = null,

    /** Payment handle token. */
    val paymentHandleTokenFrom: String? = null,

    /** Render type to display specific payment challenges. */
    val renderType: RenderType? = null

) : PSTokenizeOptions
