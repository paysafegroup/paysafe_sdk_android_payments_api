/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paypal

import android.app.Application
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
import com.paysafe.android.paypal.domain.model.PSPayPalConfig
import com.paysafe.android.paypal.domain.model.PSPayPalTokenizeOptions
import com.paysafe.android.paypal.exception.sdkNotInitializedException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PSPayPalContext internal constructor(
    private val controller: PSPayPalController
) : PSPayPal {

    companion object {

        fun initialize(
            fragment: Fragment,
            payPalConfig: PSPayPalConfig,
            callback: PSCallback<PSPayPalContext>
        ) = initialize(
            activityResultCaller = fragment,
            application = fragment.requireActivity().application,
            lifecycleScope = fragment.lifecycleScope,
            config = payPalConfig,
            callback = callback
        )

        fun initialize(
            activity: ComponentActivity,
            payPalConfig: PSPayPalConfig,
            callback: PSCallback<PSPayPalContext>
        ) = initialize(
            activityResultCaller = activity,
            application = activity.application,
            lifecycleScope = activity.lifecycleScope,
            config = payPalConfig,
            callback = callback
        )

        private fun initialize(
            activityResultCaller: ActivityResultCaller,
            application: Application,
            lifecycleScope: LifecycleCoroutineScope,
            config: PSPayPalConfig,
            callback: PSCallback<PSPayPalContext>,
            mainDispatcher: CoroutineDispatcher = Dispatchers.Main
        ) {
            if (!PaysafeSDK.isInitialized()) {
                callback.onFailure(sdkNotInitializedException())
                return
            }
            lifecycleScope.launchCatching {
                val controllerResult = PSPayPalController.initialize(
                    activityResultCaller = activityResultCaller,
                    application = application,
                    lifecycleScope = lifecycleScope,
                    config = config,
                    psApiClient = PaysafeSDK.getPSApiClient()
                )
                withContext(mainDispatcher) {
                    when (controllerResult) {
                        is PSResult.Failure ->
                            callback.onFailure(controllerResult.exception)

                        is PSResult.Success -> {
                            val controller = controllerResult.value
                            if (controller == null) {
                                callback.onFailure(Exception("Cannot create PSPayPalContext as the controller received is null."))
                                return@withContext
                            }
                            callback.onSuccess(PSPayPalContext(controller))
                        }
                    }
                }
            }.onFailure {
                callback.onFailure(Exception(it.message))
            }
        }
    }

    override suspend fun tokenize(
        context: Context,
        payPalTokenizeOptions: PSPayPalTokenizeOptions,
        callback: PSPayPalTokenizeCallback
    ) {
        controller.tokenize(context, payPalTokenizeOptions, callback)
    }

    override fun dispose() = controller.dispose()
}