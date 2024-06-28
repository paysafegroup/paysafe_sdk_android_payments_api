/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Structure to organize billing details request data.
 */
@Serializable
data class BillingDetailsRequestSerializable(

    /** Country for billing. */
    @SerialName("country")
    val country: String? = null,

    /** Postal code for billing. */
    @SerialName("zip")
    val zip: String? = null,

    /** Nickname for billing. */
    @SerialName("nickName")
    val nickName: String? = null,

    /** Street for billing. */
    @SerialName("street")
    val street: String? = null,

    /** Street one for billing. */
    @SerialName("street1")
    val street1: String? = null,

    /** Street two for billing. */
    @SerialName("street2")
    val street2: String? = null,

    /** City for billing. */
    @SerialName("city")
    val city: String? = null,

    /** State(or department) for billing. */
    @SerialName("state")
    val state: String? = null,

    /** Phone for billing. */
    @SerialName("phone")
    val phone: String? = null

)