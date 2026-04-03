/*
 * Copyright (c) 2026 Paysafe Group
 */

package com.paysafe.android.hostedfields.cardnumber

/**
 * Configuration class for PSCardNumberField text display settings.
 */
data class PSCardNumberFieldTextOptions(
    val labelText: String,
    val placeholderText: String? = null,
    val animateTopLabelText: Boolean = true
)
