/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.domain.model

enum class PSCardFieldInputEvent {
    FOCUS,
    BLUR,
    VALID,
    INVALID,
    FIELD_VALUE_CHANGE,
    INVALID_CHARACTER
}