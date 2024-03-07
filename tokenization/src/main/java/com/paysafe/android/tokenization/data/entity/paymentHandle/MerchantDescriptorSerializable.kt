/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Structure to organize merchant descriptor data.
 */
@Serializable
internal data class MerchantDescriptorSerializable(

    /** Dynamic descriptor for merchant. */
    @SerialName("dynamicDescriptor")
    val dynamicDescriptor: String,

    /** Phone for merchant. */
    @SerialName("phone")
    val phone: String? = null

)