package com.paysafe.android.venmo.activity

import android.content.Intent
import androidx.core.content.IntentCompat
import com.paysafe.android.venmo.domain.model.BraintreeDetailsConfiguration

internal class VenmoWebCheckoutActivityResultParser {

    fun parseResult(resultCode: Int, intent: Intent?): VenmoWebCheckoutActivityResult =
        when (resultCode) {
            VenmoConstants.RESULT_SUCCESS -> VenmoWebCheckoutActivityResult.Success(
                intent.loadConfiguration()
            )

            VenmoConstants.RESULT_CANCELED -> VenmoWebCheckoutActivityResult.Canceled

            else -> VenmoWebCheckoutActivityResult.Failure
        }

    private fun Intent?.loadConfiguration(): BraintreeDetailsConfiguration =
        this?.let {
            IntentCompat.getParcelableExtra(
                it,
                VenmoConstants.VENMO_DATA_EXTRA,
                BraintreeDetailsConfiguration::class.java
            )
        }
            ?: throw IllegalStateException(MISSING_BRAINTREE_DATA_MESSAGE)

    companion object {

        private const val MISSING_BRAINTREE_DATA_MESSAGE = "Braintree data is missing"
    }
}