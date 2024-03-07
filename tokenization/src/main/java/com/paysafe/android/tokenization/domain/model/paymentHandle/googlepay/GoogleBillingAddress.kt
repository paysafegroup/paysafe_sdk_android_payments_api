/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.googlepay

/**
 * Billing address for Google Pay.
 */
data class GoogleBillingAddress(

    /** Name. */
    val name: String? = null,

    /** Postal code. */
    val postalCode: String? = null,

    /** Country code (i.e. US). */
    val countryCode: String? = null,

    /** Phone number. */
    val phoneNumber: String? = null,

    /** Address One. */
    val address1: String? = null,

    /** Address Two. */
    val address2: String? = null,

    /** Address Three. */
    val address3: String? = null,

    /** Locality. */
    val locality: String? = null,

    /** Administrative area (i.e. CA). */
    val administrativeArea: String? = null,

    /** Sorting code. */
    val sortingCode: String? = null
)