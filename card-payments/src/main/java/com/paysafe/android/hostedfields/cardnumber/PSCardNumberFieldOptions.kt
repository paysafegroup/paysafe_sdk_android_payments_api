/*
 * Copyright (c) 2026 Paysafe Group
 */

package com.paysafe.android.hostedfields.cardnumber

import com.paysafe.android.hostedfields.domain.model.CardNumberSeparator

/**
 * Configuration class for PSCardNumberField UI and validation settings.
 */
data class PSCardNumberFieldOptions(
    val separator: CardNumberSeparator = CardNumberSeparator.WHITESPACE,
    val clearsErrorOnInput: Boolean = false,
    val validatesEmptyFieldOnBlur: Boolean = true,
    val showBrandIcon: Boolean = true,
    val compactFieldHeight: Float? = null
)
