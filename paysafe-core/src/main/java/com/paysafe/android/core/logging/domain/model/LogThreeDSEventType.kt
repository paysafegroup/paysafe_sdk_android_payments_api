/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging.domain.model

/**
 * Types of 3DS logging events.
 */
enum class LogThreeDSEventType {
    INTERNAL_SDK_ERROR,
    VALIDATION_ERROR,
    SUCCESS,
    NETWORK_ERROR
}
