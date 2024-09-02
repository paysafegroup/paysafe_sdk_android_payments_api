package com.paysafe.android.venmo.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.braintreepayments.api.BraintreeClient
import com.braintreepayments.api.VenmoAccountNonce
import com.braintreepayments.api.VenmoClient
import com.braintreepayments.api.VenmoListener
import com.braintreepayments.api.VenmoPaymentMethodUsage
import com.braintreepayments.api.VenmoRequest
import com.paysafe.android.venmo.R

internal class VenmoWebCheckoutActivity : AppCompatActivity(), VenmoListener {

    lateinit var venmoClient: VenmoClient
    lateinit var braintreeClient: BraintreeClient
    lateinit var sessionToken: String
    lateinit var clientToken: String
    lateinit var displayAmount: String
    var profileId: String? = null
    lateinit var customUrlScheme: String

    private val observer = object : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            handleLifecycleObserverOnResume()
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venmo_web_checkout)
        handleOnCreate()
        launchVenmo()
    }

    fun handleOnCreate() {
        onBackPressedDispatcher.addCallback(this, provideOnBackPressedCallback())

        val sessionTokenIntent = intent?.getStringExtra(VenmoConstants.INTENT_EXTRA_SESSION_TOKEN)
        val clientTokenIntent = intent?.getStringExtra(VenmoConstants.INTENT_EXTRA_CLIENT_TOKEN)
        val customUrlSchemeIntent =
            intent?.getStringExtra(VenmoConstants.INTENT_EXTRA_CUSTOM_URL_SCHEME)
        val profileIdIntent = intent?.getStringExtra(VenmoConstants.INTENT_EXTRA_PROFILE_ID)
        val displayAmountIntent = intent?.getStringExtra(VenmoConstants.INTENT_EXTRA_AMOUNT)

        if (sessionTokenIntent == null || clientTokenIntent == null ||
            customUrlSchemeIntent == null || displayAmountIntent == null
        ) {
            finishActivityWithResult(VenmoConstants.RESULT_FAILED, null)
            return
        }

        sessionToken = sessionTokenIntent
        clientToken = clientTokenIntent
        customUrlScheme = customUrlSchemeIntent
        profileId = profileIdIntent
        displayAmount = displayAmountIntent

        lifecycle.addObserver(observer)
    }

    public override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    internal fun handleLifecycleObserverOnResume() {
        removeLifecycleObserver()
    }

    fun launchVenmo() {
        braintreeClient =
            BraintreeClient(
                context = this,
                authorization = clientToken,
                returnUrlScheme = customUrlScheme
            )
        venmoClient = VenmoClient(this, braintreeClient)
        venmoClient.setListener(this)

        val request = VenmoRequest(VenmoPaymentMethodUsage.MULTI_USE)

        request.collectCustomerBillingAddress = true
        request.collectCustomerShippingAddress = true
        request.profileId = profileId
        request.shouldVault = false
        request.totalAmount = displayAmount

        if (venmoClient.isVenmoAppSwitchAvailable(this)) {
            venmoClient.tokenizeVenmoAccount(this, request)
        } else if (!isAppInstalled(this)) {
            finishActivityWithResult(VenmoConstants.RESULT_VENMO_APP_IS_NOT_INSTALLED, null)
            venmoClient.showVenmoInGooglePlayStore(this)
        } else {
            venmoClient.tokenizeVenmoAccount(this, request)
        }
    }

    override fun onVenmoSuccess(venmoAccountNonce: VenmoAccountNonce) {

        val resultIntent = Intent().apply {
            putExtra("VENMO_ACCOUNT_NONCE", venmoAccountNonce.string)
            putExtra("JWT_SESSION_TOKEN", sessionToken)
            putExtra("USER_NAME", venmoAccountNonce.username)
            putExtra("FIRST_NAME", venmoAccountNonce.firstName)
            putExtra("LAST_NAME", venmoAccountNonce.lastName)
            putExtra("PHONE_NUMBER", venmoAccountNonce.phoneNumber)
            putExtra("EMAIL", venmoAccountNonce.email)
            putExtra("EXTERNAL_ID", venmoAccountNonce.externalId)
        }
        setResult(VenmoConstants.RESULT_SUCCESS, resultIntent)
        finish()

    }

    override fun onVenmoFailure(error: Exception) {

        val resultIntent = Intent().apply {
            putExtra("ERROR_MESSAGE", error.message)
        }
        setResult(VenmoConstants.RESULT_FAILED, resultIntent)
        finishActivityWithResult(VenmoConstants.RESULT_FAILED, resultIntent)
        finish()
    }

    fun removeLifecycleObserver() {
        lifecycle.removeObserver(this@VenmoWebCheckoutActivity.observer)
    }

    private fun provideOnBackPressedCallback(): OnBackPressedCallback =
        object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                finishActivityWithResult(VenmoConstants.RESULT_FAILED, null)
            }

        }

    internal fun finishActivityWithResult(result: Int, intent: Intent?) {
        setResult(result, intent)
        finish()
    }

    fun isAppInstalled(context: Context): Boolean {
        val packageManager = context.packageManager
        return try {
            packageManager.getPackageInfo(
                VenmoConstants.VENMO_PACKAGE,
                PackageManager.GET_ACTIVITIES
            )
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}