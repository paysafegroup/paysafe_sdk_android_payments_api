/*
 * Copyright (c) 2026 Paysafe Group
 */

package com.paysafe.android.hostedfields.cardnumber

import com.paysafe.android.hostedfields.domain.model.PSCardNumberState

/**
 * Configuration class for PSCardNumber field state and modifiers.
 */
data class PSCardNumberFieldConfig(
    val state: PSCardNumberState,
    val modifier: PSCardNumberModifier
)
