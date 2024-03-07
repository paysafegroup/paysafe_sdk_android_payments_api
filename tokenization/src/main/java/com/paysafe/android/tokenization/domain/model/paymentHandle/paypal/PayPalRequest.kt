/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.paypal

/**
 * The details of the PayPal account used for the transaction.
 */
data class PayPalRequest(

    /** The source of funds for this payment, the email address of the consumer or payer.
     * Number of characters required: >= 1 & <= 50 */
    val consumerId: String,

    /** A label that overrides the business name in the merchant's PayPal account
     * on the PayPal checkout pages.
     * Number of characters required: >= 1 & <= 127 */
    val recipientDescription: String?,

    /** The preferred language code for the consumer. */
    val language: PSPayPalLanguage?,

    /** The shipping preference. */
    val shippingPreference: PSPayPalShippingPreference?,

    /** Note to be displayed on the PayPal page.
     * Number of characters required: >= 1 & <= 4000 */
    val consumerMessage: String?,

    /** Order description to display on PayPal page.
     * If merchant does not set this field it defaults to 'Payment for order'.
     * Number of characters required: >= 1 & <= 127 */
    val orderDescription: String?

)
