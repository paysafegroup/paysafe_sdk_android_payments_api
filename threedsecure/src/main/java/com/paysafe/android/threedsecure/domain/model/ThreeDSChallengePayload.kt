/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.threedsecure.domain.model

/**
 * Successful 3DS challenge result.
 */
data class ThreeDSChallengePayload(
    /** 3DS challenge authentication ID. */
    val authenticationId: String? = null,
    /** Account ID that launch 3DS challenge. */
    val accountId: String? = null,
    /** 3DS challenge server JWT. */
    val serverJwt: String? = null
)
