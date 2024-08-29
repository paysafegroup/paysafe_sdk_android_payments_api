/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.holdername

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.MutableLiveData
import com.paysafe.android.hostedfields.domain.model.PSCardholderNameState
import com.paysafe.android.hostedfields.domain.model.PSCardholderNameStateImpl
import com.paysafe.android.hostedfields.model.DefaultPSCardFieldEventHandler

import com.paysafe.android.hostedfields.provideDefaultPSTheme
import com.paysafe.android.hostedfields.util.PS_CARD_HOLDER_NAME_NO_ANIM_LABEL_TEST_TAG
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PSCardholderNameFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun holderNameNoAnimationLabel() = composeTestRule.onNodeWithTag(
        PS_CARD_HOLDER_NAME_NO_ANIM_LABEL_TEST_TAG
    )

    private fun sut(
        state: PSCardholderNameState = PSCardholderNameStateImpl(),
        animateTopLabelText: Boolean
    ) {
        composeTestRule.setContent {
            val isValidLiveData = MutableLiveData(false)
            val eventHandler = DefaultPSCardFieldEventHandler(isValidLiveData)

            PSCardholderNameField(
                holderNameState = state,
                modifier = Modifier.fillMaxWidth(),
                labelText = "Name on card",
                placeholderText = "",
                animateTopLabelText = animateTopLabelText,
                isValidLiveData = isValidLiveData,
                psTheme = provideDefaultPSTheme(),
                eventHandler = eventHandler
            )
        }
    }

    @Test
    fun `IF PSCardholderNameField not animating top label PERFORMING just click TRIGGER label without animation display`() {
        // Arrange
        val animateTop = false
        val holderNameStateInput = PSCardholderNameStateImpl().apply {
            value = ""
            isFocused = false
            isValidInUi = true
        }
        sut(state = holderNameStateInput, animateTopLabelText = animateTop)

        // Act
        holderNameNoAnimationLabel().performClick()

        // Assert
        holderNameNoAnimationLabel().assertIsDisplayed()
    }

    @Test
    fun `IF PSCardholderNameField not animating top label, and not valid in ui PERFORMING just click TRIGGER label without animation display`() {
        // Arrange
        val animateTop = false
        val holderNameStateInput = PSCardholderNameStateImpl().apply {
            value = ""
            isFocused = false
            isValidInUi = false
        }
        sut(state = holderNameStateInput, animateTopLabelText = animateTop)

        // Act
        holderNameNoAnimationLabel().performClick()

        // Assert
        holderNameNoAnimationLabel().assertIsDisplayed()
    }
}
