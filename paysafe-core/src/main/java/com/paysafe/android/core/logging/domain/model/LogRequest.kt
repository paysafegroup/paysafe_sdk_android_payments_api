/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging.domain.model

/**
 * Structure that encapsulates the log request.
 */
internal data class LogRequest(

    /** Log type. */
    val type: LogType,

    /** Log client info. */
    val clientInfo: LogClientInfo,

    /** Log payload. */
    val payload: LogPayload

)
