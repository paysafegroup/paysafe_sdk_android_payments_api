/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paypal

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import com.paypal.android.corepayments.CoreConfig
import com.paypal.android.corepayments.PayPalSDKError
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutClient
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutListener
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutRequest
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutResult
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.core.util.LocalLog
import com.paysafe.android.paypal.util.toPaypalEnvironment
import com.paysafe.android.tokenization.PSTokenization
import com.paysafe.android.tokenization.PSTokenizationService

internal class PSPayPalNativeController internal constructor(
    lifecycleScope: LifecycleCoroutineScope,
    psApiClient: PSApiClient,
    tokenizationService: PSTokenizationService,
    private val payPalNativeCheckoutClient: PayPalNativeCheckoutClient
) : PSPayPalController(
    lifecycleScope,
    psApiClient,
    tokenizationService
), PayPalNativeCheckoutListener {

    companion object {

        internal fun provideController(
            application: Application,
            clientId: String,
            psApiClient: PSApiClient,
            applicationId: String,
            lifecycleScope: LifecycleCoroutineScope
        ): PSPayPalNativeController = PSPayPalNativeController(
            lifecycleScope = lifecycleScope,
            psApiClient = psApiClient,
            tokenizationService = PSTokenization(psApiClient),
            payPalNativeCheckoutClient = providePayPalNativeCheckoutClient(
                application = application,
                clientId = clientId,
                applicationId = applicationId,
                psApiClient = psApiClient
            )
        )

        private fun providePayPalNativeCheckoutClient(
            application: Application,
            clientId: String,
            applicationId: String,
            psApiClient: PSApiClient
        ) = PayPalNativeCheckoutClient(
            application = application,
            coreConfig = CoreConfig(
                clientId = clientId,
                environment = psApiClient.environment.toPaypalEnvironment()
            ),
            returnUrl = "$applicationId://paypalpay"
        )

    }

    override fun dispose() {
        LocalLog.d("PSPayPalNativeController", "dispose")
        payPalNativeCheckoutClient.listener = null
        super.dispose()
    }

    override fun startPayPalCheckout(context: Context, orderId: String) {
        LocalLog.d("PSPayPalNativeController", "startPayPalCheckout")
        payPalNativeCheckoutClient.listener = this@PSPayPalNativeController
        payPalNativeCheckoutClient.startCheckout(
            PayPalNativeCheckoutRequest(
                orderId = orderId
            )
        )
    }

    override fun onPayPalCheckoutCanceled() {
        LocalLog.d("PSPayPalNativeController", "onPayPalCheckoutCanceled")
        payPalNativeCheckoutClient.listener = null
        onPayPalCanceled()
    }

    override fun onPayPalCheckoutFailure(error: PayPalSDKError) {
        LocalLog.d("PSPayPalNativeController", "onPayPalCheckoutFailure")
        payPalNativeCheckoutClient.listener = null
        onPayPalFailure()
    }

    override fun onPayPalCheckoutStart() {
        LocalLog.d("PSPayPalNativeController", "onPayPalCheckoutStart")
    }

    override fun onPayPalCheckoutSuccess(result: PayPalNativeCheckoutResult) {
        LocalLog.d("PSPayPalNativeController", "onPayPalCheckoutSuccess")
        payPalNativeCheckoutClient.listener = null
        onPayPalSuccess()
    }

}
