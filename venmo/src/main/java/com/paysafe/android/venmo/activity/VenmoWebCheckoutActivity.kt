package com.paysafe.android.venmo.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.net.toUri
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.braintreepayments.api.venmo.VenmoAccountNonce
import com.braintreepayments.api.venmo.VenmoClient
import com.braintreepayments.api.venmo.VenmoLauncher
import com.braintreepayments.api.venmo.VenmoPaymentAuthRequest
import com.braintreepayments.api.venmo.VenmoPaymentAuthResult
import com.braintreepayments.api.venmo.VenmoPaymentMethodUsage
import com.braintreepayments.api.venmo.VenmoPendingRequest
import com.braintreepayments.api.venmo.VenmoRequest
import com.braintreepayments.api.venmo.VenmoResult
import com.paysafe.android.venmo.R
import com.paysafe.android.venmo.VenmoRequestStorageManage
import com.paysafe.android.venmo.domain.model.BraintreeDetailsConfiguration
import com.paysafe.android.venmo.domain.model.VenmoWebCheckoutConfiguration

internal class VenmoWebCheckoutActivity : AppCompatActivity() {

    lateinit var venmoClient: VenmoClient
    lateinit var venmoLauncher: VenmoLauncher
    lateinit var configuration: VenmoWebCheckoutConfiguration
    private val venmoStorageManger: VenmoRequestStorageManage = VenmoRequestStorageManage()

    private val observer = object : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            handleLifecycleObserverOnResume()
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venmo_web_checkout)
        handleOnCreate(savedInstanceState)

        if (!::venmoLauncher.isInitialized) {
            venmoLauncher = VenmoLauncher(this)
        }
        venmoClient = VenmoClient(
            context = this,
            authorization = configuration.clientToken,
            appLinkReturnUrl = configuration.appLinkReturnUrl.toUri(),
            deepLinkFallbackUrlScheme = configuration.customUrlScheme
        )

        restorePendingRequest()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(VenmoConstants.VENMO_CHECKOUT_CONFIGURATION_EXTRA, configuration)
    }

    override fun onResume() {
        super.onResume()
        handleOnResume()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleOnNewIntent(intent)
    }

    internal fun handleOnResume() {
        launchVenmo(intent)
    }

    internal fun handleOnNewIntent(intent: Intent) {
        setIntent(intent)
        launchVenmo(intent)
    }

    fun handleOnCreate(savedInstanceState: Bundle?) {
        onBackPressedDispatcher.addCallback(this, provideOnBackPressedCallback())

        configuration = savedInstanceState?.let {
            BundleCompat.getParcelable(
                it, VenmoConstants.VENMO_CHECKOUT_CONFIGURATION_EXTRA,
                VenmoWebCheckoutConfiguration::class.java
            )
        } ?: IntentCompat.getParcelableExtra(
            intent, VenmoConstants.VENMO_CHECKOUT_CONFIGURATION_EXTRA,
            VenmoWebCheckoutConfiguration::class.java
        ) ?: throw IllegalStateException("Missing required configuration")

        lifecycle.addObserver(observer)
    }

    internal fun handleLifecycleObserverOnResume() {
        removeLifecycleObserver()
    }

    private fun restorePendingRequest() {
        val venmoRequestString = venmoStorageManger.fetch(this)
        if (!venmoRequestString.isNullOrEmpty()) {
            try {
                venmoLauncher.restorePendingRequest(venmoRequestString)
            } catch (_: Exception) {
                venmoStorageManger.clear(this)
            }
        }
    }

    internal fun startPaymentAuth() {
        val request = VenmoRequest(VenmoPaymentMethodUsage.MULTI_USE).apply {
            collectCustomerBillingAddress = true
            collectCustomerShippingAddress = true
            profileId = this@VenmoWebCheckoutActivity.configuration.profileId
            shouldVault = false
            totalAmount = configuration.amount
        }

        venmoClient.createPaymentAuthRequest(this, request) { paymentAuthRequest ->
            when (paymentAuthRequest) {
                is VenmoPaymentAuthRequest.ReadyToLaunch -> handleReadyToLaunch(paymentAuthRequest)
                is VenmoPaymentAuthRequest.Failure -> onVenmoFailure(paymentAuthRequest.error)
            }
        }
    }

    private fun handleReadyToLaunch(paymentAuthRequest: VenmoPaymentAuthRequest.ReadyToLaunch) {
        when (val pendingRequest = venmoLauncher.launch(this, paymentAuthRequest)) {
            is VenmoPendingRequest.Started ->
                venmoStorageManger.store(this, pendingRequest.pendingRequestString)

            is VenmoPendingRequest.Failure -> onVenmoFailure(pendingRequest.error)
        }
    }

    internal fun launchVenmo(intent: Intent) {
        if (!::venmoLauncher.isInitialized || !::venmoClient.isInitialized) {
            return
        }

        venmoStorageManger.fetch(this)?.let {
            handleStartedAuthRequest(it, intent)
        } ?: startPaymentAuth()
    }

    private fun handleStartedAuthRequest(string: String, intent: Intent) {
        when (
            val paymentAuthResult = venmoLauncher.handleReturnToApp(
                VenmoPendingRequest.Started(string),
                intent
            )
        ) {
            is VenmoPaymentAuthResult.Success -> {
                venmoStorageManger.clear(this)
                completeVenmoFlow(paymentAuthResult)
            }

            is VenmoPaymentAuthResult.NoResult -> {
                venmoStorageManger.clear(this)
                finishActivityWithResult(VenmoConstants.RESULT_CANCELED, null)
            }

            is VenmoPaymentAuthResult.Failure -> {
                venmoStorageManger.clear(this)
                onVenmoFailure(paymentAuthResult.error)
            }
        }
    }

    private fun completeVenmoFlow(paymentAuthResult: VenmoPaymentAuthResult.Success) {
        venmoClient.tokenize(paymentAuthResult) { result ->
            when (result) {
                is VenmoResult.Success -> onVenmoSuccess(result.nonce)
                is VenmoResult.Failure -> onVenmoFailure(result.error)
                is VenmoResult.Cancel -> finishActivityWithResult(
                    VenmoConstants.RESULT_CANCELED,
                    null
                )
            }
        }
    }

    private fun onVenmoSuccess(venmoAccountNonce: VenmoAccountNonce) {
        val resultIntent = Intent().apply {
            putExtra(
                VenmoConstants.VENMO_DATA_EXTRA, BraintreeDetailsConfiguration(
                    account = venmoAccountNonce.string,
                    userName = venmoAccountNonce.username,
                    firstName = venmoAccountNonce.firstName,
                    lastName = venmoAccountNonce.lastName,
                    phoneNumber = venmoAccountNonce.phoneNumber,
                    email = venmoAccountNonce.email,
                    externalId = venmoAccountNonce.externalId,
                    sessionToken = configuration.sessionToken,
                    deviceDataCorrelationId = configuration.deviceDataCorrelationId,
                )
            )
        }

        finishActivityWithResult(VenmoConstants.RESULT_SUCCESS, resultIntent)
    }

    private fun onVenmoFailure(error: Exception) {
        val resultIntent = Intent().apply {
            putExtra("ERROR_MESSAGE", error.message)
        }

        finishActivityWithResult(VenmoConstants.RESULT_FAILED, resultIntent)
    }

    fun removeLifecycleObserver() {
        lifecycle.removeObserver(this@VenmoWebCheckoutActivity.observer)
    }

    internal fun provideOnBackPressedCallback(): OnBackPressedCallback =
        object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                venmoStorageManger.clear(this@VenmoWebCheckoutActivity)
                finishActivityWithResult(VenmoConstants.RESULT_FAILED, null)
            }
        }

    internal fun finishActivityWithResult(result: Int, intent: Intent?) {
        setResult(result, intent)
        finish()
    }

    class Open :
        ActivityResultContract<VenmoWebCheckoutConfiguration, VenmoWebCheckoutActivityResult>() {
        override fun createIntent(
            context: Context,
            input: VenmoWebCheckoutConfiguration
        ): Intent = Intent(context, VenmoWebCheckoutActivity::class.java)
            .putExtras(
                bundleOf(
                    VenmoConstants.VENMO_CHECKOUT_CONFIGURATION_EXTRA to input
                )
            )

        override fun parseResult(
            resultCode: Int,
            intent: Intent?
        ): VenmoWebCheckoutActivityResult =
            VenmoWebCheckoutActivityResultParser().parseResult(resultCode, intent)
    }
}