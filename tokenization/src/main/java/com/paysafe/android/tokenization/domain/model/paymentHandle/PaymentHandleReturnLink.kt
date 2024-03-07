/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle

/**
 * The URL endpoints to redirect the customer to after a redirection to an alternative payment
 * or 3D Secure site. You can customize the return URL based on the transaction status.
 */
data class PaymentHandleReturnLink(

    /** This is the link type that allows different endpoints to be targeted depending
     * on the end state of the transaction. */
    val relation: ReturnLinkRelation,

    /** This is the URI of the resource. */
    val href: String,

    /** This is the HTTP method. */
    val method: String?,
)