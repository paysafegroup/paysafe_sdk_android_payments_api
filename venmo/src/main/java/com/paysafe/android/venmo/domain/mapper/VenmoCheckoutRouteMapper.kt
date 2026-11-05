package com.paysafe.android.venmo.domain.mapper

import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleAction
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleTokenStatus
import com.paysafe.android.venmo.domain.model.VenmoCheckoutRoute
import com.paysafe.android.venmo.domain.model.VenmoCheckoutRoute.Failed
import com.paysafe.android.venmo.domain.model.VenmoCheckoutRoute.FirstTime
import com.paysafe.android.venmo.domain.model.VenmoCheckoutRoute.NoVenmoCheckout
import com.paysafe.android.venmo.domain.model.VenmoCheckoutRoute.Payable
import com.paysafe.android.venmo.domain.model.VenmoCheckoutRoute.Recurring

internal class VenmoCheckoutRouteMapper {

    operator fun invoke(
        status: String?,
        action: String?,
        brainTreeCustomerId: String?,
        clientToken: String?,
        sessionToken: String?,
        clientMetadataId: String?,
    ): VenmoCheckoutRoute = when {
        PaymentHandleTokenStatus.PAYABLE.status == status -> Payable
        PaymentHandleTokenStatus.FAILED.status == status -> Failed(status)
        PaymentHandleTokenStatus.EXPIRED.status == status -> Failed(status)
        sessionToken == null || clientToken == null ->
            Failed("Missing sessionToken or clientToken")

        action == PaymentHandleAction.REDIRECT.name &&
                (status == PaymentHandleTokenStatus.INITIATED.status ||
                        status == PaymentHandleTokenStatus.PROCESSING.status) ->
            getRouteBaseOnBrainTree(
                brainTreeCustomerId = brainTreeCustomerId,
                sessionToken = sessionToken,
                clientToken = clientToken,
                clientMetadataId = clientMetadataId
            )

        else -> NoVenmoCheckout
    }

    private fun getRouteBaseOnBrainTree(
        brainTreeCustomerId: String?,
        sessionToken: String,
        clientToken: String,
        clientMetadataId: String?
    ): VenmoCheckoutRoute = if (!brainTreeCustomerId.isNullOrBlank()) {
        Recurring(
            sessionToken = sessionToken,
            clientToken = clientToken,
            clientMetadataId = clientMetadataId,
        )
    } else {
        FirstTime(
            sessionToken = sessionToken,
            clientToken = clientToken,
            clientMetadataId = clientMetadataId,
        )
    }
}