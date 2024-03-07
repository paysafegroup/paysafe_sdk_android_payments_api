/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle

data class ThreeDSProfile(
    /** This is the email address of the customer.
     * Number of characters required: <= 255 */
    val email: String? = null,

    /** This is the customer's primary phone.
     * Number of characters required: <= 40 */
    val phone: String? = null,

    /** This is the customer's cell phone.
     * Number of characters required: <= 40 */
    val cellPhone: String? = null
)