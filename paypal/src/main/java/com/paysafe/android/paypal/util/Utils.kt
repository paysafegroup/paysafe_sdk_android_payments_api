/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paypal.util

import com.paypal.android.corepayments.Environment
import com.paysafe.android.core.domain.model.config.PSEnvironment

internal fun PSEnvironment.toPaypalEnvironment() = when (this) {
    PSEnvironment.TEST -> Environment.SANDBOX
    PSEnvironment.PROD -> Environment.LIVE
}