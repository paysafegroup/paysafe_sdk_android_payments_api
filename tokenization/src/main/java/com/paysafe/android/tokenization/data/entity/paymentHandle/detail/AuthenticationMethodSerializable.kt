/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.detail

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class AuthenticationMethodSerializable {
    @SerialName("THIRD_PARTY_AUTHENTICATION")
    THIRD_PARTY_AUTHENTICATION,

    @SerialName("NO_LOGIN")
    NO_LOGIN,

    @SerialName("INTERNAL_CREDENTIALS")
    INTERNAL_CREDENTIALS,

    @SerialName("FEDERATED_ID")
    FEDERATED_ID,

    @SerialName("ISSUER_CREDENTIALS")
    ISSUER_CREDENTIALS,

    @SerialName("FIDO_AUTHENTICATOR")
    FIDO_AUTHENTICATOR
}