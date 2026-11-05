package com.paysafe.android.venmo

import android.content.Context
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LifecycleCoroutineScope
import com.paysafe.android.brainTreeDetails.domain.models.BraintreeDetailsRequest
import com.paysafe.android.brainTreeDetails.domain.models.DeviceData
import com.paysafe.android.brainTreeDetails.domain.models.PayerInfo
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.core.util.LocalLog
import com.paysafe.android.currencyConverter.CurrencyConverter
import com.paysafe.android.tokenization.PSTokenization
import com.paysafe.android.tokenization.PSTokenizationService
import com.paysafe.android.venmo.activity.VenmoWebCheckoutActivity
import com.paysafe.android.venmo.activity.VenmoWebCheckoutActivityResult
import com.paysafe.android.venmo.domain.model.BraintreeDetailsConfiguration
import com.paysafe.android.venmo.domain.model.VenmoWebCheckoutConfiguration
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

    internal val activityResultLauncher: ActivityResultLauncher<VenmoWebCheckoutConfiguration> =
        initializeActivityResult(activityResultCaller)

    private var jwtToken: String = ""

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
        profileId: String?,
        amount: Int,
        appLinkReturnUrl: String,
        customUrlScheme: String?,
        clientMetadataId: String?,
    ) {
        LocalLog.d("PSVenmoNativeController", "startVenmoCheckout")
        jwtToken = sessionToken

        val currencyConverter = CurrencyConverter(CurrencyConverter.defaultCurrenciesMap())
        val amountString =
            currencyConverter.convert(amount = amount, forCurrency = "USD").toString()

        collectDeviceData(context, clientToken, clientMetadataId) { deviceData ->

            val configuration = VenmoWebCheckoutConfiguration(
                orderId = orderId,
                sessionToken = sessionToken,
                clientToken = clientToken,
                profileId = profileId,
                amount = amountString,
                appLinkReturnUrl = appLinkReturnUrl,
                customUrlScheme = customUrlScheme,
                clientMetadataId = clientMetadataId,
                deviceDataCorrelationId = deviceData?.takeIf { it.isNotBlank() }
            )

            activityResultLauncher.launch(configuration)
        }
    }

    private fun initializeActivityResult(
        activityResultCaller: ActivityResultCaller
    ): ActivityResultLauncher<VenmoWebCheckoutConfiguration> = activityResultCaller
        .registerForActivityResult(VenmoWebCheckoutActivity.Open()) {
            handleActivityResult(it)
        }

    internal fun createRequest(data: BraintreeDetailsConfiguration?): BraintreeDetailsRequest {
        return BraintreeDetailsRequest(
            paymentMethodNonce = data?.account.orEmpty(),
            paymentMethodDeviceData = data?.deviceDataCorrelationId?.let { DeviceData(correlationId = it) },
            paymentMethodJwtToken = data?.sessionToken ?: jwtToken,
            paymentMethodPayerInfo = PayerInfo(
                firstName = data?.firstName.orEmpty(),
                lastName = data?.lastName.orEmpty(),
                phoneNumber = data?.phoneNumber.orEmpty(),
                email = data?.email.orEmpty(),
                userName = data?.userName.orEmpty(),
                externalId = data?.externalId.orEmpty()
            ),
            errorCode = if (data != null) null else "VENMO_CANCELED"
        )
    }

    internal fun handleActivityResult(result: VenmoWebCheckoutActivityResult) {
        when (result) {
            is VenmoWebCheckoutActivityResult.Success -> {
                LocalLog.d("PSVenmoNativeController", "activity result RESULT_SUCCESS")
                val request = createRequest(result.configuration)
                onVenmoListenerSuccess(request)
            }

            VenmoWebCheckoutActivityResult.Failure -> onVenmoFailure(createRequest(data = null))

            VenmoWebCheckoutActivityResult.Canceled -> {
                LocalLog.d("PSVenmoNativeController", "Venmo returns RESULT_CANCEL")
                val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
                psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                tokenizationAlreadyInProgress = false
                tokenizeCallback?.onFailure(paysafeException)
            }
        }
    }
}