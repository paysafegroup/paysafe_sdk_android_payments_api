/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.profile

/**
 * Customer profile.
 */
data class Profile(

    /** First name. */
    val firstName: String? = null,

    /** Last name. */
    val lastName: String? = null,

    /** Locale. */
    val locale: ProfileLocale? = null,

    /** Merchant customer id. */
    val merchantCustomerId: String? = null,

    /** Date of birth. */
    val dateOfBirth: DateOfBirth? = null,

    /** Email. */
    val email: String? = null,

    /** Phone. */
    val phone: String? = null,

    /** Mobile. */
    val mobile: String? = null,

    /** Gender. */
    val gender: Gender? = null,

    /** Nationality. */
    val nationality: String? = null,

    /** Identity documents. */
    val identityDocuments: List<IdentityDocument>? = null

)
