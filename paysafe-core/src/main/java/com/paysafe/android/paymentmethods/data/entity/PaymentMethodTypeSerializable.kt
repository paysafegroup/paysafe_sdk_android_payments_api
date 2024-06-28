/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paymentmethods.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class PaymentMethodTypeSerializable {
    @SerialName("CARD")
    CARD,

    @SerialName("APPLEPAY")
    APPLEPAY,

    @SerialName("SKRILL")
    SKRILL,

    @SerialName("NETELLER")
    NETELLER,

    @SerialName("PAYSAFECASH")
    PAYSAFECASH,

    @SerialName("PAYSAFECARD")
    PAYSAFECARD,

    @SerialName("VENMO")
    VENMO,

    @SerialName("VIPPREFERRED")
    VIPPREFERRED,

    @SerialName("MAZOOMA")
    MAZOOMA,

    @SerialName("SIGHTLINE")
    SIGHTLINE,

    @SerialName("INTERAC_ETRANSFER")
    INTERAC_ETRANSFER,

    @SerialName("RAPID_TRANSFER")
    RAPID_TRANSFER,

    @SerialName("SKRILL1TAP")
    SKRILL1TAP,

    @SerialName("ACH")
    ACH,

    @SerialName("EFT")
    EFT,

    @SerialName("BACS")
    BACS,

    @SerialName("SEPA")
    SEPA,

    @SerialName("BANK_TRANSFER")
    BANK_TRANSFER
}