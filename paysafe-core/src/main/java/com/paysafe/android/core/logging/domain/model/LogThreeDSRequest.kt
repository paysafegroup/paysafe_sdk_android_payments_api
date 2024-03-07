/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging.domain.model

/**
 * Structure that encapsulates the 3DS log request.
 */
internal data class LogThreeDSRequest(
    val eventType: LogThreeDSEventType,
    val eventMessage: String,
    val sdk: LogThreeDSSdk = LogThreeDSSdk()
)
