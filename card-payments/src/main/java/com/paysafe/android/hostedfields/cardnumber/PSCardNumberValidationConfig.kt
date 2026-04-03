/*
 * Copyright (c) 2026 Paysafe Group
 */

package com.paysafe.android.hostedfields.cardnumber

/**
 * Configuration class for PSCardNumber validation-related settings.
 */
data class PSCardNumberValidationConfig(
    val clearsErrorOnInput: Boolean = false,
    val validatesEmptyFieldOnBlur: Boolean = true
)
