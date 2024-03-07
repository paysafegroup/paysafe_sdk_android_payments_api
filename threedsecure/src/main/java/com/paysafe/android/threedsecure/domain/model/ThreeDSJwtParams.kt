/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.threedsecure.domain.model

internal data class ThreeDSJwtParams(
    var accountId: String? = null,
    val threeDSCard: ThreeDSCard? = null,
    val threeDSSdk: ThreeDSSdk? = null
) {
    constructor(bin: String?, accountId: String?) : this(
        accountId = accountId,
        threeDSCard = ThreeDSCard(bin),
        threeDSSdk = ThreeDSSdk(ThreeDSSdkType.ANDROID, "1.0.0")
    )
}