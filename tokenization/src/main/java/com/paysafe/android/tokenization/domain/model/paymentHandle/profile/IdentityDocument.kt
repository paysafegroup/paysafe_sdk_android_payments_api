/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.profile

data class IdentityDocument(

    /** The customer’s social security number.
     * Number of characters required: <= 9 */
    val documentNumber: String

)
