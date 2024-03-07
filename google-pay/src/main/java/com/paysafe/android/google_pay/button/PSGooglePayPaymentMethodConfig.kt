/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.google_pay.button

import com.paysafe.android.google_pay.domain.model.GoogleCardNetwork
import com.paysafe.android.paymentmethods.domain.model.GoogleAuthMethod

data class PSGooglePayPaymentMethodConfig(
    val merchantId: String,
    val allowedAuthMethods: List<GoogleAuthMethod>,
    val allowedCardNetworks: List<GoogleCardNetwork>,
    val requestBillingAddress: Boolean
)