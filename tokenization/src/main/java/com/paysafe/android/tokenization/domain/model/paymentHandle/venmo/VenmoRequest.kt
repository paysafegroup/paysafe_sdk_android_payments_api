/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.venmo

/**
 * The details of the Venmo account used for the transaction.
 */
data class VenmoRequest(

    /** The unique merchant's consumer id and must be unique per consumer.*/
    val consumerId: String,

    /**
    You can set up multiple accounts with Braintree, and each account can settle funds into a different bank account. This
    parameter therefore allows you to control which of your bank accounts is used to receive settlement.*/
    val merchantAccountId: String,

    /**
    You can set up multiple profiles with Braintree, where each profile shows the consumer a different logo and description during
    checkout on the Venmo app, and on the Venmo statement. This parameter therefore allows you to vary the consumer
    experience (for example, if you have multiple brands, you can display a different logo for each).*/
    val profileId:String,



)
