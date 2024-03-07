/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.google_pay.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class BillingAddressSerializable(

    @SerialName("name")
    val name: String? = null,

    @SerialName("postalCode")
    val postalCode: String? = null,

    @SerialName("countryCode")
    val countryCode: String? = null,

    @SerialName("phoneNumber")
    val phoneNumber: String? = null,

    @SerialName("address1")
    val address1: String? = null,

    @SerialName("address2")
    val address2: String? = null,

    @SerialName("address3")
    val address3: String? = null,

    @SerialName("locality")
    val locality: String? = null,

    @SerialName("administrativeArea")
    val administrativeArea: String? = null,

    @SerialName("sortingCode")
    val sortingCode: String? = null

)