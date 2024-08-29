/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.expirydate

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.paysafe.android.hostedfields.util.PS_EXPIRY_DATE_TEXT_TEST_TAG
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class PSExpiryDateTextViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun sut() = PSExpiryDateTextView(
        RuntimeEnvironment.getApplication().baseContext
    )

    @Test
    fun `IF PSExpiryDateTextView PERFORMING creation RETURNS initial conditions`() {
        // Arrange
        val expectedEmptyData = ""
        val expected2ndMillenniumPrefix = "20"

        // Act
        val output = sut()

        // Assert
        assertEquals(expectedEmptyData, output.monthData)
        assertEquals(expected2ndMillenniumPrefix, output.yearData)
        assertTrue(output.isValidLiveData.isInitialized)
        assertNotNull(output.viewContext)
        assertFalse(output.isEmpty())
        assertFalse(output.isValid())
    }

    @Test
    fun `IF PSExpiryDateTextView PERFORMING reset TRIGGER focus loss`() {
        // Arrange
        val viewDoesNotHaveFocus = false

        // Act
        val output = sut()
        output.reset()

        // Assert
        assertEquals(viewDoesNotHaveFocus, output.isFocused)
    }

    @Test
    fun `IF PSExpiryDateTextView PERFORMING content TRIGGER view rendering`() {
        // Arrange
        val output = sut()

        // Act
        composeTestRule.setContent {
            output.Content()
        }

        // Assert
        composeTestRule.onNodeWithTag(PS_EXPIRY_DATE_TEXT_TEST_TAG).assertExists()
    }

    @Test
    fun `PERFORMING previews TRIGGER views rendering`() {
        composeTestRule.setContent {
            DefaultExpiryDateText()
            InputExpiryDateText()
            ErrorExpiryDateText()
            PreviewPSExpiryDateText()
        }
    }

}