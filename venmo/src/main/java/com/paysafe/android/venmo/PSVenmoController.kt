package com.paysafe.android.venmo

import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultCaller
import androidx.lifecycle.LifecycleCoroutineScope
import com.paysafe.android.brainTreeDetails.data.BrainTreeDetailsService
import com.paysafe.android.brainTreeDetails.data.BrainTreeDetailsServiceImpl
import com.paysafe.android.brainTreeDetails.domain.models.BraintreeDetailsRequest
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
import com.paysafe.android.tokenization.PSTokenizationService
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandle
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleReturnLink
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleTokenStatus
import com.paysafe.android.tokenization.domain.model.paymentHandle.ReturnLinkRelation
import com.paysafe.android.venmo.domain.mapper.toPaymentHandleRequest
import com.paysafe.android.venmo.domain.model.PSVenmoConfig
import com.paysafe.android.venmo.domain.model.PSVenmoTokenizeOptions
import com.paysafe.android.venmo.exception.amountShouldBePositiveException
import com.paysafe.android.venmo.exception.currencyCodeInvalidIsoException
import com.paysafe.android.venmo.exception.errorName
import com.paysafe.android.venmo.exception.genericApiErrorException
import com.paysafe.android.venmo.exception.improperlyCreatedMerchantAccountConfigException
import com.paysafe.android.venmo.exception.invalidAccountIdForPaymentMethodException
import com.paysafe.android.venmo.exception.paymentHandleStatusExpiredOrFailedException
import com.paysafe.android.venmo.exception.tokenizationAlreadyInProgressException
import com.paysafe.android.venmo.exception.venmoFailedAuthorizationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

internal abstract class PSVenmoController internal constructor(
    lifecycleScope: LifecycleCoroutineScope,
    private val psApiClient: PSApiClient,
    private val tokenizationService: PSTokenizationService
) {

    internal val lifecycleScopeWeakRef: WeakReference<LifecycleCoroutineScope>
    internal var tokenizeCallback: PSVenmoTokenizeCallback? = null
    internal var paymentHandle: PaymentHandle? = null
    internal var tokenizationAlreadyInProgress = false

    init {
        lifecycleScopeWeakRef = WeakReference(lifecycleScope)
    }

    companion object {
        suspend fun initialize(
            activityResultCaller: ActivityResultCaller,
            lifecycleScope: LifecycleCoroutineScope,
            config: PSVenmoConfig,
            psApiClient: PSApiClient
        ): PSResult<PSVenmoController> {
            LocalLog.d("PSVenmoController", "initialize")
            val currencyCode = config.currencyCode

            if (!isCurrencyCodeValid(currencyCode)) {
                val paysafeException =
                    currencyCodeInvalidIsoException(psApiClient.getCorrelationId())
                psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                return PSResult.Failure(paysafeException)
            }

            val psVenmoNativeController: PSVenmoNativeController = PSVenmoNativeController.provideController(
                activityResultCaller = activityResultCaller,
                psApiClient = psApiClient,
                lifecycleScope = lifecycleScope
            )

            val paymentMethodService: PaymentMethodsService = PaymentMethodsServiceImpl(psApiClient)
            val validatePaymentMethodsResult: PSResult<String> = validatePaymentMethods(
                currencyCode = currencyCode,
                accountId = config.accountId,
                paymentMethodService = paymentMethodService,
                psApiClient = psApiClient
            )

            return when (validatePaymentMethodsResult) {
                is PSResult.Failure -> {
                    validatePaymentMethodsResult
                }

                is PSResult.Success -> {
                    handleValidatePaymentMethodsResultSuccess(
                        psVenmoNativeController = psVenmoNativeController
                    )
                }
            }
        }

        internal  fun handleValidatePaymentMethodsResultSuccess(
            psVenmoNativeController: PSVenmoNativeController
        ): PSResult<PSVenmoController> {
            return PSResult.Success(psVenmoNativeController)
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

            val venmoPaymentMethod = paymentMethods?.firstOrNull {
                it.paymentMethod == PaymentMethodType.VENMO &&
                        it.accountId == accountId &&
                        it.currencyCode == currencyCode
            }

            if (venmoPaymentMethod != null) {
                LocalLog.d("PSVenmoController", "Venmo payment method is valid")
                return@withContext PSResult.Success()
            }

            return@withContext if (paymentMethods?.firstOrNull {
                    it.accountId == accountId &&
                            it.paymentMethod != PaymentMethodType.VENMO
                } != null) {
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
        }
    }

    internal fun onVenmoFailure(request: BraintreeDetailsRequest) {

        val brainTreeDetailsService: BrainTreeDetailsService = BrainTreeDetailsServiceImpl(psApiClient)

        lifecycleScopeWeakRef.get()?.launch {
            try {
                val brainDetailsResult = brainTreeDetailsService.getBraintreeDetails(request)

                Log.d("DETAILS_RESULT", brainDetailsResult.toString())
                onRefreshToken()
            } catch (e: Exception) {
                // Handle the error as needed
            }
        }
        LocalLog.d("PSVenmoController", "onVenmoFailure")
        val paysafeException =
            venmoFailedAuthorizationException(psApiClient.getCorrelationId())
        psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
        tokenizationAlreadyInProgress = false
        tokenizeCallback?.onFailure(paysafeException)
    }

    internal fun  onVenmoListenerSuccess(request: BraintreeDetailsRequest) {
        val brainTreeDetailsService: BrainTreeDetailsService = BrainTreeDetailsServiceImpl(psApiClient)

        lifecycleScopeWeakRef.get()?.launch {
            try {
                val brainDetailsResult = brainTreeDetailsService.getBraintreeDetails(request)

                Log.d("DETAILS_RESULT", brainDetailsResult.toString())
                onRefreshToken()
            } catch (e: Exception) {
                // Handle the error as needed
                LocalLog.d("PSVenmoController", "Refresh token failed with ${e.message}")
                tokenizationAlreadyInProgress = false
                if (e is PaysafeException)
                    tokenizeCallback?.onFailure(e)
                else {
                    val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
                    psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                    tokenizeCallback?.onFailure(paysafeException)
                }
            }
        } ?: run {
            LocalLog.d("PSVenmoController", "Cannot continue as the lifecycle scope is null.")
            val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
        }
    }

    internal fun onRefreshToken(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    ){
        val paymentHandle = this.paymentHandle
        if (paymentHandle == null) {
            LocalLog.d(
                "PSVenmoController",
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
                "PSVenmoController",
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
                    LocalLog.d("PSVenmoController", "Token was refreshed with success.")
                    tokenizationAlreadyInProgress = false
                    tokenizeCallback?.onSuccess(refreshedPaymentHandle.paymentHandleToken)
                } else {
                    LocalLog.d("PSVenmoController", "Refreshed PaymentHandle is null")
                    val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
                    psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                    tokenizationAlreadyInProgress = false
                    tokenizeCallback?.onFailure(paysafeException)
                }
            }
        }.onFailure {
            LocalLog.d("PSVenmoController", "Refresh token failed with ${it.message}")
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

    suspend fun tokenize(
        context: Context,
        venmoTokenizeOptions: PSVenmoTokenizeOptions,
        callback: PSVenmoTokenizeCallback,
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
        if (isAmountNotValid(venmoTokenizeOptions.amount)) {
            val paysafeException = amountShouldBePositiveException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
            tokenizationAlreadyInProgress = false
            callback.onFailure(paysafeException)
            return
        }

        LocalLog.d("PSVenmoController", "tokenize with $venmoTokenizeOptions")
        tokenizeCallback = callback
        val returnLinks = provideReturnLinks()
        val result = tokenizationService.tokenize(
            venmoTokenizeOptions.toPaymentHandleRequest(returnLinks)
        )
        psApiClient.customSDKSource = null
        val lifecycleScope = lifecycleScopeWeakRef.get()
        if (lifecycleScope == null) {
            LocalLog.d(
                "PSVenmoController",
                "Cannot continue Venmo payment as the lifecycle scope is null."
            )
            val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
            tokenizationAlreadyInProgress = false
            tokenizeCallback?.onFailure(paysafeException)
            return
        }
        lifecycleScope.launch(mainDispatcher) {
            when (result) {
                is PSResult.Success -> handleTokenizeResultSuccess(context, venmoTokenizeOptions.customUrlScheme,result)
                is PSResult.Failure -> handleTokenizeResultFailure(result)
            }
        }
    }

    //Checkout process implementation needed
    abstract fun startVenmoCheckout(context: Context, orderId: String, sessionToken: String, clientToken: String, customUrlScheme: String?)

    internal fun handleTokenizeResultSuccess(
        context: Context,
        customUrlScheme:String?,
        result: PSResult.Success<PaymentHandle>
    ) {
        paymentHandle = result.value
        if (paymentHandle == null) {
            LocalLog.d("PSVenmoController", "Received payment handle is null!")
            val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
            tokenizationAlreadyInProgress = false
            tokenizeCallback?.onFailure(paysafeException)
            return
        }

        when (paymentHandle?.status) {
            PaymentHandleTokenStatus.PAYABLE.status -> {
                tokenizationAlreadyInProgress = false
                tokenizeCallback?.onSuccess(paymentHandle?.paymentHandleToken ?: "")
                return
            }

            PaymentHandleTokenStatus.COMPLETED.status,
            PaymentHandleTokenStatus.INITIATED.status,
            PaymentHandleTokenStatus.PROCESSING.status -> {
                tokenizationAlreadyInProgress = false
            }

            PaymentHandleTokenStatus.FAILED.status,
            PaymentHandleTokenStatus.EXPIRED.status -> {
                tokenizationAlreadyInProgress = false
                val paysafeException =
                    paymentHandleStatusExpiredOrFailedException(
                        status = paymentHandle?.status ?: "",
                        correlationId = psApiClient.getCorrelationId()
                    )
                psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                tokenizeCallback?.onFailure(paysafeException)
                return
            }
        }

        val orderId = paymentHandle?.id ?:""
        val sessionToken = paymentHandle?.gatewayResponse?.jwtToken ?: ""
        val clientToken = paymentHandle?.gatewayResponse?.clientToken ?: ""
        if (orderId.isEmpty()) {
            val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
            tokenizationAlreadyInProgress = false
            tokenizeCallback?.onFailure(paysafeException)
            return
        }
        // Checkout process implementation needed
        tokenizationAlreadyInProgress = false
        startVenmoCheckout(context, orderId, sessionToken, clientToken, customUrlScheme)
    }

    internal fun handleTokenizeResultFailure(result: PSResult.Failure) {
        val request = BraintreeDetailsRequest(
            paymentMethodNonce = null,
            paymentMethodDeviceData = null,
            paymentMethodJwtToken = null,
            paymentMethodPayerInfo = null,
            errorCode = "VENMO_CANCELED"
        )
        tokenizationAlreadyInProgress = false
        tokenizeCallback?.onFailure(result.exception)
        onVenmoFailure(request)
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

    open fun dispose() {
        LocalLog.d("PSVenmoController", "dispose")
        tokenizeCallback = null
        paymentHandle = null
        if (lifecycleScopeWeakRef.get() != null)
            lifecycleScopeWeakRef.clear()
    }

    private fun provideSuccessReturnLink(): String =
        "https://usgaminggamblig.com/payment/return/success"

    private fun provideFailedReturnLink(): String =
        "https://usgaminggamblig.com/payment/return/failed"

    private fun provideCancelledReturnLink(): String =
        "https://usgaminggamblig.com/payment/return/cancelled"

    private fun provideDefaultReturnLink(): String =
        "https://usgaminggamblig.com/payment/return/"
}