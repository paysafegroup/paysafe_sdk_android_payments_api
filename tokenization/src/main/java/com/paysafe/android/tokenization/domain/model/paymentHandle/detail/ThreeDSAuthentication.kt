/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.detail

/**
 * This is the mechanism used previously by the cardholder to authenticate to the 3DS Requestor.
 */
enum class ThreeDSAuthentication {
    FRICTIONLESS_AUTHENTICATION,
    ACS_CHALLENGE,
    AVS_VERIFIED,
    OTHER_ISSUER_METHOD
}