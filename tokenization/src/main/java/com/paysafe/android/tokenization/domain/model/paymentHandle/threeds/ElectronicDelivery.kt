/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.threeds

data class ElectronicDelivery(
    /** Is electronic delivery. */
    val isElectronicDelivery: Boolean,

    /** Email. */
    val email: String
)