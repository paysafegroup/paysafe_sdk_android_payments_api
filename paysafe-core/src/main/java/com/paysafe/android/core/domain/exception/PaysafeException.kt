/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.domain.exception

data class PaysafeException(
    val code: Int = 0,
    val displayMessage: String = "",
    val detailedMessage: String = "",
    val correlationId: String = ""
) : Exception(displayMessage)