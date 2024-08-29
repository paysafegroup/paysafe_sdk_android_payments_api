/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.cardnumber

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextInputSelection
import androidx.compose.ui.text.TextRange
import androidx.lifecycle.MutableLiveData
import com.paysafe.android.hostedfields.domain.model.CardNumberSeparator
import com.paysafe.android.hostedfields.domain.model.PSCardFieldInputEvent
import com.paysafe.android.hostedfields.domain.model.PSCardNumberState
import com.paysafe.android.hostedfields.domain.model.PSCardNumberStateImpl

import com.paysafe.android.hostedfields.model.DefaultPSCardFieldEventHandler
import com.paysafe.android.hostedfields.model.PSCardFieldEventHandler

import com.paysafe.android.hostedfields.provideDefaultPSTheme
import com.paysafe.android.hostedfields.util.PS_CARD_NUMBER_TEST_TAG
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalTestApi::class)
class PSCardNumberTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val eventInvoked = true
    private val eventsNotInvoked = false

    private fun cardNumberField() = composeTestRule.onNodeWithTag(PS_CARD_NUMBER_TEST_TAG)

    private fun sut(
        state: PSCardNumberState = PSCardNumberStateImpl(),
        numbersSeparator: CardNumberSeparator = CardNumberSeparator.DASH,
        eventHandler: PSCardFieldEventHandler = DefaultPSCardFieldEventHandler(MutableLiveData(false))
    ) {
        composeTestRule.setContent {
            PSCardNumber(
                state = state,
                labelText = "Card number",
                cardNumberModifier = PSCardNumberModifier(
                    modifier = Modifier,
                    cardBrandModifier = Modifier
                ),
                animateTopLabelText = true,
                cardNumberLiveData = PSCardNumberLiveData(
                    cardTypeLiveData = MutableLiveData(PSCreditCardType.UNKNOWN),
                    isValidLiveData = MutableLiveData(false)
                ),
                psTheme = provideDefaultPSTheme(),
                separator = numbersSeparator,
                eventHandler = eventHandler
            )
        }
    }

    @Test
    fun `IF PSCardNumber PERFORMING just click, field RETURNS empty value`() {
        // Arrange
        val cardNumberStateInput = PSCardNumberStateImpl()
        sut(cardNumberStateInput)

        // Act
        cardNumberField().performClick()

        // Assert
        assertTrue(cardNumberStateInput.isEmpty())
        assertFalse(cardNumberStateInput.isValid())
    }

    @Test
    fun `IF PSCardNumber PERFORMING digits input RETURNS non empty value`() {
        // Arrange
        val cardNumberStateInput = PSCardNumberStateImpl()
        sut(cardNumberStateInput)

        // Act
        cardNumberField().performTextInput("123")

        // Assert
        assertFalse(cardNumberStateInput.isEmpty())
        assertFalse(cardNumberStateInput.isValid())
    }

    @Test
    fun `IF PSCardNumber PERFORMING done in keyboard after input RETURNS non empty value`() {
        // Arrange
        val cardNumberStateInput = PSCardNumberStateImpl()
        sut(cardNumberStateInput)

        // Act
        cardNumberField().performTextInput("123")
        cardNumberField().performImeAction()

        // Assert
        assertFalse(cardNumberStateInput.isEmpty())
        assertFalse(cardNumberStateInput.isValid())
    }

    @Test
    fun `IF PSCardNumber PERFORMING valid digits input RETURNS non empty valid value`() {
        // Arrange
        val cardNumberStateInput = PSCardNumberStateImpl()
        sut(cardNumberStateInput)

        // Act
        cardNumberField().performTextInput("1234567890123452")

        // Assert
        assertTrue(cardNumberStateInput.isValid())
        assertFalse(cardNumberStateInput.isEmpty())
    }

    @Test
    fun `IF PSCardNumber PERFORMING click TRIGGER onFocus call`() {
        // Arrange
        var onFocusCalled = false
        val onFocus: ((PSCardFieldInputEvent) -> Unit) = {
            if (it == PSCardFieldInputEvent.FOCUS) onFocusCalled = true
        }
        sut(eventHandler = onFocus)

        // Act
        cardNumberField().performClick()

        // Assert
        cardNumberField().assertIsDisplayed()
        cardNumberField().assertIsFocused()
        assertEquals(eventInvoked, onFocusCalled)
    }

    @Test
    fun `IF PSCardNumber PERFORMING click doesn't TRIGGER unwanted calls`() {
        // Arrange
        var unwantedEventsCalled = false
        val nonFocus: ((PSCardFieldInputEvent) -> Unit) = {
            if (it != PSCardFieldInputEvent.FOCUS) unwantedEventsCalled = true
        }
        sut(eventHandler = nonFocus)

        // Act
        cardNumberField().performClick()

        // Assert
        assertEquals(eventsNotInvoked, unwantedEventsCalled)
    }

    @Test
    fun `IF PSCardNumber PERFORMING one digit input TRIGGER onFieldValueChange and onInvalid call`() {
        // Arrange
        var onFieldValueChangeCalled = false
        var onInvalidCalled = false
        val onFieldValueChangeAndInvalid: ((PSCardFieldInputEvent) -> Unit) = {
            if (it == PSCardFieldInputEvent.FIELD_VALUE_CHANGE) {
                onFieldValueChangeCalled = true
            }
            if (it == PSCardFieldInputEvent.INVALID) {
                onInvalidCalled = true
            }
        }
        sut(eventHandler = onFieldValueChangeAndInvalid)

        // Act
        cardNumberField().performTextInput("1")

        // Assert
        assertEquals(eventInvoked, onFieldValueChangeCalled)
        assertEquals(eventInvoked, onInvalidCalled)
    }

    @Test
    fun `IF PSCardNumber PERFORMING one digit input doesn't TRIGGER unwanted calls`() {
        // Arrange
        var unwantedEventsCalled = false
        val nonFieldValueChange: ((PSCardFieldInputEvent) -> Unit) = {
            when (it) {
                PSCardFieldInputEvent.VALID -> unwantedEventsCalled = true
                PSCardFieldInputEvent.INVALID_CHARACTER -> unwantedEventsCalled = true
                else -> {}
            }
        }
        sut(eventHandler = nonFieldValueChange)

        // Act
        cardNumberField().performTextInput("1")

        // Assert
        assertEquals(eventsNotInvoked, unwantedEventsCalled)
    }

    @Test
    fun `IF PSCardNumber PERFORMING one incorrect char input TRIGGER onInvalidCharacter call`() {
        // Arrange
        var onInvalidCharacterCalled = false
        val onInvalidCharacter: ((PSCardFieldInputEvent) -> Unit) = {
            if (it == PSCardFieldInputEvent.INVALID_CHARACTER) {
                onInvalidCharacterCalled = true
            }
        }
        sut(eventHandler = onInvalidCharacter)

        // Act
        cardNumberField().performTextInput("-")

        // Assert
        assertEquals(eventInvoked, onInvalidCharacterCalled)
    }

    @Test
    fun `IF PSCardNumber PERFORMING one incorrect char input TRIGGER unwanted calls`() {
        // Arrange
        var unwantedEventsCalled = false
        val nonInvalidCharacter: ((PSCardFieldInputEvent) -> Unit) = {
            when (it) {
                PSCardFieldInputEvent.VALID -> unwantedEventsCalled = true
                PSCardFieldInputEvent.INVALID -> unwantedEventsCalled = true
                PSCardFieldInputEvent.FIELD_VALUE_CHANGE -> unwantedEventsCalled = true
                else -> {}
            }
        }
        sut(eventHandler = nonInvalidCharacter)

        // Act
        cardNumberField().performTextInput("-")

        // Assert
        assertEquals(eventsNotInvoked, unwantedEventsCalled)
    }

    @Test
    fun `IF PSCardNumber PERFORMING correct card number TRIGGER onFieldValueChange and onValid call`() {
        // Arrange
        var onFieldValueChangeCalled = false
        var onValidCalled = false
        val onFieldValueChangeAndValid: ((PSCardFieldInputEvent) -> Unit) = {
            if (it == PSCardFieldInputEvent.FIELD_VALUE_CHANGE) {
                onFieldValueChangeCalled = true
            }
            if (it == PSCardFieldInputEvent.VALID) {
                onValidCalled = true
            }
        }
        sut(eventHandler = onFieldValueChangeAndValid)

        // Act
        cardNumberField().performTextInput("1234567890123452")

        // Assert
        assertEquals(eventInvoked, onFieldValueChangeCalled)
        assertEquals(eventInvoked, onValidCalled)
    }

    @Test
    fun `IF PSCardNumber PERFORMING amex card input, isValid RETURNS true`() {
        // Arrange
        val cardNumberStateInput = PSCardNumberStateImpl()
        cardNumberStateInput.type = PSCreditCardType.AMEX
        sut(cardNumberStateInput)

        // Act
        cardNumberField().performTextInput("340000000000009")
        cardNumberField().performTextInputSelection(TextRange(14))

        // Assert
        assertTrue(cardNumberStateInput.isValid())
    }

    @Test
    fun `IF PSCardNumber PERFORMING mastercard input, isValid RETURNS true`() {
        // Arrange
        val cardNumberStateInput = PSCardNumberStateImpl()
        cardNumberStateInput.type = PSCreditCardType.MASTERCARD
        sut(cardNumberStateInput)

        // Act
        cardNumberField().performTextInput("5555555555554444")
        cardNumberField().performTextInputSelection(TextRange(15))

        // Assert
        assertTrue(cardNumberStateInput.isValid())
    }

    @Test
    fun `IF PSCardNumber PERFORMING unknown card input, isValid RETURNS true`() {
        // Arrange
        val cardNumberStateInput = PSCardNumberStateImpl()
        cardNumberStateInput.type = PSCreditCardType.UNKNOWN
        sut(cardNumberStateInput, CardNumberSeparator.NONE)

        // Act
        cardNumberField().performTextInput("1234567890123452")
        cardNumberField().performTextInputSelection(TextRange(15))

        // Assert
        assertTrue(cardNumberStateInput.isValid())
    }

    @Test
    fun `IF PSCardNumber PERFORMING visa card input, isValid RETURNS true`() {
        // Arrange
        val cardNumberStateInput = PSCardNumberStateImpl()
        cardNumberStateInput.type = PSCreditCardType.VISA
        sut(cardNumberStateInput, CardNumberSeparator.WHITESPACE)

        // Act
        cardNumberField().performTextInput("4111111111111111")
        cardNumberField().performTextInputSelection(TextRange(15))

        // Assert
        assertTrue(cardNumberStateInput.isValid())
    }

    @Test
    fun `IF PSCardNumber PERFORMING discover card input, isValid RETURNS true`() {
        // Arrange
        val cardNumberStateInput = PSCardNumberStateImpl()
        cardNumberStateInput.type = PSCreditCardType.DISCOVER
        sut(cardNumberStateInput, CardNumberSeparator.SLASH)

        // Act
        cardNumberField().performTextInput("6011000990139424")
        cardNumberField().performTextInputSelection(TextRange(15))

        // Assert
        assertTrue(cardNumberStateInput.isValid())
    }

    @Test
    fun `should return 0 for unknown or unsupported credit card types`() {
        // Assuming PSCreditCardType.UNKNOWN or any other type not explicitly handled
        assertEquals(0, getCreditCardIcon(PSCreditCardType.UNKNOWN))
    }

}