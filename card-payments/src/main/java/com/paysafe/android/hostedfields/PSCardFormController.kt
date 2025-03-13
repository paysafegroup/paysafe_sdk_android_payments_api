/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.lifecycleScope
import com.paysafe.android.PaysafeSDK
import com.paysafe.android.core.data.entity.PSCallback
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.entity.PSResultCallback
import com.paysafe.android.core.data.entity.resultAsCallback
import com.paysafe.android.core.data.entity.value
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.core.domain.exception.PaysafeException
import com.paysafe.android.core.util.LocalLog
import com.paysafe.android.core.util.isCurrencyCodeValid
import com.paysafe.android.core.util.isNotAllDigits
import com.paysafe.android.core.util.isNotNullOrEmpty
import com.paysafe.android.core.util.launchCatching
import com.paysafe.android.hostedfields.cardnumber.PSCardNumberView
import com.paysafe.android.hostedfields.cvv.PSCvvView
import com.paysafe.android.hostedfields.data.api.CardAdapterAuthApi
import com.paysafe.android.hostedfields.data.repository.CardAdapterAuthRepositoryImpl
import com.paysafe.android.hostedfields.domain.mapper.toPaymentHandleRequest
import com.paysafe.android.hostedfields.domain.mapper.toPaymentHandleRequestWithRenderType
import com.paysafe.android.hostedfields.domain.model.PSCardTokenizeOptions
import com.paysafe.android.hostedfields.domain.model.PaymentHandleRequestWithRenderType
import com.paysafe.android.hostedfields.domain.model.cardadapter.AuthenticationRequest
import com.paysafe.android.hostedfields.domain.model.cardadapter.FinalizeAuthenticationResponse
import com.paysafe.android.hostedfields.domain.model.toThreeDSRenderType
import com.paysafe.android.hostedfields.domain.repository.CardAdapterAuthRepository
import com.paysafe.android.hostedfields.exception.currencyCodeInvalidIsoException
import com.paysafe.android.hostedfields.exception.errorName
import com.paysafe.android.hostedfields.exception.genericApiErrorException
import com.paysafe.android.hostedfields.exception.improperlyCreatedMerchantAccountConfigException
import com.paysafe.android.hostedfields.exception.invalidAccountIdForPaymentMethodException
import com.paysafe.android.hostedfields.exception.invalidAccountIdParameterException
import com.paysafe.android.hostedfields.exception.noAvailablePaymentMethodsException
import com.paysafe.android.hostedfields.exception.noViewsInCardFormControllerException
import com.paysafe.android.hostedfields.exception.paymentHandleCreationFailedException
import com.paysafe.android.hostedfields.exception.sdkNotInitializedException
import com.paysafe.android.hostedfields.exception.specifiedHostedFieldWithInvalidValueException
import com.paysafe.android.hostedfields.exception.tokenizationAlreadyInProgressException
import com.paysafe.android.hostedfields.exception.unsupportedCardBrandException
import com.paysafe.android.hostedfields.expirydate.PSExpiryDateView
import com.paysafe.android.hostedfields.holdername.PSCardholderNameView
import com.paysafe.android.hostedfields.valid.ExpiryDateChecks
import com.paysafe.android.paymentmethods.PaymentMethodsService
import com.paysafe.android.paymentmethods.PaymentMethodsServiceImpl
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType
import com.paysafe.android.paymentmethods.domain.model.PaymentMethod
import com.paysafe.android.paymentmethods.domain.model.PaymentMethodType
import com.paysafe.android.threedsecure.Paysafe3DS
import com.paysafe.android.tokenization.PSTokenization
import com.paysafe.android.tokenization.PSTokenizationService
import com.paysafe.android.tokenization.domain.model.cardadapter.AuthenticationStatus
import com.paysafe.android.tokenization.domain.model.paymentHandle.CardExpiryRequest
import com.paysafe.android.tokenization.domain.model.paymentHandle.CardRequest
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandle
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleAction
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleRequest
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleTokenStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.coroutines.coroutineContext

/**
 * Central place where the four widgets to get credit card data from the user is coded. It is used
 * in conjunction with a Paysafe client to perform http operations.
 *
 * @param cardNumberView Card number widget component.
 * @param cardHolderNameView Card holder name widget component.
 * @param cardExpiryDateView Expiry date widget component.
 * @param cardCvvView Card verification value widget component.
 */

class PSCardFormController internal constructor(
    internal var cardNumberView: PSCardNumberView? = null,
    internal var cardHolderNameView: PSCardholderNameView? = null,
    internal var cardExpiryDateView: PSExpiryDateView? = null,
    internal var cardCvvView: PSCvvView? = null,
    private val tokenizationService: PSTokenizationService,
    private val mainDispatcher: CoroutineDispatcher,
    private val ioDispatcher: CoroutineDispatcher,
    private val psApiClient: PSApiClient,
) {

    var onCardBrandRecognition: ((PSCreditCardType) -> Unit)? = null

    private val cardAdapterAuthRepository: CardAdapterAuthRepository =
        CardAdapterAuthRepositoryImpl(CardAdapterAuthApi(psApiClient), psApiClient)

    /** Read-only [LiveData] to enable/disable a submit action for hosted fields. */
    val isSubmitEnabledLiveData: LiveData<Boolean>
        get() = _isSubmitEnabledLiveData

    internal var tokenizationAlreadyInProgress = false

    private var correlationId: String
    private val _isSubmitEnabledLiveData = MediatorLiveData<Boolean>()

    init {
        if (areFieldsNotNull()) {
            cardNumberView!!.cardTypeLiveData.observe(
                cardNumberView!!.context as LifecycleOwner
            ) { cardType ->
                onCardBrandRecognition?.invoke(cardType)
                cardCvvView!!.cardType = cardType
            }

            listOf(
                cardNumberView!!.isValidLiveData,
                cardHolderNameView!!.isValidLiveData,
                cardExpiryDateView!!.isValidLiveData,
                cardCvvView!!.isValidLiveData
            ).forEach {
                _isSubmitEnabledLiveData.addSource(it) {
                    _isSubmitEnabledLiveData.value = areAllFieldsValid()
                }
            }
        }
        val psApiClient = PaysafeSDK.getPSApiClient()
        correlationId = psApiClient.getCorrelationId()
        logInitializedFieldsEvent(
            psApiClient,
            cardNumberView,
            cardHolderNameView,
            cardExpiryDateView,
            cardCvvView
        )
    }

    companion object {

        internal var supportedCardTypes = emptyList<PSCreditCardType>()

        private lateinit var coroutineScope: CoroutineScope

        fun initialize(
            cardFormConfig: PSCardFormConfig,
            cardNumberView: PSCardNumberView? = null,
            cardHolderNameView: PSCardholderNameView? = null,
            cardExpiryDateView: PSExpiryDateView? = null,
            cardCvvView: PSCvvView? = null,
            callback: PSCallback<PSCardFormController>,
        ) {
            if (!PaysafeSDK.isInitialized()) {
                callback.onFailure(sdkNotInitializedException())
                return
            }
            initialize(
                cardFormConfig = cardFormConfig,
                cardNumberView = cardNumberView,
                cardHolderNameView = cardHolderNameView,
                cardExpiryDateView = cardExpiryDateView,
                cardCvvView = cardCvvView,
                callback = callback,
                dispatchers = provideDispatchersPair()
            )
        }

        internal fun initialize(
            cardFormConfig: PSCardFormConfig,
            cardNumberView: PSCardNumberView?,
            cardHolderNameView: PSCardholderNameView?,
            cardExpiryDateView: PSExpiryDateView?,
            cardCvvView: PSCvvView?,
            callback: PSCallback<PSCardFormController>,
            dispatchers: Pair<CoroutineDispatcher, CoroutineDispatcher>,
        ) {
            val (mainDispatcher, ioDispatcher) = dispatchers
            if (!this::coroutineScope.isInitialized)
                coroutineScope = CoroutineScope(SupervisorJob() + ioDispatcher)
            coroutineScope.launchCatching {
                val psApiClient = PaysafeSDK.getPSApiClient()
                val validatePaymentMethodResult = validatePaymentMethods(
                    cardFormConfig,
                    psApiClient
                )
                withContext(mainDispatcher) {
                    if (validatePaymentMethodResult is PSResult.Failure) {
                        callback.onFailure(validatePaymentMethodResult.exception)
                    } else {
                        val cardFormController = PSCardFormController(
                            cardNumberView = cardNumberView,
                            cardHolderNameView = cardHolderNameView,
                            cardExpiryDateView = cardExpiryDateView,
                            cardCvvView = cardCvvView,
                            tokenizationService = PSTokenization(psApiClient),
                            mainDispatcher = mainDispatcher,
                            ioDispatcher = ioDispatcher,
                            psApiClient = psApiClient
                        )
                        callback.onSuccess(cardFormController)
                    }
                }
            }.onFailure {
                callback.onFailure(Exception(it.message))
            }
        }

        private fun provideDispatchersPair(): Pair<CoroutineDispatcher, CoroutineDispatcher> =
            Pair(Dispatchers.Main, Dispatchers.IO)

        internal suspend fun validatePaymentMethods(
            cardFormConfig: PSCardFormConfig,
            psApiClient: PSApiClient,
            paymentMethodsService: PaymentMethodsService = PaymentMethodsServiceImpl(psApiClient),
        ): PSResult<Unit> {
            val accountId = cardFormConfig.accountId
            val currencyCode = cardFormConfig.currencyCode
            val cardPaymentMethod = PaymentMethodType.CARD

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

            val paymentMethodsResult = paymentMethodsService.getPaymentMethods(currencyCode)
            if (paymentMethodsResult is PSResult.Failure)
                return paymentMethodsResult

            val paymentMethods = paymentMethodsResult.value()

            val availableCardTypesForValidPaymentMethod = paymentMethods?.firstOrNull {
                it.accountId == accountId &&
                        it.paymentMethod == cardPaymentMethod &&
                        it.currencyCode == currencyCode &&
                        it.hasAccountConfiguration()
            }?.accountConfiguration?.getAvailableCardTypes()
                ?: return if (paymentMethods?.firstOrNull {
                        it.accountId == accountId &&
                                it.paymentMethod == cardPaymentMethod &&
                                it.currencyCode == currencyCode &&
                                it.accountConfiguration?.getAvailableCardTypes().isNullOrEmpty()
                    } != null) { // no available card types
                    val paysafeException =
                        noAvailablePaymentMethodsException(psApiClient.getCorrelationId())
                    psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)

                    PSResult.Failure(paysafeException)
                } else return if (paymentMethods?.firstOrNull {
                        it.accountId == accountId &&
                                it.paymentMethod != cardPaymentMethod
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

            supportedCardTypes = availableCardTypesForValidPaymentMethod
            return PSResult.Success()
        }

        private fun PaymentMethod.hasAccountConfiguration(): Boolean =
            accountConfiguration?.getAvailableCardTypes().isNotNullOrEmpty() &&
                    accountConfiguration?.cardTypeConfig?.any() == true
    }

    fun getCardBrand() = cardNumberView?.cardTypeLiveData?.value
        ?: cardCvvView?.cardType
        ?: PSCreditCardType.UNKNOWN

    fun areAllFieldsValid() = if (areFieldsNotNull()) {
        cardNumberView!!.isValidLiveData.value == true
                && cardHolderNameView!!.isValidLiveData.value == true
                && cardExpiryDateView!!.isValidLiveData.value == true
                && cardCvvView!!.isValidLiveData.value == true
    } else
        false

    private fun handleGenericError(): PSResult.Failure {
        val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
        psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
        return PSResult.Failure(paysafeException)
    }

    /**
     * Coroutine to create a payment handle result object as a suspended function; with merchant,
     * transaction and credit card data retrieved from the user interface widgets.
     */
    @JvmSynthetic
    suspend fun tokenize(
        cardTokenizeOptions: PSCardTokenizeOptions,
    ): PSResult<String> {
        return try {
            val paymentHandleRequestWithRenderType = cardTokenizeOptions.toPaymentHandleRequestWithRenderType()
            val paymentHandleRequest = cardTokenizeOptions.toPaymentHandleRequest()
            val activity = getContext().getActivity()

            if (tokenizationAlreadyInProgress) {
                return handleTokenizationInProgressError()
            }
            tokenizationAlreadyInProgress = true

            validateUsesSupportedCreditCard(getCardBrand(), supportedCardTypes)

            val tokenizeResult = tokenizationService.tokenize(
                paymentHandleRequest = paymentHandleRequest,
                cardRequest = getCardRequestData()
            )

            processTokenizeResult(tokenizeResult, paymentHandleRequest, paymentHandleRequestWithRenderType, activity)
        } catch (psException: PaysafeException) {
            PSResult.Failure(psException)
        } catch (exception: Exception) {
            PSResult.Failure(exception)
        } finally {
            tokenizationAlreadyInProgress = false
            withContext(coroutineContext + Dispatchers.Main) {
                resetFields()
            }
        }
    }

    private fun handleTokenizationInProgressError(): PSResult.Failure {
        val paysafeException = tokenizationAlreadyInProgressException(correlationId)
        PaysafeSDK.getPSApiClient()
            .logErrorEvent(paysafeException.errorName(), paysafeException)
        return PSResult.Failure(paysafeException)
    }

    private suspend fun processTokenizeResult(
        tokenizeResult: PSResult<PaymentHandle>,
        paymentHandleRequest: PaymentHandleRequest,
        paymentHandleRequestWithRenderType: PaymentHandleRequestWithRenderType,
        activity: Activity?,
    ): PSResult<String> {
        return when (tokenizeResult) {
            is PSResult.Failure -> tokenizeResult
            is PSResult.Success -> processSuccessfulTokenization(
                tokenizeResult,
                paymentHandleRequest,
                paymentHandleRequestWithRenderType,
                activity
            )
        }
    }

    private suspend fun processSuccessfulTokenization(
        tokenizeResult: PSResult.Success<PaymentHandle>,
        paymentHandleRequest: PaymentHandleRequest,
        paymentHandleRequestWithRenderType: PaymentHandleRequestWithRenderType,
        activity: Activity?,
    ): PSResult<String> {
        val paymentHandle = tokenizeResult.value
        val bin = paymentHandle?.networkTokenBin ?: paymentHandle?.cardBin

        if (paymentHandle?.action == PaymentHandleAction.REDIRECT.toString() &&
            (paymentHandle.status == PaymentHandleTokenStatus.INITIATED.status ||
                    paymentHandle.status == PaymentHandleTokenStatus.PROCESSING.status) &&
            bin != null
        ) {
            return start3DS(
                activity,
                bin,
                paymentHandle,
                paymentHandleRequest,
                paymentHandleRequestWithRenderType
            )
        }

        LocalLog.d("PSTokenizationController", "Status processing")
        return when (paymentHandle?.status) {
            PaymentHandleTokenStatus.PAYABLE.status,
            PaymentHandleTokenStatus.COMPLETED.status ->
                PSResult.Success(paymentHandle.paymentHandleToken)
            else -> handleStatus(paymentHandle?.status)
        }
    }

    private fun handleStatus(
        status: String?,
    ): PSResult.Failure {
        val paysafeException =
            paymentHandleCreationFailedException(
                status = status ?: PaymentHandleTokenStatus.FAILED.status,
                correlationId = psApiClient.getCorrelationId()
            )
        psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
        return PSResult.Failure(paysafeException)
    }

    private suspend fun PSCardFormController.start3DS(
        activity: Activity?,
        bin: String,
        paymentHandle: PaymentHandle?,
        paymentHandleRequest: PaymentHandleRequest,
        paymentHandleRequestWithRenderType: PaymentHandleRequestWithRenderType,
    ): PSResult<String> {
        LocalLog.d("PSTokenizationController", "Initialize 3DS")
        val paysafe3DS = Paysafe3DS()
        val deviceFingerprint = paysafe3DS.start(
            context = activity!!.applicationContext,
            bin = bin,
            accountId = paymentHandle?.accountId ?: paymentHandleRequest.accountId,
            threeDSRenderType = paymentHandleRequestWithRenderType.renderType?.toThreeDSRenderType()
        ).value()

        if (paymentHandle?.id == null || deviceFingerprint == null) {
            return handleGenericError()
        }

        LocalLog.d("PSTokenizationController", "Start Authentication")
        return startAuthentication(paymentHandle, paymentHandleRequest, activity, paysafe3DS, deviceFingerprint)
    }

    private suspend fun startAuthentication(
        paymentHandle: PaymentHandle,
        paymentHandleRequest: PaymentHandleRequest,
        activity: Activity?,
        paysafe3DS: Paysafe3DS,
        deviceFingerprint: String,
    ): PSResult<String> {
        val authenticationRequest = AuthenticationRequest(
            paymentHandleId = paymentHandle.id!!,
            merchantRefNum = paymentHandle.merchantRefNum,
            process = paymentHandleRequest.threeDS?.process
        )
        val authenticationResponse = cardAdapterAuthRepository.startAuthentication(
            authenticationRequest = authenticationRequest,
            deviceFingerprintingId = deviceFingerprint
        ).value()

        return when {
            authenticationResponse?.status == AuthenticationStatus.PENDING && authenticationResponse.sdkChallengePayload != null -> {
                val finalizeResult = activity?.let {
                    continueAuthenticationFlow(activity = it, paysafe3DS = paysafe3DS, sdkChallengePayload = authenticationResponse.sdkChallengePayload, paymentHandleId = paymentHandle.id!!)
                }
                if (finalizeResult?.status == AuthenticationStatus.FAILED) {
                    return handleAuthenticationStatusFailed()
                }
                if (finalizeResult?.status != AuthenticationStatus.COMPLETED) {
                    return handleGenericError()
                }
                onRefreshToken(paymentHandle = paymentHandle)
            }

            authenticationResponse?.status == AuthenticationStatus.FAILED -> handleAuthenticationStatusFailed()
            authenticationResponse?.status != AuthenticationStatus.COMPLETED -> handleGenericError()
            else -> onRefreshToken(paymentHandle = paymentHandle)
        }
    }


    private fun handleAuthenticationStatusFailed(): PSResult.Failure {
        val paysafeException = paymentHandleCreationFailedException(
            status = AuthenticationStatus.FAILED.value,
            correlationId = psApiClient.getCorrelationId()
        )
        psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
        return PSResult.Failure(paysafeException)
    }

    private suspend fun continueAuthenticationFlow(
        activity: Activity,
        paysafe3DS: Paysafe3DS,
        sdkChallengePayload: String,
        paymentHandleId: String,
    ): FinalizeAuthenticationResponse? {
        LocalLog.d("PSTokenizationController", "Launch 3DS Challenge")
        val challengePayloadResult = paysafe3DS.launch3dsChallenge(
            activity = activity,
            challengePayload = sdkChallengePayload
        )
        val authenticationId =
            (challengePayloadResult as? PSResult.Success)?.value?.authenticationId ?: return null

        LocalLog.d("PSTokenizationController", "Finalize Authentication")
        val finalizeAuthenticationResult = cardAdapterAuthRepository.finalizeAuthentication(
            paymentHandleId = paymentHandleId,
            authenticationId = authenticationId
        )
        return (finalizeAuthenticationResult as? PSResult.Success)?.value
    }

    /**
     * Creates a payment handle using a callback; with merchant, transaction and credit card data
     * retrieved from the user interface widgets.
     *
     * @param lifecycleOwner Utility object to create coroutine and emit the result in the callback.
     * @param callback Result object with success/failure methods to handle payment handle service.
     */
    fun tokenize(
        lifecycleOwner: LifecycleOwner,
        cardTokenizeOptions: PSCardTokenizeOptions,
        callback: PSResultCallback<String>,
    ) {
        lifecycleOwner.lifecycleScope.launch(ioDispatcher) {
            val result = tokenize(cardTokenizeOptions)
            withContext(mainDispatcher) {
                resultAsCallback(result, callback)
            }
        }
    }

    internal suspend fun onRefreshToken(paymentHandle: PaymentHandle?): PSResult<String> {
        if (paymentHandle?.paymentHandleToken == "" || paymentHandle == null) {
            LocalLog.d(
                "PSCardFormController",
                "Cannot refresh token as the payment handle is null."
            )
            val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
            tokenizationAlreadyInProgress = false
            return PSResult.Failure(paysafeException)
        }

        when (val refreshTokenResult = tokenizationService.refreshToken(paymentHandle)) {
            is PSResult.Failure -> {
                return refreshTokenFailureHandler(refreshTokenResult)
            }
            is PSResult.Success -> {
                return refreshTokenSuccessHandler(refreshTokenResult)
            }
        }
    }

    private fun refreshTokenSuccessHandler(refreshTokenResult: PSResult.Success<PaymentHandle>): PSResult<String> {
        val refreshedPaymentHandle = refreshTokenResult.value
        if (refreshedPaymentHandle != null) {
            LocalLog.d("PSCardFormController", "Token was refreshed with success.")
            tokenizationAlreadyInProgress = false
            return PSResult.Success(refreshedPaymentHandle.paymentHandleToken)
        } else {
            LocalLog.d("PSCardFormController", "Refreshed PaymentHandle is null")
            val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
            tokenizationAlreadyInProgress = false
            return PSResult.Failure(paysafeException)
        }
    }

    private fun refreshTokenFailureHandler(refreshTokenResult: PSResult.Failure): PSResult.Failure {
        LocalLog.d(
            "PSCardFormController",
            "Refresh token failed with ${refreshTokenResult.exception.message}"
        )
        tokenizationAlreadyInProgress = false
        if (refreshTokenResult.exception is PaysafeException) {
            return PSResult.Failure(refreshTokenResult.exception)
        } else {
            val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
            return PSResult.Failure(paysafeException)
        }
    }

    fun resetFields() {
        cardNumberView?.reset()
        cardHolderNameView?.reset()
        cardExpiryDateView?.reset()
        cardCvvView?.reset()
    }

    fun dispose() {
        cardNumberView = null
        cardCvvView = null
        cardHolderNameView = null
        cardExpiryDateView = null
    }

    @Throws(PaysafeException::class)
    private fun validateUsesSupportedCreditCard(
        creditCardType: PSCreditCardType,
        supportedCreditCards: List<PSCreditCardType>?,
    ) {
        if (supportedCreditCards?.contains(creditCardType) == false)
            throw unsupportedCardBrandException(correlationId)
    }

    private fun getContext() = cardCvvView?.context
        ?: cardHolderNameView?.context
        ?: cardExpiryDateView?.viewContext
        ?: cardCvvView?.context
        ?: throw noViewsInCardFormControllerException(
            PaysafeSDK.getPSApiClient().getCorrelationId()
        )

    private tailrec fun Context.getActivity(): Activity? = this as? Activity
        ?: (this as? ContextWrapper)?.baseContext?.getActivity()

    private fun areFieldsNotNull() = cardNumberView != null
            && cardHolderNameView != null
            && cardExpiryDateView != null
            && cardCvvView != null

    private fun getCardRequestData(): CardRequest {
        var cardExpiration: CardExpiryRequest? = null
        if (cardExpiryDateView != null) {
            cardExpiration = CardExpiryRequest(
                cardExpiryDateView!!.monthData,
                cardExpiryDateView!!.yearData
            )
        }

        val hostedFieldsErrors = hostedFieldsErrorsList()
        if (hostedFieldsErrors.isNotEmpty()) {
            val paysafeException = specifiedHostedFieldWithInvalidValueException(
                *hostedFieldsErrors.toTypedArray(), correlationId = correlationId
            )
            PaysafeSDK.getPSApiClient()
                .logErrorEvent(paysafeException.errorName(), paysafeException)
            throw paysafeException
        }

        return CardRequest(
            cardNum = cardNumberView?.data,
            cardExpiry = cardExpiration,
            holderName = cardHolderNameView?.data,
            cvv = cardCvvView?.data
        )
    }

    private fun hostedFieldsErrorsList(): ArrayList<String> {
        val fieldsErrors = arrayListOf<String>()
        if (cardNumberView != null && !cardNumberView!!.isValid()) {
            fieldsErrors.add("card number")
        }
        if (cardHolderNameView != null && !cardHolderNameView!!.isValid()) {
            fieldsErrors.add("cardholder name")
        }
        if (cardExpiryDateView != null) {
            val date = cardExpiryDateView!!.monthData + cardExpiryDateView!!.yearData
            if (ExpiryDateChecks.validations(date)) {
                fieldsErrors.add("expiry date")
            }
        }
        if (cardCvvView != null && !cardCvvView!!.isValid()) {
            fieldsErrors.add("cvv")
        }
        return fieldsErrors
    }

    private fun logInitializedFieldsEvent(
        psApiClient: PSApiClient,
        cardNumberView: PSCardNumberView?,
        cardHolderNameView: PSCardholderNameView?,
        cardExpiryDateView: PSExpiryDateView?,
        cardCvvView: PSCvvView?,
    ) {
        val notAvailable = "N/A"
        val cardNumberPlaceholder = getNotEmptyOrDefault(cardNumberView?.placeholderString)
        val holderNamePlaceholder = getNotEmptyOrDefault(cardHolderNameView?.placeholderString)
        val expiryDatePlaceholder = getNotEmptyOrDefault(cardExpiryDateView?.placeholderString)
        val cvvPlaceholder = getNotEmptyOrDefault(cardCvvView?.placeholderString)

        val fields = Fields(
            cardNumber = Field(cardNumberPlaceholder, cardNumberPlaceholder, notAvailable),
            cardHolderName = Field(holderNamePlaceholder, holderNamePlaceholder, notAvailable),
            expiryDate = Field(expiryDatePlaceholder, expiryDatePlaceholder, notAvailable),
            cvvField = Field(cvvPlaceholder, cvvPlaceholder, notAvailable),
        )

        val message = "Initialized fields: ${Json.encodeToString(fields)}"
        psApiClient.logEvent(message)
    }

    private fun getNotEmptyOrDefault(value: String?, default: String = "N/A"): String =
        if (value?.isNotEmpty() == true)
            value
        else
            default
}


@Serializable
internal data class Field(
    @SerialName("placeHolder")
    val placeHolder: String,
    @SerialName("accessibilityLabel")
    val accessibilityLabel: String,
    @SerialName("accessibilityErrorMessage")
    val accessibilityErrorMessage: String,
)

@Serializable
internal data class Fields(
    @SerialName("cardNumber")
    val cardNumber: Field?,
    @SerialName("cardHolderName")
    val cardHolderName: Field,
    @SerialName("expiryDate")
    val expiryDate: Field?,
    @SerialName("cvvField")
    val cvvField: Field?,
)