/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paypal

import android.app.Application
import android.content.Context
import androidx.activity.result.ActivityResultCaller
import androidx.lifecycle.LifecycleCoroutineScope
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.entity.value
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.core.domain.exception.PaysafeException
import com.paysafe.android.core.util.LocalLog
import com.paysafe.android.core.util.isAmountNotValid
import com.paysafe.android.core.util.isCurrencyCodeValid
import com.paysafe.android.core.util.launchCatching
import com.paysafe.android.paymentmethods.PaymentMethodsService
import com.paysafe.android.paymentmethods.PaymentMethodsServiceImpl
import com.paysafe.android.paymentmethods.domain.model.PaymentMethodType
import com.paysafe.android.paypal.domain.mapper.toPaymentHandleRequest
import com.paysafe.android.paypal.domain.model.PSPayPalConfig
import com.paysafe.android.paypal.domain.model.PSPayPalRenderType
import com.paysafe.android.paypal.domain.model.PSPayPalTokenizeOptions
import com.paysafe.android.paypal.exception.amountShouldBePositiveException
import com.paysafe.android.paypal.exception.currencyCodeInvalidIsoException
import com.paysafe.android.paypal.exception.errorName
import com.paysafe.android.paypal.exception.genericApiErrorException
import com.paysafe.android.paypal.exception.improperlyCreatedMerchantAccountConfigException
import com.paysafe.android.paypal.exception.invalidAccountIdForPaymentMethodException
import com.paysafe.android.paypal.exception.payPalFailedAuthorizationException
import com.paysafe.android.paypal.exception.payPalUserCancelledException
import com.paysafe.android.paypal.exception.tokenizationAlreadyInProgressException
import com.paysafe.android.tokenization.PSTokenizationService
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandle
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleReturnLink
import com.paysafe.android.tokenization.domain.model.paymentHandle.ReturnLinkRelation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.ref.WeakReference

internal abstract class PSPayPalController internal constructor(
    lifecycleScope: LifecycleCoroutineScope,
    private val psApiClient: PSApiClient,
    private val tokenizationService: PSTokenizationService
) {

    internal val lifecycleScopeWeakRef: WeakReference<LifecycleCoroutineScope>
    internal var tokenizeCallback: PSPayPalTokenizeCallback? = null
    internal var paymentHandle: PaymentHandle? = null
    internal var tokenizationAlreadyInProgress = false

    init {
        lifecycleScopeWeakRef = WeakReference(lifecycleScope)
    }

    companion object {
        suspend fun initialize(
            activityResultCaller: ActivityResultCaller,
            application: Application,
            lifecycleScope: LifecycleCoroutineScope,
            config: PSPayPalConfig,
            psApiClient: PSApiClient
        ): PSResult<PSPayPalController> {
            LocalLog.d("PSPayPalController", "initialize")
            val currencyCode = config.currencyCode
            if (!isCurrencyCodeValid(currencyCode)) {
                val paysafeException =
                    currencyCodeInvalidIsoException(psApiClient.getCorrelationId())
                psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                return PSResult.Failure(paysafeException)
            }

            val psPayPalWebController: PSPayPalWebController? =
                if (config.renderType is PSPayPalRenderType.PSPayPalWebRenderType) {
                    PSPayPalWebController.provideController(
                        activityResultCaller = activityResultCaller,
                        psApiClient = psApiClient,
                        lifecycleScope = lifecycleScope
                    )
                } else
                    null

            val paymentMethodService: PaymentMethodsService = PaymentMethodsServiceImpl(psApiClient)
            val validatePaymentMethodsResult: PSResult<String> = validatePaymentMethods(
                currencyCode = currencyCode,
                accountId = config.accountId,
                paymentMethodService = paymentMethodService,
                psApiClient = psApiClient
            )

            return when (validatePaymentMethodsResult) {
                is PSResult.Failure -> {
                    psPayPalWebController?.dispose()
                    validatePaymentMethodsResult
                }

                is PSResult.Success -> {
                    handleValidatePaymentMethodsResultSuccess(
                        validatePaymentMethodsResult = validatePaymentMethodsResult,
                        psApiClient = psApiClient,
                        config = config,
                        application = application,
                        lifecycleScope = lifecycleScope,
                        psPayPalWebController = psPayPalWebController
                    )
                }
            }
        }

        internal fun handleValidatePaymentMethodsResultSuccess(
            validatePaymentMethodsResult: PSResult.Success<String>,
            psApiClient: PSApiClient,
            config: PSPayPalConfig,
            application: Application,
            lifecycleScope: LifecycleCoroutineScope,
            psPayPalWebController: PSPayPalWebController?
        ): PSResult<PSPayPalController> {
            val clientId = validatePaymentMethodsResult.value
            return if (clientId.isNullOrBlank()) {
                val paysafeException =
                    improperlyCreatedMerchantAccountConfigException(psApiClient.getCorrelationId())
                psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                PSResult.Failure(paysafeException)
            } else {
                logInitializeEvent(psApiClient, config)

                val controller =
                    if (config.renderType is PSPayPalRenderType.PSPayPalNativeRenderType)
                        PSPayPalNativeController.provideController(
                            application = application,
                            clientId = clientId,
                            psApiClient = psApiClient,
                            applicationId = config.renderType.applicationId,
                            lifecycleScope = lifecycleScope
                        )
                    else
                        psPayPalWebController?.apply {
                            setupController(clientId)
                        }

                PSResult.Success(controller)
            }
        }

        internal suspend fun validatePaymentMethods(
            currencyCode: String,
            accountId: String,
            paymentMethodService: PaymentMethodsService,
            psApiClient: PSApiClient,
            ioDispatcher: CoroutineDispatcher = Dispatchers.IO
        ): PSResult<String> = withContext(ioDispatcher) {
            val paymentMethodsResult = paymentMethodService.getPaymentMethods(currencyCode)

            if (paymentMethodsResult is PSResult.Failure)
                return@withContext paymentMethodsResult

            val paymentMethods = paymentMethodsResult.value()
            val clientId = paymentMethods?.firstOrNull {
                it.paymentMethod == PaymentMethodType.PAYPAL &&
                        it.accountId == accountId &&
                        it.currencyCode == currencyCode &&
                        it.accountConfiguration?.clientId != null
            }?.accountConfiguration?.clientId
                ?: return@withContext if (paymentMethods?.firstOrNull {
                        it.accountId == accountId &&
                                it.paymentMethod != PaymentMethodType.PAYPAL
                    } != null) { // accountId is present but not for PAYPAL payment method
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

            LocalLog.d("PSPayPalController", "PayPal payment method is valid")
            return@withContext PSResult.Success(clientId)
        }

        private fun logInitializeEvent(psApiClient: PSApiClient, psPayPalConfig: PSPayPalConfig) {
            val eventContent: LogEventContent
            with(psPayPalConfig) {
                eventContent = LogEventContent(currencyCode, accountId)
            }
            val message = "Options passed on PSPayPalContext initialize: ${
                Json.encodeToString(eventContent)
            }"
            psApiClient.logEvent(message)
        }
    }

    abstract fun startPayPalCheckout(context: Context, orderId: String)

    suspend fun tokenize(
        context: Context,
        payPalTokenizeOptions: PSPayPalTokenizeOptions,
        callback: PSPayPalTokenizeCallback,
        mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    ) {
        if (tokenizationAlreadyInProgress) {
            val paysafeException =
                tokenizationAlreadyInProgressException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
            callback.onFailure(paysafeException)
            return
        }
        tokenizationAlreadyInProgress = true
        if (isAmountNotValid(payPalTokenizeOptions.amount)) {
            val paysafeException = amountShouldBePositiveException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
            tokenizationAlreadyInProgress = false
            callback.onFailure(paysafeException)
            return
        }
        LocalLog.d("PSPayPalController", "tokenize with $payPalTokenizeOptions")
        tokenizeCallback = callback
        val returnLinks = provideReturnLinks()
        psApiClient.customSDKSource = "PaysafeJSV1"
        val result = tokenizationService.tokenize(
            payPalTokenizeOptions.toPaymentHandleRequest(returnLinks)
        )
        psApiClient.customSDKSource = null
        val lifecycleScope = lifecycleScopeWeakRef.get()
        if (lifecycleScope == null) {
            LocalLog.d(
                "PSPayPalController",
                "Cannot continue PayPal payment as the lifecycle scope is null."
            )
            val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
            tokenizationAlreadyInProgress = false
            tokenizeCallback?.onFailure(paysafeException)
            return
        }
        lifecycleScope.launch(mainDispatcher) {
            when (result) {
                is PSResult.Success -> handleTokenizeResultSuccess(context, result)
                is PSResult.Failure -> handleTokenizeResultFailure(result)
            }
        }
    }

    internal fun handleTokenizeResultSuccess(
        context: Context,
        result: PSResult.Success<PaymentHandle>
    ) {
        val paymentHandle = result.value
        if (paymentHandle == null) {
            LocalLog.d("PSPayPalController", "Received payment handle is null!")
            val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
            tokenizationAlreadyInProgress = false
            tokenizeCallback?.onFailure(paysafeException)
            return
        }
        this@PSPayPalController.paymentHandle = paymentHandle

        val orderId = paymentHandle.payPalOrderId
        if (orderId == null) {
            val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
            tokenizationAlreadyInProgress = false
            tokenizeCallback?.onFailure(paysafeException)
            return
        }
        startPayPalCheckout(context, orderId)
    }

    internal fun handleTokenizeResultFailure(result: PSResult.Failure) {
        tokenizationAlreadyInProgress = false
        tokenizeCallback?.onFailure(result.exception)
    }

    open fun dispose() {
        LocalLog.d("PSPayPalController", "dispose")
        tokenizeCallback = null
        paymentHandle = null
        if (lifecycleScopeWeakRef.get() != null)
            lifecycleScopeWeakRef.clear()
    }


    internal fun onPayPalSuccess(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    ) {
        LocalLog.d("PSPayPalController", "onPayPalSuccess")
        val paymentHandle = this.paymentHandle
        if (paymentHandle == null) {
            LocalLog.d(
                "PSPayPalController",
                "Cannot refresh token as the payment handle is null."
            )
            val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
            tokenizationAlreadyInProgress = false
            tokenizeCallback?.onFailure(paysafeException)
            return
        }
        val lifecycleScope = lifecycleScopeWeakRef.get()
        if (lifecycleScope == null) {
            LocalLog.d(
                "PSPayPalController",
                "Cannot refresh token as the lifecycle scope is null."
            )
            val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
            tokenizationAlreadyInProgress = false
            tokenizeCallback?.onFailure(paysafeException)
            return
        }
        lifecycleScope.launchCatching(ioDispatcher) {
            val refreshTokenResult = tokenizationService.refreshToken(paymentHandle)
            val refreshedPaymentHandle = refreshTokenResult.value()
            withContext(mainDispatcher) {
                if (refreshedPaymentHandle != null) {
                    LocalLog.d("PSPayPalController", "Token was refreshed with success.")
                    tokenizationAlreadyInProgress = false
                    tokenizeCallback?.onSuccess(refreshedPaymentHandle.paymentHandleToken)
                } else {
                    LocalLog.d("PSPayPalController", "Refreshed PaymentHandle is null")
                    val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
                    psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                    tokenizationAlreadyInProgress = false
                    tokenizeCallback?.onFailure(paysafeException)
                }
            }
        }.onFailure {
            LocalLog.d("PSPayPalController", "Refresh token failed with ${it.message}")
            tokenizationAlreadyInProgress = false
            if (it is PaysafeException)
                tokenizeCallback?.onFailure(it)
            else {
                val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
                psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                tokenizeCallback?.onFailure(paysafeException)
            }
        }
    }

    internal fun onPayPalFailure() {
        LocalLog.d("PSPayPalController", "onPayPalFailure")
        val paysafeException =
            payPalFailedAuthorizationException(psApiClient.getCorrelationId())
        psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
        tokenizationAlreadyInProgress = false
        tokenizeCallback?.onFailure(paysafeException)
    }

    internal fun onPayPalCanceled() {
        LocalLog.d("PSPayPalController", "onPayPalCanceled")
        val paysafeException = payPalUserCancelledException(psApiClient.getCorrelationId())
        psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
        tokenizationAlreadyInProgress = false
        tokenizeCallback?.onCancelled(paysafeException)
    }

    private fun provideReturnLinks(): List<PaymentHandleReturnLink> = listOf(
        PaymentHandleReturnLink(
            relation = ReturnLinkRelation.DEFAULT,
            href = provideDefaultReturnLink(),
            method = "GET"
        ),
        PaymentHandleReturnLink(
            relation = ReturnLinkRelation.ON_COMPLETED,
            href = provideSuccessReturnLink(),
            method = "GET"
        ),
        PaymentHandleReturnLink(
            relation = ReturnLinkRelation.ON_FAILED,
            href = provideFailedReturnLink(),
            method = "GET"
        ),
        PaymentHandleReturnLink(
            relation = ReturnLinkRelation.ON_CANCELLED,
            href = provideCancelledReturnLink(),
            method = "GET"
        )
    )

    private fun provideSuccessReturnLink(): String =
        "https://usgaminggamblig.com/payment/return/success"

    private fun provideFailedReturnLink(): String =
        "https://usgaminggamblig.com/payment/return/failed"

    private fun provideCancelledReturnLink(): String =
        "https://usgaminggamblig.com/payment/return/cancelled"

    private fun provideDefaultReturnLink(): String =
        "https://usgaminggamblig.com/payment/return/"


}

@Serializable
internal data class LogEventContent(
    @SerialName("currencyCode")
    val currencyCode: String,
    @SerialName("accountId")
    val accountId: String
)
