/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.threedsecure

import android.app.Activity
import android.content.Context
import com.cardinalcommerce.cardinalmobilesdk.Cardinal
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalEnvironment
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalRenderType
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalUiType
import com.cardinalcommerce.cardinalmobilesdk.models.CardinalActionCode
import com.cardinalcommerce.cardinalmobilesdk.models.CardinalConfigurationParameters
import com.cardinalcommerce.cardinalmobilesdk.models.ValidateResponse
import com.cardinalcommerce.cardinalmobilesdk.services.CardinalInitService
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.entity.value
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.core.domain.model.config.PSEnvironment
import com.paysafe.android.core.util.base64Decode
import com.paysafe.android.threedsecure.data.entity.ThreeDSChallengePayloadResponse
import com.paysafe.android.threedsecure.domain.model.ThreeDSChallengePayload
import com.paysafe.android.threedsecure.domain.model.ThreeDSJwtParams
import com.paysafe.android.threedsecure.domain.model.ThreeDSRenderType
import com.paysafe.android.threedsecure.domain.repository.ThreeDSecureRepository
import com.paysafe.android.threedsecure.exception.challenge3DSFailedValidationException
import com.paysafe.android.threedsecure.exception.challenge3DSSessionFailureException
import com.paysafe.android.threedsecure.exception.challenge3DSTimeoutException
import com.paysafe.android.threedsecure.exception.challenge3DSUserCancelledException
import com.paysafe.android.threedsecure.exception.challengeAccountIdIsNullException
import com.paysafe.android.threedsecure.exception.challengeAuthIdIsNullException
import com.paysafe.android.threedsecure.exception.challengeServerJwtIsNullException
import com.paysafe.android.threedsecure.exception.deviceFingerprintIsNullException
import com.paysafe.android.threedsecure.exception.errorName
import com.paysafe.android.threedsecure.exception.genericApiErrorException
import com.paysafe.android.threedsecure.exception.init3DSComplexException
import com.paysafe.android.threedsecure.exception.init3DsBasicException
import com.paysafe.android.threedsecure.exception.jwtIsNullException
import kotlinx.serialization.json.Json
import org.json.JSONArray
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class Paysafe3DSController(
    private val cardinal: Cardinal,
    private val json: Json,
    private val threeDSecureRepository: ThreeDSecureRepository,
) {

    suspend fun initCardinal3DS(
        context: Context,
        bin: String,
        accountId: String,
        threeDSRenderType: ThreeDSRenderType?,
        psApiClient: PSApiClient
    ): PSResult<String> {
        val threeDSJwtParams = ThreeDSJwtParams(bin, accountId)
        val threeDSJwt = threeDSecureRepository.getThreeDSJwt(threeDSJwtParams).value()
        if (threeDSJwt?.jwt == null) {
            val paysafeException = jwtIsNullException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException, true)
            return PSResult.Failure(paysafeException)
        }
        if (threeDSJwt.deviceFingerprintingId == null) {
            val paysafeException = deviceFingerprintIsNullException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException, true)
            return PSResult.Failure(paysafeException)
        }
        val init3DSServiceResult =
            initCardinal3DSService(context, threeDSJwt.jwt, threeDSRenderType, psApiClient)

        return if (init3DSServiceResult is PSResult.Failure)
            init3DSServiceResult
        else
            PSResult.Success(threeDSJwt.deviceFingerprintingId)
    }

    private suspend fun initCardinal3DSService(
        context: Context,
        jwtToken: String,
        threeDSRenderType: ThreeDSRenderType?,
        psApiClient: PSApiClient
    ): PSResult<String> =
        suspendCoroutine { continuation ->
            configureCardinal(context, psApiClient.environment, threeDSRenderType)
            cardinal.init(jwtToken, object : CardinalInitService {
                override fun onSetupCompleted(consumerSessionId: String?) {
                    continuation.resume(PSResult.Success(consumerSessionId))
                }

                override fun onValidated(validateResponse: ValidateResponse?, serverJwt: String?) {
                    if (validateResponse != null) {
                        val paysafeException = init3DSComplexException(
                            validateResponse.errorNumber,
                            validateResponse.errorDescription,
                            psApiClient.getCorrelationId()
                        )
                        psApiClient.logErrorEvent(
                            paysafeException.errorName(),
                            paysafeException,
                            true
                        )
                        continuation.resume(PSResult.Failure(paysafeException))
                    } else {
                        val paysafeException = init3DsBasicException(psApiClient.getCorrelationId())
                        psApiClient.logErrorEvent(
                            paysafeException.errorName(),
                            paysafeException,
                            true
                        )
                        continuation.resume(PSResult.Failure(paysafeException))
                    }
                }
            })
        }

    private fun configureCardinal(
        context: Context,
        psEnvironment: PSEnvironment,
        threeDSRenderType: ThreeDSRenderType?
    ) = cardinal.configure(
        context,
        createCardinalConfiguration(
            psEnvironment = psEnvironment,
            threeDSRenderType = threeDSRenderType
        )
    )

    private fun createCardinalConfiguration(
        psEnvironment: PSEnvironment,
        threeDSRenderType: ThreeDSRenderType?
    ) = CardinalConfigurationParameters().apply {
        val cardinalUiType = threeDSRenderType?.toCardinalUIType() ?: CardinalUiType.BOTH
        environment = psEnvironment.toCardinalEnvironment()
        requestTimeout = 8000
        renderType = provideRenderType(cardinalUiType)
        challengeTimeout = 5
        uiType = cardinalUiType
    }

    private fun PSEnvironment.toCardinalEnvironment() = when (this) {
        PSEnvironment.TEST -> CardinalEnvironment.STAGING
        PSEnvironment.PROD -> CardinalEnvironment.PRODUCTION
    }

    private fun ThreeDSRenderType.toCardinalUIType() = when (this) {
        ThreeDSRenderType.NATIVE -> CardinalUiType.NATIVE
        ThreeDSRenderType.HTML -> CardinalUiType.HTML
        ThreeDSRenderType.BOTH -> CardinalUiType.BOTH
    }

    private fun provideRenderType(cardinalUiType: CardinalUiType) = JSONArray().apply {
        put(CardinalRenderType.OTP)
        put(CardinalRenderType.SINGLE_SELECT)
        put(CardinalRenderType.MULTI_SELECT)
        put(CardinalRenderType.OOB)
        if (cardinalUiType == CardinalUiType.HTML || cardinalUiType == CardinalUiType.BOTH)
            put(CardinalRenderType.HTML)
    }

    suspend fun continueCardinal3DSChallenge(
        activity: Activity,
        encodedChallengePayload: String,
        psApiClient: PSApiClient
    ): PSResult<ThreeDSChallengePayload> {
        val challengePayloadResult = launchCardinal3DSChallenge(
            activity, encodedChallengePayload, psApiClient
        )

        if (challengePayloadResult is PSResult.Failure)
            return challengePayloadResult

        val challengePayload = challengePayloadResult.value()

        if (challengePayload?.authenticationId == null) {
            val paysafeException = challengeAuthIdIsNullException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException, true)
            return PSResult.Failure(paysafeException)
        }
        if (challengePayload.serverJwt == null) {
            val paysafeException = challengeServerJwtIsNullException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException, true)
            return PSResult.Failure(paysafeException)
        }
        if (challengePayload.accountId == null) {
            val paysafeException = challengeAccountIdIsNullException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException, true)
            return PSResult.Failure(paysafeException)
        }

        val finalizeResult = threeDSecureRepository.finalizeThreeDSAuthentication(
            challengePayload.authenticationId,
            challengePayload.serverJwt,
            challengePayload.accountId
        )

        return if (finalizeResult is PSResult.Failure)
            finalizeResult
        else
            challengePayloadResult
    }

    private suspend fun launchCardinal3DSChallenge(
        activity: Activity,
        encodedChallengePayload: String,
        psApiClient: PSApiClient
    ): PSResult<ThreeDSChallengePayload> =
        suspendCoroutine { continuation ->
            try {
                val payloadJsonString = encodedChallengePayload.base64Decode()
                val payloadResponse =
                    json.decodeFromString<ThreeDSChallengePayloadResponse>(payloadJsonString)
                cardinal.cca_continue(
                    payloadResponse.transactionId,
                    payloadResponse.payload,
                    activity
                ) { _, validateResponse, serverJwt ->
                    handleCardinalValidateReceiver(
                        validateResponse,
                        payloadResponse,
                        serverJwt,
                        continuation,
                        psApiClient
                    )
                }
            } catch (ex: Exception) {
                handleGenericException(psApiClient, continuation)
            }
        }

    private fun handleCardinalValidateReceiver(
        validateResponse: ValidateResponse,
        payloadResponse: ThreeDSChallengePayloadResponse,
        serverJwt: String?,
        continuation: Continuation<PSResult<ThreeDSChallengePayload>>,
        psApiClient: PSApiClient
    ) {
        when (validateResponse.actionCode) {
            CardinalActionCode.SUCCESS,
            CardinalActionCode.NOACTION -> handleCardinalValidateActionSuccessAndNoAction(
                validateResponse,
                payloadResponse,
                serverJwt,
                continuation,
                psApiClient
            )

            CardinalActionCode.FAILURE,
            CardinalActionCode.ERROR -> handleCardinalValidateActionFailureAndError(
                validateResponse,
                psApiClient,
                continuation
            )

            CardinalActionCode.CANCEL -> handleCardinalValidateActionCancel(
                psApiClient,
                continuation
            )

            CardinalActionCode.TIMEOUT -> handleCardinalValidateActionTimeout(
                psApiClient,
                continuation
            )

            else -> handleGenericException(psApiClient, continuation)
        }
    }

    private fun handleCardinalValidateActionSuccessAndNoAction(
        validateResponse: ValidateResponse,
        payloadResponse: ThreeDSChallengePayloadResponse,
        serverJwt: String?,
        continuation: Continuation<PSResult<ThreeDSChallengePayload>>,
        psApiClient: PSApiClient
    ) {
        if (validateResponse.isValidated) {
            val successPayload = ThreeDSChallengePayload(
                authenticationId = payloadResponse.id,
                accountId = payloadResponse.accountId,
                serverJwt = serverJwt
            )
            continuation.resume(PSResult.Success(successPayload))
        } else {
            val paysafeException =
                challenge3DSFailedValidationException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException, true)
            continuation.resume(PSResult.Failure(paysafeException))
        }
    }

    private fun handleCardinalValidateActionFailureAndError(
        validateResponse: ValidateResponse,
        psApiClient: PSApiClient,
        continuation: Continuation<PSResult<ThreeDSChallengePayload>>
    ) {
        val paysafeException = challenge3DSSessionFailureException(
            errorNumber = validateResponse.errorNumber,
            correlationId = psApiClient.getCorrelationId()
        )
        psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException, true)
        continuation.resume(PSResult.Failure(paysafeException))
    }

    private fun handleCardinalValidateActionCancel(
        psApiClient: PSApiClient,
        continuation: Continuation<PSResult<ThreeDSChallengePayload>>
    ) {
        val paysafeException = challenge3DSUserCancelledException(psApiClient.getCorrelationId())
        psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException, true)
        continuation.resume(PSResult.Failure(paysafeException))
    }

    private fun handleCardinalValidateActionTimeout(
        psApiClient: PSApiClient,
        continuation: Continuation<PSResult<ThreeDSChallengePayload>>
    ) {
        val paysafeException = challenge3DSTimeoutException(psApiClient.getCorrelationId())
        psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException, true)
        continuation.resume(PSResult.Failure(paysafeException))
    }

    private fun handleGenericException(
        psApiClient: PSApiClient,
        continuation: Continuation<PSResult<ThreeDSChallengePayload>>
    ) {
        val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
        psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException, true)
        continuation.resume(PSResult.Failure(paysafeException))
    }

    fun disposeCardinal() = cardinal.cleanup()
}