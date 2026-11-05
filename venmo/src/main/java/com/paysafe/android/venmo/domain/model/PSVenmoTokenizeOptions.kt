/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.venmo.domain.model

import com.paysafe.android.tokenization.domain.model.paymentHandle.BillingDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.MerchantDescriptor
import com.paysafe.android.tokenization.domain.model.paymentHandle.PSTokenizeOptions
import com.paysafe.android.tokenization.domain.model.paymentHandle.ShippingDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.SimulatorType
import com.paysafe.android.tokenization.domain.model.paymentHandle.TransactionType
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.Profile
import com.paysafe.android.tokenization.domain.model.paymentHandle.venmo.VenmoRequest

/**
 * Tokenization parameters needed for a Venmo payment.
 */
data class PSVenmoTokenizeOptions(

    override val amount: Int,

    override val currencyCode: String,

    override val transactionType: TransactionType,

    override val merchantRefNum: String,

    override val billingDetails: BillingDetails? = null,

    override val profile: Profile? = null,

    override val accountId: String,

    override val merchantDescriptor: MerchantDescriptor? = null,

    override val shippingDetails: ShippingDetails? = null,

    override val simulator: SimulatorType = SimulatorType.EXTERNAL,

    /**
     * HTTPS App Link passed to Braintree as `VenmoClient.appLinkReturnUrl`.
     *
     * Braintree uses it to send the user back from Venmo. When this app is the
     * verified default handler of the URL, the return targets are
     * `{appLinkReturnUrl}/success`, `/cancel`, and `/error`. If the link is not
     * verified, Braintree falls back to [customUrlScheme].
     *
     * The host must:
     * - publish `https://<host>/.well-known/assetlinks.json` with
     *   `relation` `delegate_permission/common.handle_all_urls`, `namespace`
     *   `android_app`, the host `applicationId`, and the SHA-256 of the app
     *   signing certificate
     * - add an `android:autoVerify` https intent filter for that host on the
     *   activity that receives the return
     * - register the same domain in the Braintree Control Panel
     * - more information how to set up it: [https://developer.android.com/training/app-links/configure-assetlinks]
     */
    val appLinkReturnUrl: String,

    /** The details of the Venmo account used for the transaction.  */
    val venmoRequest: VenmoRequest? = null,

    /**
     * Return URL scheme supplied by the host app.
     * Browser switch uses `${applicationId}.braintree`, registered on the checkout activity.
     */
    val customUrlScheme: String? = null

) : PSTokenizeOptions
