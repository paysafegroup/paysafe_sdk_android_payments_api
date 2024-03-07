/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.threedsecure.domain.model

internal data class ThreeDSJwt(
    val accountId: String? = null,
    val card: ThreeDSCard? = null,
    val deviceFingerprintingId: String? = null,
    val id: String? = null,
    val jwt: String? = null,
    val sdk: ThreeDSSdk? = null
)