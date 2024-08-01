/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization

import android.util.Patterns
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.entity.value
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.core.domain.exception.PaysafeException
import com.paysafe.android.core.util.LocalLog
import com.paysafe.android.core.util.isAmountNotValid
import com.paysafe.android.tokenization.data.api.PaymentHubApi
import com.paysafe.android.tokenization.data.repository.InvocationId
import com.paysafe.android.tokenization.data.repository.PaymentHubRepositoryImpl
import com.paysafe.android.tokenization.domain.model.paymentHandle.CardRequest
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandle
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleRequest
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleStatus
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleTokenStatus
import com.paysafe.android.tokenization.exception.amountShouldBePositiveException
import com.paysafe.android.tokenization.exception.errorName
import com.paysafe.android.tokenization.exception.genericApiErrorException
import com.paysafe.android.tokenization.exception.invalidDynamicDescriptorException
import com.paysafe.android.tokenization.exception.invalidMerchantDescriptorPhoneException
import com.paysafe.android.tokenization.exception.paymentHandleCreationFailedException
import com.paysafe.android.tokenization.exception.profileEmailInvalidException
import com.paysafe.android.tokenization.exception.profileFirstNameInvalidException
import com.paysafe.android.tokenization.exception.profileLastNameInvalidException
import com.paysafe.android.tokenization.exception.responseCannotBeHandledException
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

internal class PSTokenizationController(
    private val psApiClient: PSApiClient
) {

    private val paymentHubRepository = PaymentHubRepositoryImpl(
        PaymentHubApi(psApiClient, InvocationId()), psApiClient
    )

    suspend fun tokenize(
        paymentHandleRequest: PaymentHandleRequest,
        cardRequest: CardRequest?
    ): PSResult<PaymentHandle> {
        try {
            validatePaymentHandleRequest(paymentHandleRequest)
        } catch (paysafeException: PaysafeException) {
            return PSResult.Failure(paysafeException)
        }

        LocalLog.d("PSTokenizationController", "Get PaymentHandle")
        val paymentHandle =
            paymentHubRepository.createPaymentHandle(paymentHandleRequest, cardRequest)
            { content, invocationId ->
                logTokenizeOptionsEvent(content, invocationId)
            }.value()

        if (paymentHandle?.id == null)
            return handleGenericError()

        try {
            validateTokenStatus(paymentHandle)
        } catch (paysafeException: PaysafeException) {
            return PSResult.Failure(paysafeException)
        }

        return PSResult.Success(paymentHandle)
    }

    private fun handleGenericError(): PSResult.Failure {
        val paysafeException = genericApiErrorException(psApiClient.getCorrelationId())
        psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
        return PSResult.Failure(paysafeException)
    }

    suspend fun refreshToken(
        paymentHandle: PaymentHandle,
        retryCount: Int = 3,
        delayInSeconds: Int = 6
    ): PSResult<PaymentHandle> {
        LocalLog.d("PSTokenizationController", "RefreshToken")
        val paymentTokenStatus =
            paymentHubRepository.getPaymentHandleStatus(paymentHandle.paymentHandleToken).value()
        LocalLog.d(
            "PSTokenizationController",
            "RefreshToken paymentTokenStatus: ${paymentTokenStatus?.status}"
        )
        return when (paymentTokenStatus?.status) {
            PaymentHandleTokenStatus.PAYABLE ->
                handleRefreshTokenStatusPayable(
                    paymentHandle,
                    paymentTokenStatus.status
                )

            PaymentHandleTokenStatus.COMPLETED,
            PaymentHandleTokenStatus.INITIATED,
            PaymentHandleTokenStatus.PROCESSING ->
                handleRefreshTokenStatusCompletedInitiatedAndProcessing(
                    retryCount,
                    delayInSeconds,
                    paymentHandle,
                    paymentTokenStatus.status
                )

            PaymentHandleTokenStatus.FAILED,
            PaymentHandleTokenStatus.EXPIRED,
            null ->
                handleRefreshTokenStatusFailedExpiredAndNull(paymentTokenStatus)
        }
    }

    private fun handleRefreshTokenStatusPayable(
        paymentHandle: PaymentHandle,
        status: PaymentHandleTokenStatus
    ): PSResult.Success<PaymentHandle> {
        logPaymentHandleTokenizeEvent()
        val updatedPaymentHandle = paymentHandle.copy(
            status = status.status
        )
        return PSResult.Success(updatedPaymentHandle)
    }

    private suspend fun handleRefreshTokenStatusCompletedInitiatedAndProcessing(
        retryCount: Int,
        delayInSeconds: Int,
        paymentHandle: PaymentHandle,
        status: PaymentHandleTokenStatus
    ) = if (retryCount > 0) {
        delay(delayInSeconds.seconds)
        refreshToken(
            paymentHandle = paymentHandle,
            retryCount = retryCount - 1
        )
    } else {
        val paysafeException = paymentHandleCreationFailedException(
            status = status.status,
            correlationId = psApiClient.getCorrelationId()
        )
        psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
        PSResult.Failure(paysafeException)
    }

    private fun handleRefreshTokenStatusFailedExpiredAndNull(
        paymentTokenStatus: PaymentHandleStatus?
    ): PSResult.Failure {
        val paysafeException = paymentHandleCreationFailedException(
            status = paymentTokenStatus?.status?.status ?: PaymentHandleTokenStatus.FAILED.status,
            correlationId = psApiClient.getCorrelationId()
        )
        psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
        return PSResult.Failure(paysafeException)
    }

    private fun logPaymentHandleTokenizeEvent() {
        val message = "Payment Handle Tokenize function call."
        psApiClient.logEvent(message)
    }

    private fun logTokenizeOptionsEvent(content: String, invocationId: String) {
        val message = "Options object passed on tokenize: $content, invocationId: $invocationId"
        psApiClient.logEvent(message)
    }

    private fun validatePaymentHandleRequest(paymentHandleRequest: PaymentHandleRequest) {
        if (isAmountNotValid(paymentHandleRequest.amount)) {
            val paysafeException = amountShouldBePositiveException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
            throw paysafeException
        }
        val merchantDescriptor = paymentHandleRequest.merchantDescriptor
        if (merchantDescriptor != null) {
            if (isDynamicDescriptorNotValid(merchantDescriptor.dynamicDescriptor)) {
                val paysafeException =
                    invalidDynamicDescriptorException(psApiClient.getCorrelationId())
                psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                throw paysafeException
            }
            val phone = merchantDescriptor.phone
            if (phone != null && isMerchantDescriptorPhoneNotValid(phone)) {
                val paysafeException =
                    invalidMerchantDescriptorPhoneException(psApiClient.getCorrelationId())
                psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                throw paysafeException
            }
        }
        val profile = paymentHandleRequest.profile
        if (profile != null) {
            if (isProfileFirstNameNotValid(profile.firstName)) {
                val paysafeException =
                    profileFirstNameInvalidException(psApiClient.getCorrelationId())
                psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                throw paysafeException
            }
            if (isProfileLastNameNotValid(profile.lastName)) {
                val paysafeException =
                    profileLastNameInvalidException(psApiClient.getCorrelationId())
                psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                throw paysafeException
            }
            if (isProfileEmailNotValid(profile.email)) {
                val paysafeException = profileEmailInvalidException(psApiClient.getCorrelationId())
                psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
                throw paysafeException
            }
        }
    }

    private fun validateTokenStatus(paymentHandle: PaymentHandle?) {
        if (paymentHandle?.paymentHandleToken.isNullOrBlank()) {
            val paysafeException = responseCannotBeHandledException(psApiClient.getCorrelationId())
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
            throw paysafeException
        }
        if (paymentHandle?.status == PaymentHandleTokenStatus.FAILED.status) {
            val paysafeException =
                paymentHandleCreationFailedException(
                    status = paymentHandle.status,
                    correlationId = psApiClient.getCorrelationId()
                )
            psApiClient.logErrorEvent(paysafeException.errorName(), paysafeException)
            throw paysafeException
        }
    }

    private fun isDynamicDescriptorNotValid(input: String) =
        input.isBlank() || input.length > 20

    private fun isMerchantDescriptorPhoneNotValid(input: String) =
        input.isBlank() || input.length > 13

    private fun isProfileFirstNameNotValid(input: String?) =
        input.isNullOrBlank() || input.length > 80

    private fun isProfileLastNameNotValid(input: String?) =
        input.isNullOrBlank() || input.length > 80

    private fun isProfileEmailNotValid(input: String?) =
        input.isNullOrBlank() || !Patterns.EMAIL_ADDRESS.matcher(input).matches()
}