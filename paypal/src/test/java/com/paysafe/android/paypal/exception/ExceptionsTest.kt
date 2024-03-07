/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paypal.exception

import com.paysafe.android.core.domain.exception.PaysafeException
import com.paysafe.android.core.exception.genericDisplayMessage
import org.junit.Assert.assertEquals
import org.junit.Test

class ExceptionsTest {

    private val correlationIdInput = "correlationIdForTesting"

    @Test
    fun `IF exception belongs to api error THEN errorName RETURNS APIError`() {
        // Arrange
        val input = genericApiErrorException(correlationIdInput)

        // Act
        val output = input.errorName()

        // Assert
        assertEquals("APIError", output)
    }

    @Test
    fun `IF exception belongs to core error THEN errorName RETURNS CoreError`() {
        // Arrange
        val input = improperlyCreatedMerchantAccountConfigException(correlationIdInput)

        // Act
        val output = input.errorName()

        // Assert
        assertEquals("CoreError", output)
    }

    @Test
    fun `IF exception belongs to pay pal error THEN errorName RETURNS PayPalError`() {
        // Arrange
        val input = payPalFailedAuthorizationException(correlationIdInput)

        // Act
        val output = input.errorName()

        // Assert
        assertEquals("PayPalError", output)
    }

    @Test
    fun `IF exception belongs to card form THEN errorName RETURNS CardFormError`() {
        // Arrange
        val input = amountShouldBePositiveException(correlationIdInput)

        // Act
        val output = input.errorName()

        // Assert
        assertEquals("CardFormError", output)
    }

    @Test
    fun `IF for a generic paysafe exception THEN errorName RETURNS PayPalError`() {
        // Arrange
        val input = PaysafeException()

        // Act
        val output = input.errorName()

        // Assert
        assertEquals("PayPalError", output)
    }

    @Test
    fun `IF invalid account id for payment method exception is created THEN check output data`() {
        // Arrange
        val expectedDetailedMessage = "Invalid account id for PayPal."

        // Act
        val output = invalidAccountIdForPaymentMethodException(correlationIdInput)

        // Assert
        assertEquals(INVALID_ACCOUNT_ID_FOR_PAYMENT_METHOD, output.code)
        assertEquals(
            genericDisplayMessage(INVALID_ACCOUNT_ID_FOR_PAYMENT_METHOD),
            output.displayMessage
        )
        assertEquals(expectedDetailedMessage, output.detailedMessage)
        assertEquals(correlationIdInput, output.correlationId)
    }

    @Test
    fun `IF pay pal user cancelled exception is created THEN check output data`() {
        // Arrange
        val expectedDetailedMessage = "User aborted authentication."

        // Act
        val output = payPalUserCancelledException(correlationIdInput)

        // Assert
        assertEquals(PAYPAL_USER_CANCELLED, output.code)
        assertEquals(genericDisplayMessage(PAYPAL_USER_CANCELLED), output.displayMessage)
        assertEquals(expectedDetailedMessage, output.detailedMessage)
        assertEquals(correlationIdInput, output.correlationId)
    }

    @Test
    fun `IF pay pal failed authorization exception is created THEN check output data`() {
        // Arrange
        val expectedDetailedMessage = "PayPal failed authorization."

        // Act
        val output = payPalFailedAuthorizationException(correlationIdInput)

        // Assert
        assertEquals(PAYPAL_FAILED_AUTHORIZATION, output.code)
        assertEquals(genericDisplayMessage(PAYPAL_FAILED_AUTHORIZATION), output.displayMessage)
        assertEquals(expectedDetailedMessage, output.detailedMessage)
        assertEquals(correlationIdInput, output.correlationId)
    }

    @Test
    fun `IF currency code invalid iso exception is created THEN check output data`() {
        // Arrange
        val expectedDetailedMessage = "Invalid currency parameter."

        // Act
        val output = currencyCodeInvalidIsoException(correlationIdInput)

        // Assert
        assertEquals(CURRENCY_CODE_INVALID_ISO, output.code)
        assertEquals(genericDisplayMessage(CURRENCY_CODE_INVALID_ISO), output.displayMessage)
        assertEquals(expectedDetailedMessage, output.detailedMessage)
        assertEquals(correlationIdInput, output.correlationId)
    }

    @Test
    fun `IF improperly created merchant account config exception is created THEN check output data`() {
        // Arrange
        val expectedDetailedMessage = "Account not configured correctly."

        // Act
        val output = improperlyCreatedMerchantAccountConfigException(correlationIdInput)

        // Assert
        assertEquals(IMPROPERLY_CREATED_MERCHANT_ACCOUNT_CONFIG, output.code)
        assertEquals(
            genericDisplayMessage(IMPROPERLY_CREATED_MERCHANT_ACCOUNT_CONFIG),
            output.displayMessage
        )
        assertEquals(expectedDetailedMessage, output.detailedMessage)
        assertEquals(correlationIdInput, output.correlationId)
    }

    @Test
    fun `IF amount should be positive exception is created THEN check output data`() {
        // Arrange
        val expectedDetailedMessage =
            "Amount should be a number greater than 0 no longer than 11 characters."

        // Act
        val output = amountShouldBePositiveException(correlationIdInput)

        // Assert
        assertEquals(AMOUNT_SHOULD_BE_POSITIVE, output.code)
        assertEquals(genericDisplayMessage(AMOUNT_SHOULD_BE_POSITIVE), output.displayMessage)
        assertEquals(expectedDetailedMessage, output.detailedMessage)
        assertEquals(correlationIdInput, output.correlationId)
    }

    @Test
    fun `IF tokenization already in progress exception is created THEN check output data`() {
        // Arrange
        val expectedDetailedMessage = "Tokenization is already in progress."

        // Act
        val output = tokenizationAlreadyInProgressException(correlationIdInput)

        // Assert
        assertEquals(TOKENIZATION_ALREADY_IN_PROGRESS, output.code)
        assertEquals(genericDisplayMessage(TOKENIZATION_ALREADY_IN_PROGRESS), output.displayMessage)
        assertEquals(expectedDetailedMessage, output.detailedMessage)
        assertEquals(correlationIdInput, output.correlationId)
    }

    @Test
    fun `IF generic api error exception is created THEN check output data`() {
        // Arrange
        val expectedDetailedMessage = "Unhandled error occurred."

        // Act
        val output = genericApiErrorException(correlationIdInput)

        // Assert
        assertEquals(GENERIC_API_ERROR, output.code)
        assertEquals(genericDisplayMessage(GENERIC_API_ERROR), output.displayMessage)
        assertEquals(expectedDetailedMessage, output.detailedMessage)
        assertEquals(correlationIdInput, output.correlationId)
    }

    @Test
    fun `IF sdk not initialized exception is created THEN check output data`() {
        // Arrange
        val expectedDetailedMessage = "PaysafeSDK is not initialized."

        // Act
        val output = sdkNotInitializedException()

        // Assert
        assertEquals(SDK_NOT_INITIALIZED, output.code)
        assertEquals(genericDisplayMessage(SDK_NOT_INITIALIZED), output.displayMessage)
        assertEquals(expectedDetailedMessage, output.detailedMessage)
        assertEquals("", output.correlationId) // no correlationId in this case
    }

}