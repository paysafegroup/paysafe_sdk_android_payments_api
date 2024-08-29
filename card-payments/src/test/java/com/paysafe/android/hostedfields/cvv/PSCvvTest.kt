/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.cvv

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.MutableLiveData
import com.paysafe.android.hostedfields.domain.model.PSCardFieldInputEvent
import com.paysafe.android.hostedfields.domain.model.PSCvvState
import com.paysafe.android.hostedfields.domain.model.PSCvvStateImpl
import com.paysafe.android.hostedfields.model.DefaultPSCardFieldEventHandler
import com.paysafe.android.hostedfields.model.PSCardFieldEventHandler

import com.paysafe.android.hostedfields.provideDefaultPSTheme
import com.paysafe.android.hostedfields.util.PS_CVV_TEST_TAG
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PSCvvTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val eventInvoked = true
    private val eventsNotInvoked = false

    private fun cvvField() = composeTestRule.onNodeWithTag(PS_CVV_TEST_TAG)

    private fun sut(
        state: PSCvvState = PSCvvStateImpl(),
        eventHandler: PSCardFieldEventHandler? = null
    ) {
        composeTestRule.setContent {
            PSCvv(
                state = state,
                labelText = "CVV Number",
                animateTopLabelText = true,
                isValidLiveData = MutableLiveData(false),
                psTheme = provideDefaultPSTheme(),
                eventHandler = eventHandler ?: DefaultPSCardFieldEventHandler(MutableLiveData(false)),
                isMasked = false
            )
        }
    }

    @Test
    fun `IF PSCvv PERFORMING just click, field RETURNS empty value`() {
        // Arrange
        val cvvStateInput = PSCvvStateImpl()
        sut(cvvStateInput)

        // Act
        cvvField().performClick()

        // Assert
        assertTrue(cvvStateInput.isEmpty())
    }

    @Test
    fun `IF PSCvv PERFORMING numbers input RETURNS non empty value`() {
        // Arrange
        val cvvStateInput = PSCvvStateImpl()
        sut(cvvStateInput)

        // Act
        cvvField().performTextInput("1")

        // Assert
        assertFalse(cvvStateInput.isEmpty())
    }

    @Test
    fun `IF PSCvv PERFORMING done in keyboard after input RETURNS non empty value`() {
        // Arrange
        val cvvStateInput = PSCvvStateImpl()
        sut(cvvStateInput)

        // Act
        cvvField().performTextInput("1")
        cvvField().performImeAction()

        // Assert
        assertFalse(cvvStateInput.isEmpty())
    }

    @Test
    fun `IF PSCvv PERFORMING valid numbers input RETURNS non empty valid value`() {
        // Arrange
        val cvvStateInput = PSCvvStateImpl()
        sut(cvvStateInput)

        // Act
        cvvField().performTextInput("123")

        // Assert
        assertTrue(cvvStateInput.isValid())
        assertFalse(cvvStateInput.isEmpty())
    }

    @Test
    fun `IF PSCvv PERFORMING click TRIGGER onFocus call`() {
        // Arrange
        var onFocusCalled = false
        val eventHandler = PSCardFieldEventHandler { event ->
            if (event == PSCardFieldInputEvent.FOCUS) {
                onFocusCalled = true
            }
        }
        sut(eventHandler = eventHandler)

        // Act
        cvvField().performClick()

        // Assert
        cvvField().assertIsDisplayed()
        cvvField().assertIsFocused()
        assertEquals(eventInvoked, onFocusCalled)
    }

    @Test
    fun `IF PSCvv PERFORMING click doesn't TRIGGER unwanted calls`() {
        // Arrange
        var unwantedEventsCalled = false
        val eventHandler = PSCardFieldEventHandler { event ->
            if (event != PSCardFieldInputEvent.FOCUS) {
                unwantedEventsCalled = true
            }
        }
        sut(eventHandler = eventHandler)

        // Act
        cvvField().performClick()

        // Assert
        assertEquals(eventsNotInvoked, unwantedEventsCalled)
    }

    @Test
    fun `IF PSCvv PERFORMING one digit input TRIGGER onFieldValueChange and onInvalid call`() {
        // Arrange
        var onFieldValueChangeCalled = false
        var onInvalidCalled = false
        val eventHandler = PSCardFieldEventHandler { event ->
            if (event == PSCardFieldInputEvent.FIELD_VALUE_CHANGE) {
                onFieldValueChangeCalled = true
            }
            if (event == PSCardFieldInputEvent.INVALID) {
                onInvalidCalled = true
            }
        }
        sut(eventHandler = eventHandler)

        // Act
        cvvField().performTextInput("1")

        // Assert
        assertEquals(eventInvoked, onFieldValueChangeCalled)
        assertEquals(eventInvoked, onInvalidCalled)
    }

    @Test
    fun `IF PSCvv PERFORMING one digit input doesn't TRIGGER unwanted calls`() {
        // Arrange
        var unwantedEventsCalled = false
        val eventHandler = PSCardFieldEventHandler { event ->
            when (event) {
                PSCardFieldInputEvent.VALID, PSCardFieldInputEvent.INVALID_CHARACTER -> unwantedEventsCalled = true
                else -> {}
            }
        }
        sut(eventHandler = eventHandler)

        // Act
        cvvField().performTextInput("1")

        // Assert
        assertEquals(eventsNotInvoked, unwantedEventsCalled)
    }

    @Test
    fun `IF PSCvv PERFORMING three digit input for amex TRIGGER onFieldValueChange and onInvalid call`() {
        // Arrange
        val cvvStateInput = PSCvvStateImpl(type = PSCreditCardType.AMEX)
        var onFieldValueChangeCalled = false
        var onInvalidCalled = false
        val eventHandler = PSCardFieldEventHandler { event ->
            if (event == PSCardFieldInputEvent.FIELD_VALUE_CHANGE) {
                onFieldValueChangeCalled = true
            }
            if (event == PSCardFieldInputEvent.INVALID) {
                onInvalidCalled = true
            }
        }
        sut(cvvStateInput, eventHandler = eventHandler)

        // Act
        cvvField().performTextInput("456")

        // Assert
        assertEquals(eventInvoked, onFieldValueChangeCalled)
        assertEquals(eventInvoked, onInvalidCalled)
    }

    @Test
    fun `IF PSCvv PERFORMING one incorrect char input TRIGGER onInvalidCharacter call`() {
        // Arrange
        var onInvalidCharacterCalled = false
        val eventHandler = PSCardFieldEventHandler { event ->
            if (event == PSCardFieldInputEvent.INVALID_CHARACTER) {
                onInvalidCharacterCalled = true
            }
        }
        sut(eventHandler = eventHandler)

        // Act
        cvvField().performTextInput("-")

        // Assert
        assertEquals(eventInvoked, onInvalidCharacterCalled)
    }

    @Test
    fun `IF PSCvv PERFORMING one incorrect char input TRIGGER unwanted calls`() {
        // Arrange
        var unwantedEventsCalled = false
        val eventHandler = PSCardFieldEventHandler { event ->
            when (event) {
                PSCardFieldInputEvent.VALID, PSCardFieldInputEvent.INVALID, PSCardFieldInputEvent.FIELD_VALUE_CHANGE -> unwantedEventsCalled = true
                else -> {}
            }
        }
        sut(eventHandler = eventHandler)

        // Act
        cvvField().performTextInput("-")

        // Assert
        assertEquals(eventsNotInvoked, unwantedEventsCalled)
    }

    @Test
    fun `IF PSCvv PERFORMING correct cvv TRIGGER onFieldValueChange and onValid call`() {
        // Arrange
        var onFieldValueChangeCalled = false
        var onValidCalled = false
        val eventHandler = PSCardFieldEventHandler { event ->
            if (event == PSCardFieldInputEvent.FIELD_VALUE_CHANGE) {
                onFieldValueChangeCalled = true
            }
            if (event == PSCardFieldInputEvent.VALID) {
                onValidCalled = true
            }
        }
        sut(eventHandler = eventHandler)

        // Act
        cvvField().performTextInput("789")

        // Assert
        assertEquals(eventInvoked, onFieldValueChangeCalled)
        assertEquals(eventInvoked, onValidCalled)
    }

    @Test
    fun `IF PSCvv PERFORMING correct cvv for amex TRIGGER onFieldValueChange and onValid call`() {
        // Arrange
        val cvvStateInput = PSCvvStateImpl(type = PSCreditCardType.AMEX)
        var onFieldValueChangeCalled = false
        var onValidCalled = false
        val eventHandler = PSCardFieldEventHandler { event ->
            if (event == PSCardFieldInputEvent.FIELD_VALUE_CHANGE) {
                onFieldValueChangeCalled = true
            }
            if (event == PSCardFieldInputEvent.VALID) {
                onValidCalled = true
            }
        }
        sut(cvvStateInput, eventHandler = eventHandler)

        // Act
        cvvField().performTextInput("4321")

        // Assert
        assertEquals(eventInvoked, onFieldValueChangeCalled)
        assertEquals(eventInvoked, onValidCalled)
    }
}
