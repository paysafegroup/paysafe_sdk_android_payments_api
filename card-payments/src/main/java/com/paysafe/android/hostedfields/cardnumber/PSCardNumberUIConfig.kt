/*
 * Copyright (c) 2026 Paysafe Group
 */

package com.paysafe.android.hostedfields.cardnumber

import com.paysafe.android.hostedfields.domain.model.CardNumberSeparator

/**
 * Configuration class for PSCardNumber UI-related settings.
 */
data class PSCardNumberUIConfig(
    val separator: CardNumberSeparator = CardNumberSeparator.WHITESPACE,
    val showBrandIcon: Boolean = true,
    val compactFieldHeight: Float? = null
)
