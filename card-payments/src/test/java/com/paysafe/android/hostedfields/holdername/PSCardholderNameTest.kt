/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.holdername

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.MutableLiveData
import com.paysafe.android.hostedfields.domain.model.PSCardFieldInputEvent
import com.paysafe.android.hostedfields.domain.model.PSCardholderNameState
import com.paysafe.android.hostedfields.domain.model.PSCardholderNameStateImpl
import com.paysafe.android.hostedfields.model.DefaultPSCardFieldEventHandler
import com.paysafe.android.hostedfields.model.PSCardFieldEventHandler

import com.paysafe.android.hostedfields.provideDefaultPSTheme
import com.paysafe.android.hostedfields.util.PS_CARD_HOLDER_NAME_TEST_TAG
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PSCardholderNameTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val eventInvoked = true
    private val eventsNotInvoked = false

    private fun holderNameField() = composeTestRule.onNodeWithTag(PS_CARD_HOLDER_NAME_TEST_TAG)

    private fun sut(
        state: PSCardholderNameState = PSCardholderNameStateImpl(),
        eventHandler: PSCardFieldEventHandler = DefaultPSCardFieldEventHandler(MutableLiveData(false))
    ) {
        composeTestRule.setContent {
            PSCardholderName(
                state = state,
                labelText = "Name on card",
                animateTopLabelText = true,
                psTheme = provideDefaultPSTheme(),
                eventHandler = eventHandler
            )
        }
    }

    @Test
    fun `IF PSCardholderName PERFORMING just click, field RETURNS empty value`() {
        // Arrange
        val holderNameStateInput = PSCardholderNameStateImpl()
        sut(holderNameStateInput)

        // Act
        holderNameField().performClick()

        // Assert
        assertTrue(holderNameStateInput.isEmpty())
    }

    @Test
    fun `IF PSCardholderName PERFORMING chars input RETURNS non empty value`() {
        // Arrange
        val holderNameStateInput = PSCardholderNameStateImpl()
        sut(holderNameStateInput)

        // Act
        holderNameField().performTextInput("John")

        // Assert
        assertFalse(holderNameStateInput.isEmpty())
    }

    @Test
    fun `IF PSCardholderName PERFORMING done in keyboard after input RETURNS non empty value`() {
        // Arrange
        val holderNameStateInput = PSCardholderNameStateImpl()
        sut(holderNameStateInput)

        // Act
        holderNameField().performTextInput("John")
        holderNameField().performImeAction()

        // Assert
        assertFalse(holderNameStateInput.isEmpty())
    }

    @Test
    fun `IF PSCardholderName PERFORMING valid chars input RETURNS non empty valid value`() {
        // Arrange
        val holderNameStateInput = PSCardholderNameStateImpl()
        sut(holderNameStateInput)

        // Act
        holderNameField().performTextInput("John Doe")

        // Assert
        assertTrue(holderNameStateInput.isValid())
        assertFalse(holderNameStateInput.isEmpty())
    }

    @Test
    fun `IF PSCardholderName PERFORMING invalid chars input RETURNS invalid value`() {
        // Arrange
        val holderNameStateInput = PSCardholderNameStateImpl()
        sut(holderNameStateInput)

        // Act
        holderNameField().performTextInput("Space at the end ")

        // Assert
        assertFalse(holderNameStateInput.isValid())
    }

    @Test
    fun `IF PSCardholderName PERFORMING click TRIGGER onFocus call`() {
        // Arrange
        var onFocusCalled = false
        val eventHandler = object : PSCardFieldEventHandler {
            override fun handleEvent(event: PSCardFieldInputEvent) {
                if (event == PSCardFieldInputEvent.FOCUS) onFocusCalled = true
            }
        }
        sut(eventHandler = eventHandler)

        // Act
        holderNameField().performClick()

        // Assert
        holderNameField().assertIsDisplayed()
        holderNameField().assertIsFocused()
        assertEquals(eventInvoked, onFocusCalled)
    }

    @Test
    fun `IF PSCardholderName PERFORMING click doesn't TRIGGER unwanted calls`() {
        // Arrange
        var unwantedEventsCalled = false
        val eventHandler = object : PSCardFieldEventHandler {
            override fun handleEvent(event: PSCardFieldInputEvent) {
                if (event != PSCardFieldInputEvent.FOCUS) unwantedEventsCalled = true
            }
        }
        sut(eventHandler = eventHandler)

        // Act
        holderNameField().performClick()

        // Assert
        assertEquals(eventsNotInvoked, unwantedEventsCalled)
    }

    @Test
    fun `IF PSCardholderName PERFORMING one char input TRIGGER onFieldValueChange and onValid call`() {
        // Arrange
        var onFieldValueChangeCalled = false
        var onValidCalled = false
        val eventHandler = object : PSCardFieldEventHandler {
            override fun handleEvent(event: PSCardFieldInputEvent) {
                when (event) {
                    PSCardFieldInputEvent.FIELD_VALUE_CHANGE -> onFieldValueChangeCalled = true
                    PSCardFieldInputEvent.VALID -> onValidCalled = true
                    else -> {}
                }
            }
        }
        sut(eventHandler = eventHandler)

        // Act
        holderNameField().performTextInput("J")

        // Assert
        assertEquals(eventInvoked, onFieldValueChangeCalled)
        assertEquals(eventInvoked, onValidCalled)
    }

    @Test
    fun `IF PSCardholderName PERFORMING one char input doesn't TRIGGER unwanted calls`() {
        // Arrange
        var unwantedEventsCalled = false
        val eventHandler = object : PSCardFieldEventHandler {
            override fun handleEvent(event: PSCardFieldInputEvent) {
                if (event == PSCardFieldInputEvent.INVALID || event == PSCardFieldInputEvent.INVALID_CHARACTER) {
                    unwantedEventsCalled = true
                }
            }
        }
        sut(eventHandler = eventHandler)

        // Act
        holderNameField().performTextInput("J")

        // Assert
        assertEquals(eventsNotInvoked, unwantedEventsCalled)
    }

    @Test
    fun `IF PSCardholderName PERFORMING one incorrect char input TRIGGER onInvalidCharacter call`() {
        // Arrange
        var onInvalidCharacterCalled = false
        val eventHandler = object : PSCardFieldEventHandler {
            override fun handleEvent(event: PSCardFieldInputEvent) {
                if (event == PSCardFieldInputEvent.INVALID_CHARACTER) {
                    onInvalidCharacterCalled = true
                }
            }
        }
        sut(eventHandler = eventHandler)

        // Act
        holderNameField().performTextInput("1")

        // Assert
        assertEquals(eventInvoked, onInvalidCharacterCalled)
    }

    @Test
    fun `IF PSCardholderName PERFORMING one incorrect char input TRIGGER unwanted calls`() {
        // Arrange
        var unwantedEventsCalled = false
        val eventHandler = object : PSCardFieldEventHandler {
            override fun handleEvent(event: PSCardFieldInputEvent) {
                if (event == PSCardFieldInputEvent.VALID || event == PSCardFieldInputEvent.INVALID || event == PSCardFieldInputEvent.FIELD_VALUE_CHANGE) {
                    unwantedEventsCalled = true
                }
            }
        }
        sut(eventHandler = eventHandler)

        // Act
        holderNameField().performTextInput("1")

        // Assert
        assertEquals(eventsNotInvoked, unwantedEventsCalled)
    }

    @Test
    fun `IF PSCardholderName PERFORMING space at end input TRIGGER onFieldValueChange and onInvalid call`() {
        // Arrange
        var onFieldValueChangeCalled = false
        var onInvalidCalled = false
        val eventHandler = object : PSCardFieldEventHandler {
            override fun handleEvent(event: PSCardFieldInputEvent) {
                when (event) {
                    PSCardFieldInputEvent.FIELD_VALUE_CHANGE -> onFieldValueChangeCalled = true
                    PSCardFieldInputEvent.INVALID -> onInvalidCalled = true
                    else -> {}
                }
            }
        }
        sut(eventHandler = eventHandler)

        // Act
        holderNameField().performTextInput("John Space ")

        // Assert
        assertEquals(eventInvoked, onFieldValueChangeCalled)
        assertEquals(eventInvoked, onInvalidCalled)
    }

    @Test
    fun `IF focus changes to focused state THEN trigger onFocus event`() {
        // Arrange
        var onFocusCalled = false
        val eventHandler = object : PSCardFieldEventHandler {
            override fun handleEvent(event: PSCardFieldInputEvent) {
                if (event == PSCardFieldInputEvent.FOCUS) onFocusCalled = true
            }
        }
        val holderNameState = PSCardholderNameStateImpl()
        sut(holderNameState, eventHandler)

        // Act
        holderNameField().performClick() // Simulate focus

        // Assert
        assertEquals(eventInvoked, onFocusCalled)
    }

    @Test
    fun `IF focus changes to focused state THEN doesn't trigger unwanted events`() {
        // Arrange
        var unwantedEventsCalled = false
        val eventHandler = object : PSCardFieldEventHandler {
            override fun handleEvent(event: PSCardFieldInputEvent) {
                if (event != PSCardFieldInputEvent.FOCUS) unwantedEventsCalled = true
            }
        }
        val holderNameState = PSCardholderNameStateImpl()
        sut(holderNameState, eventHandler)

        // Act
        holderNameField().performClick() // Simulate focus

        // Assert
        assertEquals(eventsNotInvoked, unwantedEventsCalled)
    }

    @Test
    fun `IF focus changes THEN set alreadyShown to true`() {
        // Arrange
        val holderNameState = PSCardholderNameStateImpl()
        sut(holderNameState)

        // Act
        holderNameField().performClick() // Focus
        composeTestRule.waitForIdle()
        holderNameField().performClick() // Unfocus

        // Assert
        assertTrue(holderNameState.alreadyShown)
    }

    @Test
    fun `renders DefaultPSCardholderName without errors`() {
        // Render the DefaultPSCardholderName composable
        composeTestRule.setContent {
            DefaultPSCardholderName()
        }

        // Check that the cardholder name field is displayed
        composeTestRule.onNodeWithTag(PS_CARD_HOLDER_NAME_TEST_TAG).assertIsDisplayed()
    }

    @Test
    fun `renders DefaultPSCardholderName with correct label text`() {
        // Render the DefaultPSCardholderName composable
        composeTestRule.setContent {
            DefaultPSCardholderName()
        }

        // Check that the label "Name on card" is displayed
        composeTestRule.onNodeWithText("Name on card").assertIsDisplayed()
    }
}
