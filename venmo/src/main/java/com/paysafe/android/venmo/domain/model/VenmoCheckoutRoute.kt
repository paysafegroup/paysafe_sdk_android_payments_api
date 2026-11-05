package com.paysafe.android.venmo.domain.model

/**
 * What the SDK does after a Venmo payment handle is created.
 *
 * [brainTreeCustomerId] chooses a recurring shopper. Status and action only decide
 * whether a first-time shopper still needs the Venmo app.
 */
internal sealed class VenmoCheckoutRoute {

    object Payable : VenmoCheckoutRoute()
    data class Failed(val status: String) : VenmoCheckoutRoute()

    data class Recurring(
        val sessionToken: String,
        val clientToken: String,
        val clientMetadataId: String?,
    ) : VenmoCheckoutRoute()

    data class FirstTime(
        val sessionToken: String,
        val clientToken: String,
        val clientMetadataId: String?,
    ) : VenmoCheckoutRoute()

    object NoVenmoCheckout : VenmoCheckoutRoute()
}
