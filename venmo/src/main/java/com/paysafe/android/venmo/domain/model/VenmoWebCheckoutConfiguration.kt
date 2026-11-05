package com.paysafe.android.venmo.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class VenmoWebCheckoutConfiguration(
    val orderId: String,
    val sessionToken: String,
    val clientToken: String,
    val profileId: String?,
    val amount: String,
    val appLinkReturnUrl: String,
    val customUrlScheme: String?,
    val clientMetadataId: String?,
    val deviceDataCorrelationId: String?,
) : Parcelable
