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
import com.paysafe.android.hostedfields.provideDefaultPSTheme
import com.paysafe.android.hostedfields.util.PS_EXPIRY_DATE_TEXT_NO_ANIM_LABEL_TEST_TAG
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PSExpiryDateTextFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun expiryDateTextNoAnimationLabel() = composeTestRule.onNodeWithTag(
        PS_EXPIRY_DATE_TEXT_NO_ANIM_LABEL_TEST_TAG
    )

    private fun sut(
        state: PSExpiryDateState = PSExpiryDateStateImpl(),
        animateTopLabelText: Boolean,
        onEvent: ((PSCardFieldInputEvent) -> Unit)? = null
    ) {
        composeTestRule.setContent {
            PSExpiryDateTextField(
                expiryDateState = state,
                modifier = Modifier.fillMaxWidth(),
                animateTopLabelText = animateTopLabelText,
                labelText = "Expiry Date",
                placeholderText = "MM   YY",
                isValidLiveData = MutableLiveData(false),
                psTheme = provideDefaultPSTheme(),
                onEvent = onEvent
            )
        }
    }

    @Test
    fun `IF PSExpiryDateTextField not animating top label PERFORMING just click TRIGGER label without animation display`() {
        // Arrange
        val animateTop = false
        val expiryDateStateInput = PSExpiryDateStateImpl()
        expiryDateStateInput.value = ""
        expiryDateStateInput.isFocused = false
        expiryDateStateInput.isValidInUi = true
        sut(state = expiryDateStateInput, animateTopLabelText = animateTop)

        // Act
        expiryDateTextNoAnimationLabel().performClick()

        // Assert
        expiryDateTextNoAnimationLabel().assertIsDisplayed()
    }

    @Test
    fun `IF PSExpiryDateTextField not animating top label, and not valid in ui PERFORMING just click TRIGGER label without animation display`() {
        // Arrange
        val animateTop = false
        val expiryDateStateInput = PSExpiryDateStateImpl()
        expiryDateStateInput.value = ""
        expiryDateStateInput.isFocused = false
        expiryDateStateInput.isValidInUi = false
        sut(state = expiryDateStateInput, animateTopLabelText = animateTop)

        // Act
        expiryDateTextNoAnimationLabel().performClick()

        // Assert
        expiryDateTextNoAnimationLabel().assertIsDisplayed()
    }

}