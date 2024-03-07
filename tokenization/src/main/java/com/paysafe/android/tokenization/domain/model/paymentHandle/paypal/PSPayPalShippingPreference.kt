/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.paypal

/**
 * The shipping preference.
 */
enum class PSPayPalShippingPreference {

    /** Redacts the shipping address from the PayPal pages. Recommended for digital goods.*/
    GET_FROM_FILE,

    /** Uses the customer-selected shipping address on PayPal pages. */
    NO_SHIPPING,

    /** If available, uses the merchant-provided shipping address, which the customer cannot
     * change on the PayPal pages. If the merchant does not provide an address,
     * the customer can enter the address on PayPal pages.*/
    SET_PROVIDED_ADDRESS

}
