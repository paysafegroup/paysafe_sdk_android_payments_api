package com.paysafe.android.venmo.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BraintreeDetailsConfiguration(
    val account: String?,
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String?,
    val email: String?,
    val userName: String?,
    val externalId: String?,
    val deviceDataCorrelationId: String?,
    val sessionToken: String?,
    ): Parcelable