/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paypal.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.paypal.android.corepayments.CoreConfig
import com.paypal.android.corepayments.PayPalSDKError
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutClient
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutListener
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutRequest
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutResult
import com.paysafe.android.PaysafeSDK
import com.paysafe.android.core.util.LocalLog
import com.paysafe.android.paypal.util.toPaypalEnvironment

internal class PayPalWebCheckoutActivity : FragmentActivity(), PayPalWebCheckoutListener {

    internal lateinit var payPalWebCheckoutClient: PayPalWebCheckoutClient
    internal lateinit var orderId: String

    internal var receivedIntent: Intent? = null

    private val observer = object : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            handleLifecycleObserverOnResume()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleOnCreate()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleOnNewIntent(intent)
    }

    override fun onPayPalWebCanceled() {
        LocalLog.d(
            "WebCheckoutActivity",
            "onPayPalWebCanceled receivedIntent = ${receivedIntent?.data}"
        )
        val uri = receivedIntent?.data
        if (uri == null) {
            finishActivityWithResult(PayPalConstants.RESULT_CANCELED)
            return
        }
        val opTypeParameter = "opType"
        val isOpTypeParameterAvailable = uri.queryParameterNames.contains(opTypeParameter)
        val opTypeQueryValue = if (isOpTypeParameterAvailable)
            uri.getQueryParameter(opTypeParameter)
        else
            null
        val successfulOpType = "payment"
        val cancelOpTypeValue = "cancel"
        finishActivityWithResult(
            when (opTypeQueryValue) {
                successfulOpType -> PayPalConstants.RESULT_SUCCESS
                cancelOpTypeValue -> PayPalConstants.RESULT_CANCELED
                else -> PayPalConstants.RESULT_FAILED
            }
        )
    }

    override fun onPayPalWebFailure(error: PayPalSDKError) {
        LocalLog.d("WebCheckoutActivity", "onPayPalWebFailure")
        finishActivityWithResult(PayPalConstants.RESULT_FAILED)
    }

    override fun onPayPalWebSuccess(result: PayPalWebCheckoutResult) {
        LocalLog.d("WebCheckoutActivity", "onPayPalWebSuccess")
        finishActivityWithResult(PayPalConstants.RESULT_SUCCESS)
    }

    internal fun handleOnCreate() {
        onBackPressedDispatcher.addCallback(this, provideOnBackPressedCallback())

        val orderIdFromIntent = intent?.getStringExtra(PayPalConstants.INTENT_EXTRA_ORDER_ID)
        val clientIdFromIntent = intent?.getStringExtra(PayPalConstants.INTENT_EXTRA_CLIENT_ID)
        if (orderIdFromIntent == null || clientIdFromIntent == null) {
            finishActivityWithResult(PayPalConstants.RESULT_FAILED)
            return
        }

        orderId = orderIdFromIntent
        payPalWebCheckoutClient = providePayPalWebCheckoutClient(clientIdFromIntent)
        // adding the observer in order to be able to start the PayPalCheckout after
        // PayPalWebCheckoutClient hooks for and receives onResume call
        lifecycle.addObserver(observer)
    }

    internal fun handleOnNewIntent(intent: Intent?) {
        receivedIntent = intent
    }

    internal fun handleLifecycleObserverOnResume() {
        startPayPalCheckout()
        removeLifecycleObserver()
    }

    internal fun providePayPalWebCheckoutClient(
        clientId: String,
    ) = PayPalWebCheckoutClient(
        activity = this,
        configuration = CoreConfig(
            clientId = clientId,
            environment = PaysafeSDK.getPSApiClient().environment.toPaypalEnvironment()
        ),
        urlScheme = "com.paysafe.android.paypal"
    )

    internal fun startPayPalCheckout() {
        LocalLog.d("WebCheckoutActivity", "startPayPalCheckout")
        payPalWebCheckoutClient.listener = this@PayPalWebCheckoutActivity
        payPalWebCheckoutClient.start(
            PayPalWebCheckoutRequest(
                orderId = orderId
            )
        )
    }

    internal fun removeLifecycleObserver() {
        lifecycle.removeObserver(this@PayPalWebCheckoutActivity.observer)
    }


    private fun provideOnBackPressedCallback(): OnBackPressedCallback =
        object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                finishActivityWithResult(PayPalConstants.RESULT_CANCELED)
            }

        }

    internal fun finishActivityWithResult(result: Int) {
        setResult(result)
        finish()
    }

}