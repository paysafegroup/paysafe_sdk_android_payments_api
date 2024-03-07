/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paymentmethods.data.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AccountConfigurationSerializable(

    @SerialName("cardTypeConfig")
    val cardTypeConfig: CardTypeConfigSerializable? = null,

    @SerialName("isGooglePay")
    val isGooglePay: Boolean? = null,

    @SerialName("googlePay")
    val googlePay: GooglePaySerializable? = null,

    @SerialName("clientId")
    val clientId: String? = null

)