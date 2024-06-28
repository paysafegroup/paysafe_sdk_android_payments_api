package com.paysafe.android.venmo

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
import com.paysafe.android.venmo.domain.model.PSVenmoConfig
import com.paysafe.android.venmo.domain.model.PSVenmoTokenizeOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PSVenmoContext internal constructor(
    private val controller: PSVenmoController
) : PSVenmo {
    companion object {
        fun initialize(
            fragment: Fragment,
            venmoConfig: PSVenmoConfig,
            callback: PSCallback<PSVenmoContext>
        ) = initialize(
            activityResultCaller = fragment,
            lifecycleScope = fragment.lifecycleScope,
            config = venmoConfig,
            callback = callback
        )

        fun initialize(
            activity: ComponentActivity,
            venmoConfig: PSVenmoConfig,
            callback: PSCallback<PSVenmoContext>
        ) = initialize(
            activityResultCaller = activity,
            lifecycleScope = activity.lifecycleScope,
            config = venmoConfig,
            callback = callback
        )

        private fun initialize(
            activityResultCaller: ActivityResultCaller,
            lifecycleScope: LifecycleCoroutineScope,
            config: PSVenmoConfig,
            callback: PSCallback<PSVenmoContext>,
            mainDispatcher: CoroutineDispatcher = Dispatchers.Main
        ) {

            lifecycleScope.launchCatching {
                val controllerResult = PSVenmoController.initialize(
                    activityResultCaller = activityResultCaller,
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
                                callback.onFailure(Exception("Cannot create PSVenmoContext as the controller received is null."))
                                return@withContext
                            }
                            callback.onSuccess(PSVenmoContext(controller))
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
        psVenmoTokenizeOptions: PSVenmoTokenizeOptions,
        callback: PSVenmoTokenizeCallback
    ) {
        controller.tokenize(context,psVenmoTokenizeOptions,callback)
    }

    override fun dispose() = controller.dispose()
}
