/*
 * Copyright (c) 2026 Paysafe Group
 */

package com.paysafe.android.hostedfields.cardnumber

/**
 * Configuration class for PSCardNumber text-related settings.
 */
data class PSCardNumberTextConfig(
    val labelText: String,
    val placeholderText: String? = null,
    val animateTopLabelText: Boolean = true
)
