/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging.domain.model

import com.paysafe.android.core.BuildConfig

internal data class LogThreeDSSdk(
    val type: String = "ANDROID",
    val version: String = BuildConfig.APP_VERSION
)
