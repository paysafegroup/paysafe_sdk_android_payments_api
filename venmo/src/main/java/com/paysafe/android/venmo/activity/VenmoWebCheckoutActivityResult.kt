package com.paysafe.android.venmo.activity

import com.paysafe.android.venmo.domain.model.BraintreeDetailsConfiguration

internal sealed interface VenmoWebCheckoutActivityResult {

    data class Success(
        val configuration: BraintreeDetailsConfiguration
    ) : VenmoWebCheckoutActivityResult

    object Canceled : VenmoWebCheckoutActivityResult

    object Failure : VenmoWebCheckoutActivityResult
}