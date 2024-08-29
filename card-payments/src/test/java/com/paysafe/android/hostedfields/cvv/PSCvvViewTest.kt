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

}