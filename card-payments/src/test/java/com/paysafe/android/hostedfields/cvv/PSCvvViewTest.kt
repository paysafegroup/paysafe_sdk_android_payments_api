/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.cvv

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.paysafe.android.hostedfields.util.PS_CVV_TEST_TAG
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class PSCvvViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun sut() = PSCvvView(
        RuntimeEnvironment.getApplication().baseContext
    )

    @Test
    fun `IF PSCvvView PERFORMING creation RETURNS initial conditions`() {
        // Arrange
        val expectedEmptyData = ""

        // Act
        val output = sut()

        // Assert
        assertEquals(expectedEmptyData, output.data)
        assertTrue(output.isValidLiveData.isInitialized)
        assertTrue(output.isEmpty())
        assertFalse(output.isValid())
    }

    @Test
    fun `IF PSCvvView PERFORMING reset TRIGGER focus loss`() {
        // Arrange
        val viewDoesNotHaveFocus = false

        // Act
        val output = sut()
        output.reset()

        // Assert
        assertEquals(viewDoesNotHaveFocus, output.isFocused)
    }

    @Test
    fun `IF PSCvvView PERFORMING content TRIGGER view rendering`() {
        // Arrange
        val output = sut()

        // Act
        composeTestRule.setContent {
            output.Content()
        }

        // Assert
        composeTestRule.onNodeWithTag(PS_CVV_TEST_TAG).assertExists()
    }

    @Test
    fun `PERFORMING previews TRIGGER views rendering`() {
        composeTestRule.setContent {
            Default()
            Input()
            InputMasked()
            Error()
            ErrorMasked()
            PreviewPSCvv()
        }
    }

    @Test
    fun `IF PSCvvView PERFORMING get labelText RETURNS default label text`() {
        // Arrange
        val expectedDefaultLabelText = "CVV Number"

        // Act
        val output = sut()

        // Assert
        assertEquals(expectedDefaultLabelText, output.labelText)
    }

    @Test
    fun `IF PSCvvView PERFORMING set labelText RETURNS updated label text`() {
        // Arrange
        val customLabelText = "Custom CVV Label"
        val output = sut()

        // Act
        output.labelText = customLabelText

        // Assert
        assertEquals(customLabelText, output.labelText)
    }

    @Test
    fun `IF PSCvvView PERFORMING set labelText multiple times RETURNS latest label text`() {
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

}