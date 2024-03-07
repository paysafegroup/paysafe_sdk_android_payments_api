/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paypal

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LifecycleCoroutineScope
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.core.util.LocalLog
import com.paysafe.android.paypal.activity.PayPalConstants
import com.paysafe.android.paypal.activity.PayPalWebCheckoutActivity
import com.paysafe.android.paypal.exception.errorName
import com.paysafe.android.paypal.exception.genericApiErrorException
import com.paysafe.android.tokenization.PSTokenization
import com.paysafe.android.tokenization.PSTokenizationService

internal open class PSPayPalWebController internal constructor(
    activityResultCaller: ActivityResultCaller,
    lifecycleScope: LifecycleCoroutineScope,
    private val psApiClient: PSApiClient,
    tokenizationService: PSTokenizationService
) : PSPayPalController(
    lifecycleScope,
    psApiClient,
    tokenizationService
) {

    private lateinit var clientId: String

    internal val activityResultLauncher: ActivityResultLauncher<Intent> =
        initializeActivityResult(activityResultCaller)

    companion object {

        internal fun provideController(
            activityResultCaller: ActivityResultCaller,
            psApiClient: PSApiClient,
            lifecycleScope: LifecycleCoroutineScope
        ): PSPayPalWebController = PSPayPalWebController(
            activityResultCaller = activityResultCaller,
            lifecycleScope = lifecycleScope,
            psApiClient = psApiClient,
            tokenizationService = PSTokenization(psApiClient)
        )

    }

    override fun dispose() {
        LocalLog.d("PSPayPalWebController", "dispose")
        activityResultLauncher.unregister()
        super.dispose()
    }

    override fun startPayPalCheckout(context: Context, orderId: String) {
        LocalLog.d("PSPayPalWebController", "startPayPalCheckout")
        val intent = Intent(context, PayPalWebCheckoutActivity::class.java).apply {
            putExtra(PayPalConstants.INTENT_EXTRA_CLIENT_ID, clientId)
            putExtra(PayPalConstants.INTENT_EXTRA_ORDER_ID, orderId)
        }
        activityResultLauncher.launch(intent)
    }

    internal fun setupController(clientId: String) {
        this.clientId = clientId
    }

    private fun initializeActivityResult(
        activityResultCaller: ActivityResultCaller
    ): ActivityResultLauncher<Intent> = activityResultCaller
        .registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            handleActivityResult(result.resultCode)
        }


    internal fun handleActivityResult(resultCode: Int) {
        when (resultCode) {
            PayPalConstants.RESULT_SUCCESS -> {
                LocalLog.d("PSPayPalWebController", "activity result RESULT_SUCCESS")
                onPayPalSuccess()
            }

            PayPalConstants.RESULT_CANCELED -> {
                LocalLog.d("PSPayPalWebController", "activity result RESULT_CANCELED")
                onPayPalCanceled()
            }

            PayPalConstants.RESULT_FAILED -> {
                LocalLog.d("PSPayPalWebController", "activity result RESULT_FAILED")
                onPayPalFailure()
            }

            else -> {
                LocalLog.d("PSPayPalWebController", "Unknown result code $resultCode received!")
                val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
                psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                tokenizationAlreadyInProgress = false
                tokenizeCallback?.onFailure(paysafeException)
            }
        }
    }

}
