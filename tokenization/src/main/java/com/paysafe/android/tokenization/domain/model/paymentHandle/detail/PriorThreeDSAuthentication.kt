/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.detail

class PriorThreeDSAuthentication(
    /** Data. */
    val data: String? = null,

    /** 3DS authentication method. */
    val method: ThreeDSAuthentication? = null,

    /** Id. */
    val id: String? = null,

    /** Time. */
    val time: String? = null
)