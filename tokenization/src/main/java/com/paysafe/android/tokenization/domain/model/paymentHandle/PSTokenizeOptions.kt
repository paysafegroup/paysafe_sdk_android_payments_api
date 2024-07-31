/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle

import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.Profile

/**
 * Parameters needed for every tokenize call.
 */
interface PSTokenizeOptions {

    /** Payment amount in minor units. */
    val amount: Int

    /** Currency code. */
    val currencyCode: String

    /** Transaction type. */
    val transactionType: TransactionType

    /** Merchant reference number. */
    val merchantRefNum: String

    /** Billing details. */
    val billingDetails: BillingDetails?

    /** User profile. */
    val profile: Profile?

    /** Identification account. */
    val accountId: String

    /** Merchant descriptor. */
    val merchantDescriptor: MerchantDescriptor?

    /** Shipping details. */
    val shippingDetails: ShippingDetails?

    /** Simulator details. */
    val simulator: SimulatorType
        get() = SimulatorType.EXTERNAL

}