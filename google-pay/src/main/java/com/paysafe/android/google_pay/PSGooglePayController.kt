/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.google_pay

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.entity.value
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.core.domain.exception.PaysafeException
import com.paysafe.android.core.util.getUserNameFromApiKey
import com.paysafe.android.core.util.isAmountNotValid
import com.paysafe.android.core.util.isCurrencyCodeValid
import com.paysafe.android.core.util.isNotAllDigits
import com.paysafe.android.core.util.isNotNullOrEmpty
import com.paysafe.android.core.util.launchCatching
import com.paysafe.android.google_pay.button.PSGooglePayPaymentMethodConfig
import com.paysafe.android.google_pay.data.mapper.toDomain
import com.paysafe.android.google_pay.data.model.PaymentInformationResponse
import com.paysafe.android.google_pay.domain.mapper.toPaymentHandleRequest
import com.paysafe.android.google_pay.domain.model.GoogleCardNetwork
import com.paysafe.android.google_pay.domain.model.GoogleMerchantInfo
import com.paysafe.android.google_pay.domain.model.PSGooglePayConfig
import com.paysafe.android.google_pay.domain.model.PSGooglePayTokenizeOptions
import com.paysafe.android.google_pay.exception.amountShouldBePositiveException
import com.paysafe.android.google_pay.exception.currencyCodeInvalidIsoException
import com.paysafe.android.google_pay.exception.errorName
import com.paysafe.android.google_pay.exception.genericApiErrorException
import com.paysafe.android.google_pay.exception.googlePayUserCancelledException
import com.paysafe.android.google_pay.exception.gpNotSupportedException
import com.paysafe.android.google_pay.exception.improperlyCreatedMerchantAccountConfigException
import com.paysafe.android.google_pay.exception.invalidAccountIdForPaymentMethodException
import com.paysafe.android.google_pay.exception.invalidAccountIdParameterException
import com.paysafe.android.google_pay.exception.noAvailablePaymentMethodsException
import com.paysafe.android.google_pay.exception.paymentHandleCreationFailedException
import com.paysafe.android.google_pay.exception.tokenizationAlreadyInProgressException
import com.paysafe.android.paymentmethods.PaymentMethodsService
import com.paysafe.android.paymentmethods.PaymentMethodsServiceImpl
import com.paysafe.android.paymentmethods.domain.model.GoogleAuthMethod
import com.paysafe.android.paymentmethods.domain.model.PaymentMethod
import com.paysafe.android.paymentmethods.domain.model.PaymentMethodType
import com.paysafe.android.tokenization.PSTokenization
import com.paysafe.android.tokenization.PSTokenizationService
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleTokenStatus
import com.paysafe.android.tokenization.domain.model.paymentHandle.googlepay.GooglePayPaymentToken
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.ref.WeakReference

internal class PSGooglePayController internal constructor(
    activityResultCaller: ActivityResultCaller,
    lifecycleScope: LifecycleCoroutineScope,
    private val googlePayConfig: PSGooglePayConfig,
    private val merchantId: String,
    private val psApiClient: PSApiClient,
    private val tokenizationService: PSTokenizationService,
    private val paymentsClient: PaymentsClient
) {

    internal var tokenizationAlreadyInProgress = false
    internal val activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    internal val lifecycleScopeWeakRef: WeakReference<LifecycleCoroutineScope>
    internal var resultCallback: PSGooglePayTokenizeCallback? = null
    internal var tokenizeOptions: PSGooglePayTokenizeOptions? = null

    private val json = Json {
        ignoreUnknownKeys = true
    }

    internal lateinit var allowedAuthMethods: List<GoogleAuthMethod>
    internal lateinit var allowedCardNetworks: List<GoogleCardNetwork>
    internal lateinit var merchantName: String

    init {
        activityResultLauncher = initializeActivityResult(activityResultCaller)
        lifecycleScopeWeakRef = WeakReference(lifecycleScope)
    }

    internal fun setupController(
        allowedAuthMethods: List<GoogleAuthMethod>,
        allowedCardNetworks: List<GoogleCardNetwork>,
        merchantName: String
    ) {
        this.allowedAuthMethods = allowedAuthMethods
        this.allowedCardNetworks = allowedCardNetworks
        this.merchantName = merchantName
    }

    companion object {
        suspend fun initialize(
            googlePayConfig: PSGooglePayConfig,
            psApiClient: PSApiClient,
            context: Context,
            activityResultCaller: ActivityResultCaller,
            lifecycleScope: LifecycleCoroutineScope,
            mainDispatcher: CoroutineDispatcher = Dispatchers.Main
        ): PSResult<PSGooglePayController> {
            val merchantId = getUserNameFromApiKey(psApiClient.apiKey)
            if (merchantId == null) {
                val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
                psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                return PSResult.Failure(paysafeException)
            }

            val controller: PSGooglePayController
            val paymentMethodService = PaymentMethodsServiceImpl(psApiClient)

            withContext(mainDispatcher) {
                controller = createPSGooglePayController(
                    context = context,
                    activityResultCaller = activityResultCaller,
                    lifecycleScope = lifecycleScope,
                    googlePayConfig = googlePayConfig,
                    merchantId = merchantId,
                    psApiClient = psApiClient
                )
            }

            val validatePaymentMethodResult = validatePaymentMethods(
                controller, googlePayConfig, paymentMethodService, psApiClient
            )

            if (validatePaymentMethodResult is PSResult.Failure) {
                controller.dispose()
                return validatePaymentMethodResult
            }

            val isGooglePayAvailableResult = fetchCanUseGooglePay(
                controller = controller,
                requestBillingAddress = googlePayConfig.requestBillingAddress,
                psApiClient = psApiClient,
                paymentsClient = controller.paymentsClient
            )

            return if (isGooglePayAvailableResult is PSResult.Failure) {
                controller.dispose()
                isGooglePayAvailableResult
            } else {
                with(googlePayConfig) {
                    logInitializeEvent(
                        allowedCardNetworks = controller.allowedCardNetworks,
                        psApiClient = psApiClient,
                        merchantIdentifier = merchantId,
                        countryCode = countryCode
                    )
                }
                PSResult.Success(controller)
            }
        }

        internal fun createPSGooglePayController(
            context: Context,
            activityResultCaller: ActivityResultCaller,
            lifecycleScope: LifecycleCoroutineScope,
            googlePayConfig: PSGooglePayConfig,
            merchantId: String,
            psApiClient: PSApiClient,
        ): PSGooglePayController = PSGooglePayController(
            activityResultCaller = activityResultCaller,
            lifecycleScope = lifecycleScope,
            googlePayConfig = googlePayConfig,
            merchantId = merchantId,
            psApiClient = psApiClient,
            tokenizationService = PSTokenization(psApiClient),
            paymentsClient = GooglePayManager.createPaymentsClient(context, psApiClient.environment)
        )

        internal suspend fun validatePaymentMethods(
            controller: PSGooglePayController,
            googlePayConfig: PSGooglePayConfig,
            paymentMethodService: PaymentMethodsService,
            psApiClient: PSApiClient,
            ioDispatcher: CoroutineDispatcher = Dispatchers.IO
        ): PSResult<Unit> = withContext(ioDispatcher) {
            val accountId = googlePayConfig.accountId
            val currencyCode = googlePayConfig.currencyCode

            validateAccountIdAndCurrencyCode(
                accountId,
                currencyCode,
                psApiClient
            ).takeIf { it != null }?.also { return@withContext it }

            val paymentMethodsResult = paymentMethodService.getPaymentMethods(
                googlePayConfig.currencyCode
            )

            if (paymentMethodsResult is PSResult.Failure)
                return@withContext paymentMethodsResult

            val paymentMethods = paymentMethodsResult.value()
            val googlePaymentMethod = getGooglePaymentMethod(paymentMethods, googlePayConfig)

            return@withContext if (googlePaymentMethod == null) {
                return@withContext if (paymentMethods?.firstOrNull {
                        it.isPaymentMethodCardAndGooglePay() &&
                                it.accountId == accountId &&
                                it.currencyCode == currencyCode &&
                                it.accountConfiguration?.getAvailableCardTypes().isNullOrEmpty()
                    } != null) { // no available card types
                    val paysafeException =
                        noAvailablePaymentMethodsException(psApiClient.getCorrelationId())
                    psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)

                    PSResult.Failure(paysafeException)
                } else return@withContext if (paymentMethods?.firstOrNull {
                        it.accountId == accountId &&
                                it.paymentMethod != PaymentMethodType.CARD
                    } != null) { // accountId is present but not for CARD payment method
                    val paysafeException =
                        invalidAccountIdForPaymentMethodException(psApiClient.getCorrelationId())
                    psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)

                    PSResult.Failure(paysafeException)
                } else {
                    val paysafeException =
                        improperlyCreatedMerchantAccountConfigException(psApiClient.getCorrelationId())
                    psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)

                    PSResult.Failure(paysafeException)
                }
            } else {
                setupGooglePay(controller, googlePaymentMethod)
                PSResult.Success()
            }
        }

        private fun validateAccountIdAndCurrencyCode(
            accountId: String,
            currencyCode: String,
            psApiClient: PSApiClient
        ): PSResult.Failure? {
            if (accountId.isNotAllDigits()) {
                val paysafeException =
                    invalidAccountIdParameterException(psApiClient.getCorrelationId())
                psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                return PSResult.Failure(paysafeException)
            }
            if (!isCurrencyCodeValid(currencyCode)) {
                val paysafeException =
                    currencyCodeInvalidIsoException(psApiClient.getCorrelationId())
                psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                return PSResult.Failure(paysafeException)
            }
            return null
        }

        internal fun getGooglePaymentMethod(
            paymentMethods: List<PaymentMethod>?,
            googlePayConfig: PSGooglePayConfig,
        ) = paymentMethods?.firstOrNull {
            it.isPaymentMethodCardAndGooglePay() &&
                    it.isAccountIdAndCurrencyCodeValid(googlePayConfig) &&
                    it.isAccountConfigurationDataNotNullOrEmpty()
        }

        private fun PaymentMethod.isPaymentMethodCardAndGooglePay() =
            paymentMethod == PaymentMethodType.CARD &&
                    accountConfiguration?.isGooglePay == true

        private fun PaymentMethod.isAccountIdAndCurrencyCodeValid(googlePayConfig: PSGooglePayConfig) =
            accountId == googlePayConfig.accountId &&
                    currencyCode == googlePayConfig.currencyCode

        private fun PaymentMethod.isAccountConfigurationDataNotNullOrEmpty() =
            accountConfiguration?.getAvailableCardTypes().isNotNullOrEmpty() &&
                    accountConfiguration?.googlePayConfig?.merchantName.isNotNullOrEmpty() &&
                    accountConfiguration?.googlePayConfig?.paymentMethods.isNotNullOrEmpty()

        internal fun setupGooglePay(
            controller: PSGooglePayController,
            googlePaymentMethod: PaymentMethod
        ) {
            val allowedAuthMethods =
                googlePaymentMethod.accountConfiguration?.googlePayConfig?.paymentMethods!!
            val allowedCardNetworks =
                googlePaymentMethod.accountConfiguration?.getAvailableCardTypes()
                    ?.mapNotNull { GoogleCardNetwork.fromPSCardType(it) }!!
            val merchantName =
                googlePaymentMethod.accountConfiguration?.googlePayConfig?.merchantName!!

            controller.setupController(
                allowedAuthMethods = allowedAuthMethods,
                allowedCardNetworks = allowedCardNetworks,
                merchantName = merchantName
            )

            GooglePayManager.allowCreditCards =
                googlePaymentMethod.accountConfiguration?.isGooglePayAllowCreditCards() ?: false
        }

        internal suspend fun fetchCanUseGooglePay(
            controller: PSGooglePayController,
            requestBillingAddress: Boolean,
            psApiClient: PSApiClient,
            paymentsClient: PaymentsClient
        ): PSResult<Unit> {
            val isReadyToPayJson = GooglePayManager.isReadyToPayRequest(
                controller.allowedAuthMethods, controller.allowedCardNetworks, requestBillingAddress
            )
            val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString())
            return try {
                val task = paymentsClient.isReadyToPay(request)
                val isReadyToPay = task.await()
                if (isReadyToPay) {
                    PSResult.Success()
                } else {
                    val paysafeException = gpNotSupportedException(psApiClient.getCorrelationId())
                    psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                    PSResult.Failure(paysafeException)
                }
            } catch (exception: ApiException) {
                val paysafeException = gpNotSupportedException(psApiClient.getCorrelationId())
                psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                PSResult.Failure(paysafeException)
            }
        }

        internal fun logInitializeEvent(
            allowedCardNetworks: List<GoogleCardNetwork>,
            psApiClient: PSApiClient,
            merchantIdentifier: String,
            countryCode: String,
        ) {
            val supportedNetworks = allowedCardNetworks
                .toSet()
                .joinToString(prefix = "[", postfix = "]") {
                    when (it) {
                        GoogleCardNetwork.AMEX -> "AmEx"
                        GoogleCardNetwork.DISCOVER -> "Discover"
                        GoogleCardNetwork.JCB -> "JCB"
                        GoogleCardNetwork.MASTERCARD -> "MasterCard"
                        GoogleCardNetwork.VISA -> "Visa"
                    }
                }
            val eventContent = LogEventContent(merchantIdentifier, countryCode, supportedNetworks)
            val message = "Options passed on PSGooglePayContext initialize: ${
                Json.encodeToString(eventContent)
            }"
            psApiClient.logEvent(message)
        }
    }

    fun tokenize(
        options: PSGooglePayTokenizeOptions,
        callback: PSGooglePayTokenizeCallback,
    ) {
        if (tokenizationAlreadyInProgress) {
            val paysafeException =
                tokenizationAlreadyInProgressException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
            callback.onFailure(paysafeException)
            return
        }
        tokenizationAlreadyInProgress = true
        if (isAmountNotValid(options.amount)) {
            val paysafeException = amountShouldBePositiveException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
            tokenizationAlreadyInProgress = false
            callback.onFailure(paysafeException)
            return
        }

        val paymentDataRequestJson =
            GooglePayManager.getPaymentDataRequest(
                priceCents = options.amount.toLong(),
                googleMerchantInfo = GoogleMerchantInfo(
                    merchantId = merchantId,
                    merchantName = merchantName
                ),
                countryCode = googlePayConfig.countryCode,
                currencyCode = googlePayConfig.currencyCode,
                allowedAuthMethods = allowedAuthMethods,
                allowedCardNetworks = allowedCardNetworks,
                requestBillingAddress = googlePayConfig.requestBillingAddress
            )
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        paymentsClient.loadPaymentData(request).addOnCompleteListener { completedTask ->
            onLoadPaymentDataComplete(completedTask, options, callback)
        }
    }

    fun getPaymentMethodConfig() = PSGooglePayPaymentMethodConfig(
        merchantId = merchantId,
        allowedAuthMethods = allowedAuthMethods,
        allowedCardNetworks = allowedCardNetworks,
        requestBillingAddress = googlePayConfig.requestBillingAddress
    )

    fun dispose() {
        resultCallback = null
        activityResultLauncher.unregister()
        if (lifecycleScopeWeakRef.get() != null)
            lifecycleScopeWeakRef.clear()
    }

    private fun initializeActivityResult(
        activityResultCaller: ActivityResultCaller,
    ) =
        activityResultCaller.registerForActivityResult(StartIntentSenderForResult()) { result: ActivityResult ->
            when (result.resultCode) {
                RESULT_OK -> onActivityResult(result.data)

                RESULT_CANCELED -> onActivityResultCanceled()
            }
        }

    internal fun onActivityResult(intent: Intent?) {
        if (intent != null) {
            onActivityResultValid(intent)
        } else {
            val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
            tokenizationAlreadyInProgress = false
            resultCallback?.onFailure(paysafeException)
        }
    }

    internal fun onActivityResultValid(intent: Intent) {
        val paymentData = PaymentData.getFromIntent(intent)
        val options = tokenizeOptions
        if (paymentData != null && options != null) {
            onSuccessfulPaymentData(
                paymentData,
                lifecycleScopeWeakRef.get(),
                tokenizationService,
                resultCallback,
                options
            )
        } else {
            val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
            tokenizationAlreadyInProgress = false
            resultCallback?.onFailure(paysafeException)
        }
    }

    internal fun onActivityResultCanceled() {
        val paysafeException = googlePayUserCancelledException(psApiClient.getCorrelationId())
        psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
        tokenizationAlreadyInProgress = false
        resultCallback?.onCancelled(paysafeException)
    }

    internal fun onLoadPaymentDataComplete(
        completedTask: Task<PaymentData>,
        options: PSGooglePayTokenizeOptions,
        callback: PSGooglePayTokenizeCallback
    ) {
        if (completedTask.isSuccessful) {
            val paymentData = completedTask.result
            onSuccessfulPaymentData(
                paymentData,
                lifecycleScopeWeakRef.get(),
                tokenizationService,
                callback,
                options
            )
        } else {
            when (val exception = completedTask.exception) {
                is ResolvableApiException -> {
                    resultCallback = callback
                    tokenizeOptions = options
                    activityResultLauncher.launch(
                        IntentSenderRequest.Builder(exception.resolution).build()
                    )
                }

                else -> {
                    val paysafeException =
                        genericApiErrorException(psApiClient.getCorrelationId())
                    psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                    tokenizationAlreadyInProgress = false
                    callback.onFailure(paysafeException)
                }
            }
        }
    }

    internal fun onSuccessfulPaymentData(
        paymentData: PaymentData,
        lifecycleScope: LifecycleCoroutineScope?,
        tokenizationService: PSTokenizationService,
        callback: PSGooglePayTokenizeCallback?,
        googlePayTokenizeOptions: PSGooglePayTokenizeOptions,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    ) {
        if (lifecycleScope == null) {
            val paysafeException =
                genericApiErrorException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
            tokenizationAlreadyInProgress = false
            callback?.onFailure(paysafeException)
            return // Couldn't complete operation because lifecycleScope is disposed!!
        }

        val googlePayToken = googlePayTokenFromResponse(paymentData)

        lifecycleScope.launchCatching(ioDispatcher) {
            val paymentHandle = tokenizationService.tokenize(
                googlePayTokenizeOptions.toPaymentHandleRequest(googlePayToken)
            ).value()!!
            tokenizationAlreadyInProgress = false

            withContext(mainDispatcher) {
                if (paymentHandle.status == PaymentHandleTokenStatus.PAYABLE.name) {
                    callback?.onSuccess(paymentHandle.paymentHandleToken)
                } else {
                    val paysafeException =
                        paymentHandleCreationFailedException(psApiClient.getCorrelationId())
                    psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                    callback?.onFailure(paysafeException)
                }
            }
        }.onFailure {
            tokenizationAlreadyInProgress = false
            if (it is PaysafeException)
                callback?.onFailure(it)
            else {
                val paysafeException =
                    genericApiErrorException(psApiClient.getCorrelationId())
                psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                callback?.onFailure(paysafeException)
            }
        }
    }

    private fun googlePayTokenFromResponse(paymentData: PaymentData): GooglePayPaymentToken {
        val paymentInformationResponse =
            json.decodeFromString<PaymentInformationResponse>(paymentData.toJson())
        return paymentInformationResponse.toDomain()
    }
}

@Serializable
internal data class LogEventContent(
    @SerialName("merchantIdentifier")
    val merchantIdentifier: String,
    @SerialName("countryCode")
    val countryCode: String,
    @SerialName("supportedNetworks")
    val supportedNetworks: String
)