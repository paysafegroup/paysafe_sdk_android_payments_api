/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging.domain.model

data class LogErrorMessage(
    val code: String,
    val detailedMessage: String,
    val displayMessage: String,
    val name: String,
    val message: String
)
