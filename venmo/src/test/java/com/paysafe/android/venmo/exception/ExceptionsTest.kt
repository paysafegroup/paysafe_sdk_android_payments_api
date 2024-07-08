package com.paysafe.android.venmo.exception

import com.paysafe.android.core.domain.exception.PaysafeException
import com.paysafe.android.core.exception.genericDisplayMessage
import org.junit.Assert
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
        Assert.assertEquals("APIError", output)
    }

    @Test
    fun `IF exception belongs to core error THEN errorName RETURNS CoreError`() {
        // Arrange
        val input = improperlyCreatedMerchantAccountConfigException(correlationIdInput)

        // Act
        val output = input.errorName()

        // Assert
        Assert.assertEquals("CoreError", output)
    }

    @Test
    fun `IF exception belongs to venmo error THEN errorName RETURNS VenmoError`() {
        // Arrange
        val input = venmoFailedAuthorizationException(correlationIdInput)

        // Act
        val output = input.errorName()

        // Assert
        Assert.assertEquals("VenmoError", output)
    }

    @Test
    fun `IF exception belongs to card form THEN errorName RETURNS CardFormError`() {
        // Arrange
        val input = amountShouldBePositiveException(correlationIdInput)

        // Act
        val output = input.errorName()

        // Assert
        Assert.assertEquals("CardFormError", output)
    }

    @Test
    fun `IF for a generic paysafe exception THEN errorName RETURNS VenmoError`() {
        // Arrange
        val input = PaysafeException()

        // Act
        val output = input.errorName()

        // Assert
        Assert.assertEquals("VenmoError", output)
    }

    @Test
    fun `IF invalid account id for payment method exception is created THEN check output data`() {
        // Arrange
        val expectedDetailedMessage = "Invalid account id for Venmo."

        // Act
        val output = invalidAccountIdForPaymentMethodException(correlationIdInput)

        // Assert
        Assert.assertEquals(INVALID_ACCOUNT_ID_FOR_PAYMENT_METHOD, output.code)
        Assert.assertEquals(
            genericDisplayMessage(INVALID_ACCOUNT_ID_FOR_PAYMENT_METHOD),
            output.displayMessage
        )
        Assert.assertEquals(expectedDetailedMessage, output.detailedMessage)
        Assert.assertEquals(correlationIdInput, output.correlationId)
    }

    @Test
    fun `IF pay pal user cancelled exception is created THEN check output data`() {
        // Arrange
        val expectedDetailedMessage = "User aborted authentication."

        // Act
        val output = venmoUserCancelledException(correlationIdInput)

        // Assert
        Assert.assertEquals(VENMO_USER_CANCELLED, output.code)
        Assert.assertEquals(genericDisplayMessage(VENMO_USER_CANCELLED), output.displayMessage)
        Assert.assertEquals(expectedDetailedMessage, output.detailedMessage)
        Assert.assertEquals(correlationIdInput, output.correlationId)
    }

    @Test
    fun `IF pay pal failed authorization exception is created THEN check output data`() {
        // Arrange
        val expectedDetailedMessage = "Venmo failed authorization."

        // Act
        val output = venmoFailedAuthorizationException(correlationIdInput)

        // Assert
        Assert.assertEquals(VENMO_FAILED_AUTHORIZATION, output.code)
        Assert.assertEquals(
            genericDisplayMessage(VENMO_FAILED_AUTHORIZATION),
            output.displayMessage
        )
        Assert.assertEquals(expectedDetailedMessage, output.detailedMessage)
        Assert.assertEquals(correlationIdInput, output.correlationId)
    }

    @Test
    fun `IF currency code invalid iso exception is created THEN check output data`() {
        // Arrange
        val expectedDetailedMessage = "Invalid currency parameter."

        // Act
        val output = currencyCodeInvalidIsoException(correlationIdInput)

        // Assert
        Assert.assertEquals(CURRENCY_CODE_INVALID_ISO, output.code)
        Assert.assertEquals(genericDisplayMessage(CURRENCY_CODE_INVALID_ISO), output.displayMessage)
        Assert.assertEquals(expectedDetailedMessage, output.detailedMessage)
        Assert.assertEquals(correlationIdInput, output.correlationId)
    }

    @Test
    fun `IF improperly created merchant account config exception is created THEN check output data`() {
        // Arrange
        val expectedDetailedMessage = "Account not configured correctly."

        // Act
        val output = improperlyCreatedMerchantAccountConfigException(correlationIdInput)

        // Assert
        Assert.assertEquals(IMPROPERLY_CREATED_MERCHANT_ACCOUNT_CONFIG, output.code)
        Assert.assertEquals(
            genericDisplayMessage(IMPROPERLY_CREATED_MERCHANT_ACCOUNT_CONFIG),
            output.displayMessage
        )
        Assert.assertEquals(expectedDetailedMessage, output.detailedMessage)
        Assert.assertEquals(correlationIdInput, output.correlationId)
    }

    @Test
    fun `IF amount should be positive exception is created THEN check output data`() {
        // Arrange
        val expectedDetailedMessage =
            "Amount should be a number greater than 0 no longer than 11 characters."

        // Act
        val output = amountShouldBePositiveException(correlationIdInput)

        // Assert
        Assert.assertEquals(AMOUNT_SHOULD_BE_POSITIVE, output.code)
        Assert.assertEquals(genericDisplayMessage(AMOUNT_SHOULD_BE_POSITIVE), output.displayMessage)
        Assert.assertEquals(expectedDetailedMessage, output.detailedMessage)
        Assert.assertEquals(correlationIdInput, output.correlationId)
    }

    @Test
    fun `IF tokenization already in progress exception is created THEN check output data`() {
        // Arrange
        val expectedDetailedMessage = "Tokenization is already in progress."

        // Act
        val output = tokenizationAlreadyInProgressException(correlationIdInput)

        // Assert
        Assert.assertEquals(TOKENIZATION_ALREADY_IN_PROGRESS, output.code)
        Assert.assertEquals(
            genericDisplayMessage(TOKENIZATION_ALREADY_IN_PROGRESS),
            output.displayMessage
        )
        Assert.assertEquals(expectedDetailedMessage, output.detailedMessage)
        Assert.assertEquals(correlationIdInput, output.correlationId)
    }

    @Test
    fun `IF generic api error exception is created THEN check output data`() {
        // Arrange
        val expectedDetailedMessage = "Unhandled error occurred."

        // Act
        val output = genericApiErrorException(correlationIdInput)

        // Assert
        Assert.assertEquals(GENERIC_API_ERROR, output.code)
        Assert.assertEquals(genericDisplayMessage(GENERIC_API_ERROR), output.displayMessage)
        Assert.assertEquals(expectedDetailedMessage, output.detailedMessage)
        Assert.assertEquals(correlationIdInput, output.correlationId)
    }

    @Test
    fun `IF sdk not initialized exception is created THEN check output data`() {
        // Arrange
        val expectedDetailedMessage = "PaysafeSDK is not initialized."

        // Act
        val output = sdkNotInitializedException()

        // Assert
        Assert.assertEquals(SDK_NOT_INITIALIZED, output.code)
        Assert.assertEquals(genericDisplayMessage(SDK_NOT_INITIALIZED), output.displayMessage)
        Assert.assertEquals(expectedDetailedMessage, output.detailedMessage)
        Assert.assertEquals("", output.correlationId) // no correlationId in this case
    }

    @Test
    fun `IF payment handle status expired or failed exception is created THEN check output data`() {
        // Arrange
        val status = "EXPIRED"
        val expectedDetailedMessage = "Status of the payment handle is $status."

        // Act
        val output = paymentHandleStatusExpiredOrFailedException(status, correlationIdInput)

        // Assert
        Assert.assertEquals(PAYMENT_HANDLE_CREATION_FAILED, output.code)
        Assert.assertEquals(genericDisplayMessage(PAYMENT_HANDLE_CREATION_FAILED), output.displayMessage)
        Assert.assertEquals(expectedDetailedMessage, output.detailedMessage)
        Assert.assertEquals(correlationIdInput, output.correlationId)
    }

    @Test
    fun `IF venmo app is not installed failed exception is created THEN check output data`() {
        // Arrange
        val expectedDetailedMessage = "Venmo App Doesn't exist."

        // Act
        val output = venmoAppIsNotInstalledFailedException()

        // Assert
        Assert.assertEquals(VENMO_FAILED_AUTHORIZATION, output.code)
        Assert.assertEquals(genericDisplayMessage(VENMO_FAILED_AUTHORIZATION), output.displayMessage)
        Assert.assertEquals(expectedDetailedMessage, output.detailedMessage)
        Assert.assertEquals("", output.correlationId) // Assuming no correlationId is set for this exception
    }
}