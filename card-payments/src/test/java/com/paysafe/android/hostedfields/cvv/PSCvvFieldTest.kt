/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.cvv

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.MutableLiveData
import com.paysafe.android.hostedfields.domain.model.PSCvvState
import com.paysafe.android.hostedfields.domain.model.PSCvvStateImpl
import com.paysafe.android.hostedfields.model.DefaultPSCardFieldEventHandler
import com.paysafe.android.hostedfields.model.PSCardFieldEventHandler

import com.paysafe.android.hostedfields.provideDefaultPSTheme
import com.paysafe.android.hostedfields.util.PS_CVV_NO_ANIM_LABEL_TEST_TAG
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PSCvvFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun cvvNoAnimationLabel() = composeTestRule.onNodeWithTag(PS_CVV_NO_ANIM_LABEL_TEST_TAG)

    private fun sut(
        state: PSCvvState = PSCvvStateImpl(),
        animateTopLabelText: Boolean,
        eventHandler: PSCardFieldEventHandler = DefaultPSCardFieldEventHandler(MutableLiveData(false))
    ) {
        composeTestRule.setContent {
            PSCvvField(
                cvvState = state,
                modifier = Modifier.fillMaxWidth(),
                labelText = "CVV Number",
                placeholderText = "",
                animateTopLabelText = animateTopLabelText,
                isValidLiveData = MutableLiveData(false),
                psTheme = provideDefaultPSTheme(),
                eventHandler = eventHandler,
                isMasked = false
            )
        }
    }

    @Test
    fun `IF PSCvvField not animating top label PERFORMING just click TRIGGER label without animation display`() {
        // Arrange
        val animateTop = false
        val cvvStateInput = PSCvvStateImpl()
        cvvStateInput.value = ""
        cvvStateInput.isFocused = false
        cvvStateInput.isValidInUi = true
        sut(state = cvvStateInput, animateTopLabelText = animateTop)

        // Act
        cvvNoAnimationLabel().performClick()

        // Assert
        cvvNoAnimationLabel().assertIsDisplayed()
    }

    @Test
    fun `IF PSCvvField not animating top label, and not valid in ui PERFORMING just click TRIGGER label without animation display`() {
        // Arrange
        val animateTop = false
        val cvvStateInput = PSCvvStateImpl()
        cvvStateInput.value = ""
        cvvStateInput.isFocused = false
        cvvStateInput.isValidInUi = false
        sut(state = cvvStateInput, animateTopLabelText = animateTop)

        // Act
        cvvNoAnimationLabel().performClick()

        // Assert
        cvvNoAnimationLabel().assertIsDisplayed()
    }

}