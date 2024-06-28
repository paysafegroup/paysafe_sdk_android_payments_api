/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.venmo

/**
 * The shipping preference.
 */
enum class PSVenmoShippingPreference {

    /** Redacts the shipping address from the Venmo pages. Recommended for digital goods.*/
    GET_FROM_FILE,

    /** Uses the customer-selected shipping address on Venmo pages. */
    NO_SHIPPING,

    /** If available, uses the merchant-provided shipping address, which the customer cannot
     * change on the Venmo pages. If the merchant does not provide an address,
     * the customer can enter the address on Venmo pages.*/
    SET_PROVIDED_ADDRESS

}
