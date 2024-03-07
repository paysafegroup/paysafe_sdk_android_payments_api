/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ReturnLinkSerializable(

    @SerialName("rel")
    val relation: ReturnLinkRelationSerializable,

    @SerialName("href")
    val href: String,

    @SerialName("method")
    val method: String? = null

)