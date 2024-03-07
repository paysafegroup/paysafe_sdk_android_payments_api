/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.profile

data class DateOfBirth(
    /** Day. */
    val day: Int? = null,

    /** Month. */
    val month: Int? = null,

    /** Year. */
    val year: Int? = null
)