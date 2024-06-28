/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DateOfBirthSerializable(
    /** Day. */
    @SerialName("day")
    val day: Int? = null,

    /** Month. */
    @SerialName("month")
    val month: Int? = null,

    /** Year. */
    @SerialName("year")
    val year: Int? = null
)