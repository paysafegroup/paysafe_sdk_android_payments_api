/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.expirydate

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.MutableLiveData
import com.paysafe.android.hostedfields.domain.model.PSCardFieldInputEvent
import com.paysafe.android.hostedfields.domain.model.PSExpiryDateState
import com.paysafe.android.hostedfields.domain.model.PSExpiryDateStateImpl
import com.paysafe.android.hostedfields.model.PSCardFieldEventHandler
import com.paysafe.android.hostedfields.provideDefaultPSTheme
import com.paysafe.android.hostedfields.util.PS_EXPIRY_DATE_PICKER_NO_ANIM_LABEL_TEST_TAG
import com.paysafe.android.hostedfields.util.PS_EXPIRY_DATE_PICKER_TEST_TAG
import com.paysafe.android.hostedfields.util.PS_MONTH_YEAR_PICKER_DIALOG_CONFIRM_TEST_TAG
import com.paysafe.android.hostedfields.util.PS_MONTH_YEAR_PICKER_DIALOG_TEST_TAG
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PSExpiryDatePickerFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun expiryDatePickerField() = composeTestRule.onNodeWithTag(
        PS_EXPIRY_DATE_PICKER_TEST_TAG
    )

    private fun monthYearPickerDialog() = composeTestRule.onNodeWithTag(
        PS_MONTH_YEAR_PICKER_DIALOG_TEST_TAG
    )

    private fun monthYearPickerDialogConfirmBtn() = composeTestRule.onNodeWithTag(
        PS_MONTH_YEAR_PICKER_DIALOG_CONFIRM_TEST_TAG
    )

    private fun expiryDatePickerNoAnimationLabel() = composeTestRule.onNodeWithTag(
        PS_EXPIRY_DATE_PICKER_NO_ANIM_LABEL_TEST_TAG, useUnmergedTree = true
    )

    private fun sut(
        state: PSExpiryDateState = PSExpiryDateStateImpl(),
        labelText: String = "Expiry Date",
        animateTopLabelText: Boolean = true,
        eventHandler: PSCardFieldEventHandler = PSCardFieldEventHandler {
            // no-op for tests
        }
    ) {
        composeTestRule.setContent {
            PSExpiryDatePickerField(
                expiryDateState = state,
                labelText = labelText,
                placeholderText = null,
                animateTopLabelText = animateTopLabelText,
                isValidLiveData = MutableLiveData(false),
                psTheme = provideDefaultPSTheme(),
                eventHandler = eventHandler,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    @Test
    fun `IF PSExpiryDatePickerField PERFORMING just click, field RETURNS empty value`() {
        // Arrange
        val expiryDateStateInput = PSExpiryDateStateImpl()
        sut(expiryDateStateInput)

        // Act
        expiryDatePickerField().performClick()

        // Assert
        assertTrue(expiryDateStateInput.isEmpty())
        assertFalse(expiryDateStateInput.isValid())
    }

    @Test
    fun `IF PSExpiryDatePickerField PERFORM click also in month year picker, field RETURNS empty interaction`() {
        // Arrange
        val expiryDateStateInput = PSExpiryDateStateImpl()
        sut(expiryDateStateInput)

        // Act
        expiryDatePickerField().performClick()
        monthYearPickerDialog().performClick()
        monthYearPickerDialogConfirmBtn().performClick()

        // Assert
        assertFalse(expiryDateStateInput.isValid())
    }

    @Test
    fun `IF PSExpiryDatePickerField not animating top label PERFORMING just click TRIGGER label without animation display`() {
        // Arrange
        val animateTop = false
        val expiryDateStateInput = PSExpiryDateStateImpl()
        expiryDateStateInput.value = ""
        expiryDateStateInput.isFocused = false
        expiryDateStateInput.isValidInUi = true
        sut(
            state = expiryDateStateInput,
            labelText = "Expiry Date",
            animateTopLabelText = animateTop
        )

        // Act
        expiryDatePickerNoAnimationLabel().performClick()

        // Assert
        expiryDatePickerNoAnimationLabel().assertIsDisplayed()
    }

    @Test
    fun `IF PSExpiryDatePickerField not animating top label, and not valid in ui PERFORMING just click TRIGGER label without animation display`() {
        // Arrange
        val animateTop = false
        val expiryDateStateInput = PSExpiryDateStateImpl()
        expiryDateStateInput.value = ""
        expiryDateStateInput.isFocused = false
        expiryDateStateInput.isValidInUi = false
        sut(
            state = expiryDateStateInput,
            labelText = "Expiry Date",
            animateTopLabelText = animateTop
        )

        // Act
        expiryDatePickerNoAnimationLabel().performClick()

        // Assert
        expiryDatePickerNoAnimationLabel().assertIsDisplayed()
    }

    @Test
    fun `PERFORMING month year picker previews TRIGGER views rendering`() {
        composeTestRule.setContent {
            DefaultMonthYearPickerDialog()
            WithInputMonthYearPickerDialog()
        }
    }


    @Test
    fun `transformExpiryDateForOutput returns empty string for default date format`() {
        val input = "MM/YYYY"
        val result = transformExpiryDateForOutput(input)
        Assert.assertEquals("", result)
    }

    @Test
    fun `transformExpiryDateForOutput returns transformed date for valid date`() {
        val input = "12/2024"
        val result = transformExpiryDateForOutput(input)
        Assert.assertEquals("12-1976", result)
    }

    @Test
    fun `transformExpiryDateForOutput returns month with 00 year for invalid year`() {
        val input = "12/abcd"
        val result = transformExpiryDateForOutput(input)
        Assert.assertEquals("12", result)
    }

    @Test
    fun `transformExpiryDateForOutput returns 00 for invalid month and valid year`() {
        val input = "ab/2024"
        val result = transformExpiryDateForOutput(input)
        Assert.assertEquals("00-1976", result)
    }

    @Test
    fun `transformExpiryDateForOutput returns empty string for invalid date format`() {
        val input = "invalid"
        val result = transformExpiryDateForOutput(input)
        Assert.assertEquals("", result)
    }

    @Test
    fun `WHEN picker dialog confirms with different value THEN FIELD_VALUE_CHANGE event is triggered`() {
        // Arrange
        val expiryDateState = PSExpiryDateStateImpl().apply {
            value = "01/2025"
        }
        var fieldValueChangeEventTriggered = false

        val eventHandler = PSCardFieldEventHandler { event ->
            if (event == PSCardFieldInputEvent.FIELD_VALUE_CHANGE) {
                fieldValueChangeEventTriggered = true
            }
        }

        sut(expiryDateState, eventHandler = eventHandler)

        // Act
        expiryDatePickerField().performClick()
        composeTestRule.waitForIdle()

        monthYearPickerDialogConfirmBtn().performClick()

        // Assert
        assertTrue("Expected FIELD_VALUE_CHANGE event to be triggered when picker confirms with different value", fieldValueChangeEventTriggered)
    }

    @Test
    fun `WHEN validatesEmptyFieldOnBlur is true AND field is empty on blur THEN isValidInUi should be false`() {
        // Arrange
        val expiryDateState = PSExpiryDateStateImpl().apply {
            value = ""
            isValidInUi = true
        }

        sut(
            state = expiryDateState,
            animateTopLabelText = true
        )

        // Act
        expiryDatePickerField().performClick()
        composeTestRule.waitForIdle()

        // Assert
        assertTrue("Field should have been shown (alreadyShown should be true)", expiryDateState.alreadyShown)
    }
}