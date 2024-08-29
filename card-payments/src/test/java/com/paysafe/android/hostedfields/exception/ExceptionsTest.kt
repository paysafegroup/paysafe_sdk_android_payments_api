/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.exception

import com.paysafe.android.core.domain.exception.PaysafeException
import com.paysafe.android.core.exception.genericDisplayMessage
import org.junit.Assert.assertEquals
import org.junit.Test

class ExceptionsTest {

    private val correlationIdInput = "correlationIdForTesting"

    @Test
    fun `IF exception belongs to core THEN errorName RETURNS CoreError`() {
        // Arrange
        val input = invalidAccountIdForPaymentMethodException(correlationIdInput)

        // Act
        val output = input.errorName()

        // Assert
        assertEquals("CoreError", output)
    }

    @Test
    fun `IF exception belongs to card form THEN errorName RETURNS CardFormError`() {
        // Arrange
        val input = invalidAccountIdParameterException(correlationIdInput)

        // Act
        val output = input.errorName()

        // Assert
        assertEquals("CardFormError", output)
    }

    @Test
    fun `IF for a generic paysafe exception THEN errorName RETURNS CardFormError`() {
        // Arrange
        val input = PaysafeException()

        // Act
        val output = input.errorName()

        // Assert
        assertEquals("CardFormError", output)
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
    fun `IF no available payment methods exception is created THEN check output data`() {
        // Arrange
        val expectedDetailedMessage = "There are no available payment methods for this API key."

        // Act
        val output = noAvailablePaymentMethodsException(correlationIdInput)

        // Assert
        assertEquals(NO_AVAILABLE_PAYMENT_METHODS, output.code)
        assertEquals(genericDisplayMessage(NO_AVAILABLE_PAYMENT_METHODS), output.displayMessage)
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
    fun `IF specified hosted field with invalid value exception is created THEN check output data`() {
        // Arrange
        val inputHostedField = "card number"

        // Act
        val output = specifiedHostedFieldWithInvalidValueException(
            inputHostedField, correlationId = correlationIdInput
        )

        // Assert
        assertEquals(SPECIFIED_HOSTED_FIELD_WITH_INVALID_VALUE, output.code)
        assertEquals(
            genericDisplayMessage(SPECIFIED_HOSTED_FIELD_WITH_INVALID_VALUE),
            output.displayMessage
        )
        assertEquals("Invalid fields: $inputHostedField.", output.detailedMessage)
        assertEquals(correlationIdInput, output.correlationId)
    }

    @Test
    fun `IF no hosted field with invalid value exception is created THEN check output data`() {
        // Arrange
        val noHostedField = ""

        // Act
        val output = specifiedHostedFieldWithInvalidValueException(
            correlationId = correlationIdInput
        )

        // Assert
        assertEquals(SPECIFIED_HOSTED_FIELD_WITH_INVALID_VALUE, output.code)
        assertEquals(
            genericDisplayMessage(SPECIFIED_HOSTED_FIELD_WITH_INVALID_VALUE),
            output.displayMessage
        )
        assertEquals(noHostedField, output.detailedMessage)
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
    fun `IF unsupported card brand exception is created THEN check output data`() {
        // Arrange
        val expectedDetailedMessage = "Unsupported card brand used."

        // Act
        val output = unsupportedCardBrandException(correlationIdInput)

        // Assert
        assertEquals(UNSUPPORTED_CARD_BRAND, output.code)
        assertEquals(genericDisplayMessage(UNSUPPORTED_CARD_BRAND), output.displayMessage)
        assertEquals(expectedDetailedMessage, output.detailedMessage)
        assertEquals(correlationIdInput, output.correlationId)
    }

    @Test
    fun `IF no views in card form controller exception is created THEN check output data`() {
        // Arrange
        val expectedDisplayMessage = "PSCardFormController doesn't own any views."

        // Act
        val output = noViewsInCardFormControllerException("")

        // Assert
        assertEquals(expectedDisplayMessage, output.detailedMessage)
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