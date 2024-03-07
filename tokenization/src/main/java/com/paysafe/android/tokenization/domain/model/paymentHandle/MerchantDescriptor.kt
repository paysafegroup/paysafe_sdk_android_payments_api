/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle

/**
 * Merchant descriptor.
 */
data class MerchantDescriptor(

    /** Dynamic descriptor. */
    val dynamicDescriptor: String,

    /** Phone. */
    val phone: String? = null

)
