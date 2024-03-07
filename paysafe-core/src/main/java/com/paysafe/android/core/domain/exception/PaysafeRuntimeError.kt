/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.domain.exception

data class PaysafeRuntimeError(
    val code: Int = 0,
    val displayMessage: String = "",
    val detailedMessage: String = "",
    val correlationId: String = ""
) : Throwable(displayMessage)

internal fun PaysafeRuntimeError.toPaysafeException() = PaysafeException(
    code = code,
    displayMessage = displayMessage,
    detailedMessage = detailedMessage,
    correlationId = correlationId
)