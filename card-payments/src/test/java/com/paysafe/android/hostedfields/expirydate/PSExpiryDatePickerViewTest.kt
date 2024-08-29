/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.expirydate

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.paysafe.android.hostedfields.util.PS_EXPIRY_DATE_PICKER_TEST_TAG
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
class PSExpiryDatePickerViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun sut() = PSExpiryDatePickerView(
        RuntimeEnvironment.getApplication().baseContext
    )

    @Test
    fun `IF PSExpiryDatePickerView PERFORMING creation RETURNS initial conditions`() {
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
    fun `IF PSExpiryDatePickerView PERFORMING reset TRIGGER focus loss`() {
        // Arrange
        val viewDoesNotHaveFocus = false

        // Act
        val output = sut()
        output.reset()

        // Assert
        assertEquals(viewDoesNotHaveFocus, output.isFocused)
    }

    @Test
    fun `IF PSExpiryDatePickerView PERFORMING content TRIGGER view rendering`() {
        // Arrange
        val output = sut()

        // Act
        composeTestRule.setContent {
            output.Content()
        }

        // Assert
        composeTestRule.onNodeWithTag(PS_EXPIRY_DATE_PICKER_TEST_TAG).assertExists()
    }

    @Test
    fun `PERFORMING previews TRIGGER views rendering`() {
        composeTestRule.setContent {
            DefaultExpiryDatePicker()
            InputExpiryDatePicker()
            ErrorExpiryDatePicker()
            PreviewPSExpiryDatePicker()
        }
    }

}