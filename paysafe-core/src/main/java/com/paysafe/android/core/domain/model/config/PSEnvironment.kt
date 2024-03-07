/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.domain.model.config

import com.paysafe.android.core.data.api.paysafeProduction
import com.paysafe.android.core.data.api.paysafeTest

/**
 * Paysafe server environment.
 *
 * @property url Uniform resource locator for a specific environment.
 */
enum class PSEnvironment(val url: String) {
    /** Test environment. */
    TEST(paysafeTest),

    /** Production environment. */
    PROD(paysafeProduction)
}