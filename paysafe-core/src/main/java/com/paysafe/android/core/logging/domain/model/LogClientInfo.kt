/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging.domain.model

import com.paysafe.android.core.BuildConfig

/**
 * Information regarding Mobile SDK.
 */
internal data class LogClientInfo(

    /** UUID generated per Mobile SDK instance. */
    val correlationId: String,

    /** Paysafe API key. */
    val apiKey: String,

    /** Type of the Mobile SDK integration. */
    val integrationType: LogIntegrationType,

    /** Version of the current Mobile SDK. */
    val version: String = BuildConfig.APP_VERSION,

    /** Name of the Paysafe Mobile SDK. */
    val appName: String = "paysafe.android.sdk"

)
