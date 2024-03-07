/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.paypal

import kotlinx.serialization.Serializable

@Serializable
internal enum class PayPalShippingPreferenceSerializable {

    GET_FROM_FILE,

    NO_SHIPPING,

    SET_PROVIDED_ADDRESS

}
