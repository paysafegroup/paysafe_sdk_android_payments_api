/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.detail

/**
 * This is the mechanism used by the cardholder to authenticate to the 3DS Requestor.
 */
enum class AuthenticationMethod {
    THIRD_PARTY_AUTHENTICATION,
    NO_LOGIN,
    INTERNAL_CREDENTIALS,
    FEDERATED_ID,
    ISSUER_CREDENTIALS,
    FIDO_AUTHENTICATOR
}