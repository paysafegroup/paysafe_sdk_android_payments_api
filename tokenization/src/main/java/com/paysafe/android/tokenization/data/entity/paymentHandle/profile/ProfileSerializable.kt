/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileSerializable(
    /** First name. */
    @SerialName("firstName")
    val firstName: String? = null,

    /** Last name. */
    @SerialName("lastName")
    val lastName: String? = null,

    /** Locale. */
    @SerialName("locale")
    val locale: ProfileLocaleSerializable? = null,

    /** Merchant customer id. */
    @SerialName("merchantCustomerId")
    val merchantCustomerId: String? = null,

    /** Date of birth. */
    @SerialName("dateOfBirth")
    val dateOfBirth: DateOfBirthSerializable? = null,

    /** Email. */
    @SerialName("email")
    val email: String? = null,

    /** Phone. */
    @SerialName("phone")
    val phone: String? = null,

    /** Mobile. */
    @SerialName("mobile")
    val mobile: String? = null,

    /** Gender. */
    @SerialName("gender")
    val gender: GenderSerializable? = null,

    /** Nationality. */
    @SerialName("nationality")
    val nationality: String? = null,

    /** Identity documents. */
    @SerialName("identityDocuments")
    val identityDocuments: List<IdentityDocumentSerializable?>? = null
)