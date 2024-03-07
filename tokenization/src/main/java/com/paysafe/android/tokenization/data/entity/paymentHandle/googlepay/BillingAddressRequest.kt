/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.googlepay

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class BillingAddressRequest(

    /** Name. */
    @SerialName("name")
    val name: String? = null,

    /** Postal code. */
    @SerialName("postalCode")
    val postalCode: String? = null,

    /** Country code. */
    @SerialName("countryCode")
    val countryCode: String? = null,

    /** Phone number. */
    @SerialName("phoneNumber")
    val phoneNumber: String? = null,

    /** Address one. */
    @SerialName("address1")
    val address1: String? = null,

    /** Address two. */
    @SerialName("address2")
    val address2: String? = null,

    /** Address three. */
    @SerialName("address3")
    val address3: String? = null,

    /** Locality. */
    @SerialName("locality")
    val locality: String? = null,

    /** Administrative area. */
    @SerialName("administrativeArea")
    val administrativeArea: String? = null,

    /** Sorting code. */
    @SerialName("sortingCode")
    val sortingCode: String? = null
)