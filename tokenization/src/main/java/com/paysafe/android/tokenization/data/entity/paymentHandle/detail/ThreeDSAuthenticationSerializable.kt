/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.detail

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ThreeDSAuthenticationSerializable {
    @SerialName("FRICTIONLESS_AUTHENTICATION")
    FRICTIONLESS_AUTHENTICATION,

    @SerialName("ACS_CHALLENGE")
    ACS_CHALLENGE,

    @SerialName("AVS_VERIFIED")
    AVS_VERIFIED,

    @SerialName("OTHER_ISSUER_METHOD")
    OTHER_ISSUER_METHOD
}