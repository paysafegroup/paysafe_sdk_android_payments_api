/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.domain.model

enum class PSCardFieldInputEvent {
    FOCUS,
    VALID,
    INVALID,
    FIELD_VALUE_CHANGE,
    INVALID_CHARACTER
}