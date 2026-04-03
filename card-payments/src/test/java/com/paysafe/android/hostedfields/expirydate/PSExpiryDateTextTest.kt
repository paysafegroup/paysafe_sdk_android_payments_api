/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.expirydate

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
import com.paysafe.android.hostedfields.domain.model.PSCardFieldInputEvent
import com.paysafe.android.hostedfields.domain.model.PSExpiryDateState
import com.paysafe.android.hostedfields.domain.model.PSExpiryDateStateImpl
import com.paysafe.android.hostedfields.model.PSCardFieldEventHandler
import com.paysafe.android.hostedfields.provideDefaultPSTheme
import com.paysafe.android.hostedfields.util.PS_EXPIRY_DATE_TEXT_TEST_TAG
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalTestApi::class)
class PSExpiryDateTextTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val eventInvoked = true
    private val eventsNotInvoked = false

    private fun expiryDateTextField() = composeTestRule.onNodeWithTag(PS_EXPIRY_DATE_TEXT_TEST_TAG)

    private fun sut(
        state: PSExpiryDateState = PSExpiryDateStateImpl(),
        eventHandler: PSCardFieldEventHandler = PSCardFieldEventHandler {
            // no-op for tests
        },
        clearsErrorOnInput: Boolean = false,
        validatesEmptyFieldOnBlur: Boolean = true
    ) {
        composeTestRule.setContent {
            PSExpiryDateText(
                state = state,
                labelText = "Expiry date",
                animateTopLabelText = true,
                isValidLiveData = MutableLiveData(false),
                psTheme = provideDefaultPSTheme(),
                eventHandler = eventHandler,
                clearsErrorOnInput = clearsErrorOnInput,
                validatesEmptyFieldOnBlur = validatesEmptyFieldOnBlur
            )
        }
    }

    @Test
    fun `IF PSExpiryDateText PERFORMING just click, field RETURNS empty value`() {
        // Arrange
        val expiryDateStateInput = PSExpiryDateStateImpl()
        sut(expiryDateStateInput)

        // Act
        expiryDateTextField().performClick()

        // Assert
        assertTrue(expiryDateStateInput.isEmpty())
        assertFalse(expiryDateStateInput.isValid())
    }

    @Test
    fun `IF PSExpiryDateText PERFORMING digits input RETURNS non empty value`() {
        // Arrange
        val expiryDateStateInput = PSExpiryDateStateImpl()
        sut(expiryDateStateInput)

        // Act
        expiryDateTextField().performTextInput("12")

        // Assert
        assertFalse(expiryDateStateInput.isEmpty())
        assertFalse(expiryDateStateInput.isValid())
    }

    @Test
    fun `IF PSExpiryDateText PERFORMING done in keyboard after input RETURNS non empty value`() {
        // Arrange
        val expiryDateStateInput = PSExpiryDateStateImpl()
        sut(expiryDateStateInput)

        // Act
        expiryDateTextField().performTextInput("12")
        expiryDateTextField().performImeAction()

        // Assert
        assertFalse(expiryDateStateInput.isEmpty())
        assertFalse(expiryDateStateInput.isValid())
    }

    @Test
    fun `IF PSExpiryDateText PERFORMING valid digits input RETURNS non empty valid value`() {
        // Arrange
        val expiryDateStateInput = PSExpiryDateStateImpl()
        sut(expiryDateStateInput)

        // Act
        expiryDateTextField().performTextInput("1227")

        // Assert
        assertTrue(expiryDateStateInput.isValid())
        assertFalse(expiryDateStateInput.isEmpty())
    }

    @Test
    fun `IF PSExpiryDateText PERFORMING click TRIGGER onFocus call`() {
        // Arrange
        var onFocusCalled = false
        val onFocus: ((PSCardFieldInputEvent) -> Unit) = {
            if (it == PSCardFieldInputEvent.FOCUS) onFocusCalled = true
        }
        sut(eventHandler = onFocus)

        // Act
        expiryDateTextField().performClick()

        // Assert
        expiryDateTextField().assertIsDisplayed()
        expiryDateTextField().assertIsFocused()
        assertEquals(eventInvoked, onFocusCalled)
    }

    @Test
    fun `IF PSExpiryDateText PERFORMING click doesn't TRIGGER unwanted calls`() {
        // Arrange
        var unwantedEventsCalled = false
        val nonFocus: ((PSCardFieldInputEvent) -> Unit) = {
            if (it != PSCardFieldInputEvent.FOCUS && it != PSCardFieldInputEvent.BLUR) unwantedEventsCalled = true
        }
        sut(eventHandler = nonFocus)

        // Act
        expiryDateTextField().performClick()

        // Assert
        assertEquals(eventsNotInvoked, unwantedEventsCalled)
    }

    @Test
    fun `IF PSExpiryDateText PERFORMING one digit input TRIGGER onFieldValueChange and onInvalid call`() {
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
        expiryDateTextField().performTextInput("1")

        // Assert
        assertEquals(eventInvoked, onFieldValueChangeCalled)
        assertEquals(eventInvoked, onInvalidCalled)
    }

    @Test
    fun `IF PSExpiryDateText PERFORMING one digit input doesn't TRIGGER unwanted calls`() {
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
        expiryDateTextField().performTextInput("1")

        // Assert
        assertEquals(eventsNotInvoked, unwantedEventsCalled)
    }

    @Test
    fun `IF PSExpiryDateText PERFORMING one incorrect char input TRIGGER onInvalidCharacter call`() {
        // Arrange
        var onInvalidCharacterCalled = false
        val onInvalidCharacter: ((PSCardFieldInputEvent) -> Unit) = {
            if (it == PSCardFieldInputEvent.INVALID_CHARACTER) {
                onInvalidCharacterCalled = true
            }
        }
        sut(eventHandler = onInvalidCharacter)

        // Act
        expiryDateTextField().performTextInput("-")

        // Assert
        assertEquals(eventInvoked, onInvalidCharacterCalled)
    }

    @Test
    fun `IF PSExpiryDateText PERFORMING one incorrect char input TRIGGER unwanted calls`() {
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
        expiryDateTextField().performTextInput("-")

        // Assert
        assertEquals(eventsNotInvoked, unwantedEventsCalled)
    }

    @Test
    fun `IF PSExpiryDateText PERFORMING correct date TRIGGER onFieldValueChange and onValid call`() {
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
        expiryDateTextField().performTextInput("1231")

        // Assert
        assertEquals(eventInvoked, onFieldValueChangeCalled)
        assertEquals(eventInvoked, onValidCalled)
    }

    @Test
    fun `IF PSExpiryDateText PERFORMING month year input, isValid RETURNS true`() {
        // Arrange
        val expiryDateStateInput = PSExpiryDateStateImpl()
        sut(expiryDateStateInput)

        // Act
        expiryDateTextField().performTextInput("1230")
        expiryDateTextField().performTextInputSelection(TextRange(4))

        // Assert
        assertTrue(expiryDateStateInput.isValid())
    }

    @Test
    fun `WHEN clearsErrorOnInput is true AND user inputs text THEN isValidInUi should be set to true`() {
        // Arrange
        val expiryDateState = PSExpiryDateStateImpl()
        expiryDateState.isValidInUi = false
        sut(
            state = expiryDateState,
            clearsErrorOnInput = true
        )

        // Act
        expiryDateTextField().performTextInput("1")

        // Assert
        assertTrue("isValidInUi should be true when clearsErrorOnInput is true", expiryDateState.isValidInUi)
    }

    @Test
    fun `WHEN clearsErrorOnInput is false AND user inputs text THEN isValidInUi should not be affected`() {
        // Arrange
        val expiryDateState = PSExpiryDateStateImpl()
        expiryDateState.isValidInUi = false
        sut(
            state = expiryDateState,
            clearsErrorOnInput = false
        )

        // Act
        expiryDateTextField().performTextInput("1")

        // Assert
        assertFalse("isValidInUi should remain false when clearsErrorOnInput is false", expiryDateState.isValidInUi)
    }

    @Test
    fun `WHEN clearsErrorOnInput is true AND user inputs invalid date THEN isValidInUi should be set to true`() {
        // Arrange
        val expiryDateState = PSExpiryDateStateImpl()
        expiryDateState.isValidInUi = false
        sut(
            state = expiryDateState,
            clearsErrorOnInput = true
        )

        // Act
        expiryDateTextField().performTextInput("99")

        // Assert
        assertTrue("isValidInUi should be true even for invalid input when clearsErrorOnInput is true", expiryDateState.isValidInUi)
    }

    @Test
    fun `WHEN clearsErrorOnInput is true AND user inputs valid date THEN isValidInUi should be set to true`() {
        // Arrange
        val expiryDateState = PSExpiryDateStateImpl()
        expiryDateState.isValidInUi = false
        sut(
            state = expiryDateState,
            clearsErrorOnInput = true
        )

        // Act
        expiryDateTextField().performTextInput("1230")

        // Assert
        assertTrue("isValidInUi should be true for valid input when clearsErrorOnInput is true", expiryDateState.isValidInUi)
    }

    @Test
    fun `WHEN onDonePressed is triggered AND validatesEmptyFieldOnBlur is true AND field is empty THEN isValidInUi should be false`() {
        // Arrange
        val expiryDateState = PSExpiryDateStateImpl()
        expiryDateState.value = ""
        expiryDateState.isValidInUi = true
        expiryDateState.alreadyShown = true
        sut(
            state = expiryDateState,
            validatesEmptyFieldOnBlur = true
        )

        // Act
        expiryDateTextField().performClick()
        expiryDateTextField().performImeAction()

        // Assert
        assertFalse("isValidInUi should be false for empty field when validatesEmptyFieldOnBlur is true", expiryDateState.isValidInUi)
    }

    @Test
    fun `WHEN onDonePressed is triggered AND validatesEmptyFieldOnBlur is false AND field is empty THEN isValidInUi should be true`() {
        // Arrange
        val expiryDateState = PSExpiryDateStateImpl()
        expiryDateState.value = ""
        expiryDateState.isValidInUi = false
        expiryDateState.alreadyShown = true
        sut(
            state = expiryDateState,
            validatesEmptyFieldOnBlur = false
        )

        // Act
        expiryDateTextField().performClick()
        expiryDateTextField().performImeAction()

        // Assert
        assertTrue("isValidInUi should be true for empty field when validatesEmptyFieldOnBlur is false", expiryDateState.isValidInUi)
    }

    @Test
    fun `WHEN onDonePressed is triggered AND field has valid date THEN isValidInUi should be true`() {
        // Arrange
        val expiryDateState = PSExpiryDateStateImpl()
        expiryDateState.value = "1230"
        expiryDateState.isValidInUi = false
        expiryDateState.alreadyShown = true
        sut(
            state = expiryDateState,
            validatesEmptyFieldOnBlur = true
        )

        // Act
        expiryDateTextField().performClick()
        expiryDateTextField().performImeAction()

        // Assert
        assertTrue("isValidInUi should be true for valid date when Done is pressed", expiryDateState.isValidInUi)
    }

    @Test
    fun `WHEN onDonePressed is triggered AND field has invalid date THEN isValidInUi should be false`() {
        // Arrange
        val expiryDateState = PSExpiryDateStateImpl()
        expiryDateState.value = "0120"
        expiryDateState.isValidInUi = true
        expiryDateState.alreadyShown = true
        sut(
            state = expiryDateState,
            validatesEmptyFieldOnBlur = true
        )

        // Act
        expiryDateTextField().performClick()
        expiryDateTextField().performImeAction()

        // Assert
        assertFalse("isValidInUi should be false for invalid date when Done is pressed", expiryDateState.isValidInUi)
    }

    @Test
    fun `WHEN onDonePressed is triggered AND field has incomplete date THEN isValidInUi should be false`() {
        // Arrange
        val expiryDateState = PSExpiryDateStateImpl()
        expiryDateState.value = "12"
        expiryDateState.isValidInUi = true
        expiryDateState.alreadyShown = true
        sut(
            state = expiryDateState,
            validatesEmptyFieldOnBlur = true
        )

        // Act
        expiryDateTextField().performClick()
        expiryDateTextField().performImeAction()

        // Assert
        assertFalse("isValidInUi should be false for incomplete date when Done is pressed", expiryDateState.isValidInUi)
    }

    @Test
    fun `WHEN onDonePressed is triggered AND validatesEmptyFieldOnBlur is false AND field has non-empty invalid date THEN isValidInUi should be false`() {
        // Arrange
        val expiryDateState = PSExpiryDateStateImpl()
        expiryDateState.value = "1399"
        expiryDateState.isValidInUi = true
        expiryDateState.alreadyShown = true
        sut(
            state = expiryDateState,
            validatesEmptyFieldOnBlur = false
        )

        // Act
        expiryDateTextField().performClick()
        expiryDateTextField().performImeAction()

        // Assert
        assertFalse("isValidInUi should be false for invalid date even when validatesEmptyFieldOnBlur is false", expiryDateState.isValidInUi)
    }
}