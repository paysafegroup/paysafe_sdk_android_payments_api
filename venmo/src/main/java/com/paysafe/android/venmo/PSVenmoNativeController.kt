package com.paysafe.android.venmo

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LifecycleCoroutineScope
import com.paysafe.android.brainTreeDetails.domain.models.BraintreeDetailsRequest
import com.paysafe.android.brainTreeDetails.domain.models.DeviceData
import com.paysafe.android.brainTreeDetails.domain.models.PayerInfo
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.core.util.LocalLog
import com.paysafe.android.tokenization.PSTokenization
import com.paysafe.android.tokenization.PSTokenizationService
import com.paysafe.android.venmo.activity.VenmoConstants
import com.paysafe.android.venmo.activity.VenmoWebCheckoutActivity
import com.paysafe.android.venmo.exception.errorName
import com.paysafe.android.venmo.exception.genericApiErrorException


internal class PSVenmoNativeController internal constructor(
    activityResultCaller: ActivityResultCaller,
    lifecycleScope: LifecycleCoroutineScope,
    private val psApiClient: PSApiClient,
    tokenizationService: PSTokenizationService,
) : PSVenmoController(
    lifecycleScope,
    psApiClient,
    tokenizationService
) {

    internal val activityResultLauncher: ActivityResultLauncher<Intent> =
        initializeActivityResult(activityResultCaller)

    private var jwtToken:String = ""

    companion object {

        internal fun provideController(
            activityResultCaller: ActivityResultCaller,
            psApiClient: PSApiClient,
            lifecycleScope: LifecycleCoroutineScope
        ): PSVenmoNativeController = PSVenmoNativeController(
            activityResultCaller = activityResultCaller,
            lifecycleScope = lifecycleScope,
            psApiClient = psApiClient,
            tokenizationService = PSTokenization(psApiClient),
        )
    }

    override fun dispose() {
        LocalLog.d("PSVenmoWebController", "dispose")
        activityResultLauncher.unregister()
        super.dispose()
    }

    override fun startVenmoCheckout(
        context: Context,
        orderId: String,
        sessionToken: String,
        clientToken: String,
        customUrlScheme: String?
    ) {
        LocalLog.d("PSVenmoNativeController", "startVenmoCheckout")
        jwtToken = sessionToken
        val intent = Intent(context, VenmoWebCheckoutActivity::class.java).apply {
            putExtra("SESSION_TOKEN", sessionToken)
            putExtra("CLIENT_TOKEN", clientToken)
            putExtra("CUSTOM_URL_SCHEME", customUrlScheme)
        }
        activityResultLauncher.launch(intent)
    }

    private fun initializeActivityResult(
        activityResultCaller: ActivityResultCaller
    ): ActivityResultLauncher<Intent> = activityResultCaller
        .registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleActivityResult(result.resultCode, result.data)
        }

    private fun createRequest(data: Intent?): BraintreeDetailsRequest {
        return BraintreeDetailsRequest(
            paymentMethodNonce = data?.getStringExtra("VENMO_ACCOUNT_NONCE") ?: "",
            paymentMethodDeviceData = DeviceData(correlationId = psApiClient.getCorrelationId()),
            paymentMethodJwtToken = data?.getStringExtra("JWT_SESSION_TOKEN") ?: jwtToken,
            paymentMethodPayerInfo = PayerInfo(
                firstName = data?.getStringExtra("FIRST_NAME") ?: "",
                lastName = data?.getStringExtra("LAST_NAME") ?: "",
                phoneNumber = data?.getStringExtra("PHONE_NUMBER") ?: "",
                email = data?.getStringExtra("EMAIL") ?: "",
                userName = data?.getStringExtra("USER_NAME") ?: "",
                externalId = data?.getStringExtra("EXTERNAL_ID") ?: ""
            ),
            errorCode = if (data != null) null else "VENMO_CANCELED"
        )
    }

    fun handleActivityResult(resultCode: Int, data: Intent?) {
        when (resultCode) {
            VenmoConstants.RESULT_SUCCESS -> {
                LocalLog.d("PSVenmoNativeController", "activity result RESULT_SUCCESS")
                val request = createRequest(data)
                onVenmoListenerSuccess(request)
            }

            VenmoConstants.RESULT_FAILED -> {
                val request = createRequest(data = null)
                onVenmoFailure(request)
            }

            else -> {
                LocalLog.d("PSVenmoNativeController", "Unknown result code $resultCode received!")
                val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
                psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                tokenizationAlreadyInProgress = false
                tokenizeCallback?.onFailure(paysafeException)
            }
        }
    }
}