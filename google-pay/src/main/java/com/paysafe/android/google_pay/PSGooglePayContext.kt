/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.google_pay

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.paysafe.android.PaysafeSDK
import com.paysafe.android.core.data.entity.PSCallback
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.util.launchCatching
import com.paysafe.android.google_pay.domain.model.PSGooglePayConfig
import com.paysafe.android.google_pay.domain.model.PSGooglePayTokenizeOptions
import com.paysafe.android.google_pay.exception.sdkNotInitializedException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PSGooglePayContext internal constructor(
    private var controller: PSGooglePayController
) : PSGooglePay {

    companion object {
        fun initialize(
            fragment: Fragment,
            psGooglePayConfig: PSGooglePayConfig,
            callback: PSCallback<PSGooglePayContext>
        ) = initialize(
            psGooglePayConfig,
            fragment.requireContext(),
            fragment,
            fragment.lifecycleScope,
            callback
        )

        fun initialize(
            activity: ComponentActivity,
            psGooglePayConfig: PSGooglePayConfig,
            callback: PSCallback<PSGooglePayContext>
        ) = initialize(
            psGooglePayConfig,
            activity,
            activity,
            activity.lifecycleScope,
            callback
        )

        private fun initialize(
            googlePayConfig: PSGooglePayConfig,
            context: Context,
            activityResultCaller: ActivityResultCaller,
            lifecycleScope: LifecycleCoroutineScope,
            callback: PSCallback<PSGooglePayContext>,
            mainDispatcher: CoroutineDispatcher = Dispatchers.Main
        ) {
            if (!PaysafeSDK.isInitialized()) {
                callback.onFailure(sdkNotInitializedException())
                return
            }
            lifecycleScope.launchCatching {
                val controllerResult = PSGooglePayController.initialize(
                    googlePayConfig = googlePayConfig,
                    psApiClient = PaysafeSDK.getPSApiClient(),
                    context = context,
                    activityResultCaller = activityResultCaller,
                    lifecycleScope = lifecycleScope
                )
                withContext(mainDispatcher) {
                    when (controllerResult) {
                        is PSResult.Failure ->
                            callback.onFailure(controllerResult.exception)

                        is PSResult.Success -> {
                            val controller = controllerResult.value
                            if (controller == null) {
                                callback.onFailure(Exception("Cannot create PSGooglePayContext as the controller received is null."))
                                return@withContext
                            }
                            callback.onSuccess(PSGooglePayContext(controller))
                        }
                    }
                }
            }.onFailure {
                callback.onFailure(it)
            }
        }
    }

    override fun tokenize(
        googlePayTokenizeOptions: PSGooglePayTokenizeOptions,
        callback: PSGooglePayTokenizeCallback
    ) = controller.tokenize(
        googlePayTokenizeOptions,
        callback
    )

    override fun providePaymentMethodConfig() = controller.getPaymentMethodConfig()

    fun dispose() = controller.dispose()
}