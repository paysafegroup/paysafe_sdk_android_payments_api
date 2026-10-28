/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields

data class PSCardFormConfig @JvmOverloads constructor(

    /** Currency code. */
    val currencyCode: String,

    /** Account id for payment. */
    val accountId: String,

    /**
     * Whether CVV is required.
     *
     * - Default: `true` (CVV required).
     * - `false`: empty CVV is valid for saved-card forms (no card number field), for both
     *   live validation and tokenize when saved-card tokens are present.
     * - Partial CVV is always rejected; a filled valid CVV is sent on tokenize.
     * - New card forms always require CVV.
     */
    val requireCvv: Boolean = true

)
