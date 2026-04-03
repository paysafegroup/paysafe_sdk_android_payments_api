/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.cardnumber

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.paysafe.android.hostedfields.domain.model.PSCardFieldInputEvent
import com.paysafe.android.hostedfields.model.PSCardFieldEventHandler
import com.paysafe.android.hostedfields.util.PS_CARD_NUMBER_TEST_TAG
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class PSCardNumberViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun sut() = PSCardNumberView(
        RuntimeEnvironment.getApplication().baseContext
    )

    @Test
    fun `IF PSCardNumberView PERFORMING creation RETURNS initial conditions`() {
        // Arrange
        val expectedEmptyData = ""

        // Act
        val output = sut()

        // Assert
        assertEquals(expectedEmptyData, output.data)
        assertTrue(output.cardTypeLiveData.isInitialized)
        assertTrue(output.isValidLiveData.isInitialized)
        assertTrue(output.isEmpty())
        assertFalse(output.isValid())
    }

    @Test
    fun `IF PSCardNumberView PERFORMING reset TRIGGER focus loss`() {
        // Arrange
        val viewDoesNotHaveFocus = false

        // Act
        val output = sut()
        output.reset()

        // Assert
        assertEquals(viewDoesNotHaveFocus, output.isFocused)
    }

    @Test
    fun `IF PSCardNumberView PERFORMING content TRIGGER view rendering`() {
        // Arrange
        val output = sut()

        // Act
        composeTestRule.setContent {
            output.Content()
        }

        // Assert
        composeTestRule.onNodeWithTag(PS_CARD_NUMBER_TEST_TAG).assertExists()
    }

    @Test
    fun `PERFORMING previews TRIGGER views rendering`() {
        composeTestRule.setContent {
            Default()
            InputSeparatorWhitespace()
            InputSeparatorNone()
            InputSeparatorDashVisa()
            InputSeparatorSlashAmex()
            Error()
        }
    }

    @Test
    fun `IF PSCardNumberView PERFORMING get labelText RETURNS default label text`() {
        // Arrange
        val expectedDefaultLabelText = "Card number"

        // Act
        val output = sut()

        // Assert
        assertEquals(expectedDefaultLabelText, output.labelText)
    }

    @Test
    fun `IF PSCardNumberView PERFORMING set labelText RETURNS updated label text`() {
        // Arrange
        val customLabelText = "Custom Card Number Label"
        val output = sut()

        // Act
        output.labelText = customLabelText

        // Assert
        assertEquals(customLabelText, output.labelText)
    }

    @Test
    fun `IF PSCardNumberView PERFORMING set labelText multiple times RETURNS latest label text`() {
        // Arrange
        val firstLabelText = "First Label"
        val secondLabelText = "Second Label"
        val thirdLabelText = "Third Label"
        val output = sut()

        // Act
        output.labelText = firstLabelText
        output.labelText = secondLabelText
        output.labelText = thirdLabelText

        // Assert
        assertEquals(thirdLabelText, output.labelText)
    }

    @Test
    fun `IF PSCardNumberView PERFORMING set labelText TRIGGER placeholderString update`() {
        // Arrange
        val customLabelText = "Updated Card Placeholder"
        val output = sut()

        // Act
        output.labelText = customLabelText

        // Assert
        assertEquals(customLabelText, output.placeholderString)
    }

    @Test
    fun `WHEN eventHandler is provided THEN custom eventHandler is used`() {
        // Arrange
        var customEventHandlerCalled = false
        var capturedEvent: PSCardFieldInputEvent? = null

        val customEventHandler = PSCardFieldEventHandler { event ->
            customEventHandlerCalled = true
            capturedEvent = event
        }

        val output = sut()
        output.eventHandler = customEventHandler

        composeTestRule.setContent {
            output.Content()
        }

        // Act
        composeTestRule.onNodeWithTag(PS_CARD_NUMBER_TEST_TAG).performClick()

        // Assert
        assertTrue("Custom event handler should have been called", customEventHandlerCalled)
        assertEquals(
            "Expected FOCUS event to be captured",
            PSCardFieldInputEvent.FOCUS,
            capturedEvent
        )
    }

    @Test
    fun `WHEN eventHandler is provided and input occurs THEN custom eventHandler receives FIELD_VALUE_CHANGE event`() {
        // Arrange
        var fieldValueChangeEventCalled = false

        val customEventHandler = PSCardFieldEventHandler { event ->
            if (event == PSCardFieldInputEvent.FIELD_VALUE_CHANGE) {
                fieldValueChangeEventCalled = true
            }
        }

        val output = sut()
        output.eventHandler = customEventHandler

        composeTestRule.setContent {
            output.Content()
        }

        // Act
        composeTestRule.onNodeWithTag(PS_CARD_NUMBER_TEST_TAG).performTextInput("4111")

        // Assert
        assertTrue(
            "Custom event handler should receive FIELD_VALUE_CHANGE event on input",
            fieldValueChangeEventCalled
        )
    }
}