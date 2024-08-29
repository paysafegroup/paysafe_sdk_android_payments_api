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
        onEvent: ((PSCardFieldInputEvent) -> Unit)? = null
    ) {
        composeTestRule.setContent {
            PSExpiryDateText(
                state = state,
                labelText = "Expiry date",
                animateTopLabelText = true,
                isValidLiveData = MutableLiveData(false),
                psTheme = provideDefaultPSTheme(),
                onEvent = onEvent
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
        sut(onEvent = onFocus)

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
            if (it != PSCardFieldInputEvent.FOCUS) unwantedEventsCalled = true
        }
        sut(onEvent = nonFocus)

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
        sut(onEvent = onFieldValueChangeAndInvalid)

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
        sut(onEvent = nonFieldValueChange)

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
        sut(onEvent = onInvalidCharacter)

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
        sut(onEvent = nonInvalidCharacter)

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
        sut(onEvent = onFieldValueChangeAndValid)

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
        expiryDateTextField().performTextInput("1224")
        expiryDateTextField().performTextInputSelection(TextRange(4))

        // Assert
        assertTrue(expiryDateStateInput.isValid())
    }

}