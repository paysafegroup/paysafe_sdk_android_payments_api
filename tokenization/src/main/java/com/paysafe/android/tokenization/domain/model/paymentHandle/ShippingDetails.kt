/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle

data class ShippingDetails(

    /** This is the method of shipment. */
    val shipMethod: ShippingMethod? = null,

    /** This is the recipient's street address.
     * Number of characters required: <= 50 */
    val street: String? = null,

    /** This is the second line of the street address in the shipping address, if required
     * (e.g., apartment number).
     * Number of characters required: <= 255 */
    val street2: String? = null,

    /** This is the city in which the recipient resides.
     * Number of characters required: <= 40 */
    val city: String? = null,

    /** This is the state/province/region in which the recipient lives.
     * Number of characters required: >= 2 & <= 40 */
    val state: String? = null,

    /** This is the country where the address is located.
     * Number of characters required: >= 2 & <= 2 */
    val countryCode: String? = null,

    /** This is the recipient's postal/zip code.
     * Number of characters required: <= 10 */
    val zip: String? = null
)
