/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.cardnumber

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.MutableLiveData
import com.paysafe.android.hostedfields.domain.model.CardNumberSeparator
import com.paysafe.android.hostedfields.domain.model.PSCardNumberState
import com.paysafe.android.hostedfields.domain.model.PSCardNumberStateImpl

import com.paysafe.android.hostedfields.model.DefaultPSCardFieldEventHandler
import com.paysafe.android.hostedfields.model.PSCardFieldEventHandler

import com.paysafe.android.hostedfields.provideDefaultPSTheme
import com.paysafe.android.hostedfields.util.PS_CARD_NUMBER_NO_ANIM_LABEL_TEST_TAG
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PSCardNumberFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun cardNumberNoAnimationLabel() = composeTestRule.onNodeWithTag(
        PS_CARD_NUMBER_NO_ANIM_LABEL_TEST_TAG
    )

    private fun sut(
        state: PSCardNumberState = PSCardNumberStateImpl(),
        animateTopLabelText: Boolean,
        numbersSeparator: CardNumberSeparator = CardNumberSeparator.DASH,
        eventHandler: PSCardFieldEventHandler = DefaultPSCardFieldEventHandler(MutableLiveData(false))
    ) {
        composeTestRule.setContent {
            PSCardNumberField(
                cardNumberState = state,
                cardNumberModifier = PSCardNumberModifier(
                    modifier = Modifier,
                    cardBrandModifier = Modifier
                ),
                labelText = "Card Number",
                placeholderText = "XXXX XXXX XXXX XXXX",
                animateTopLabelText = animateTopLabelText,
                cardNumberLiveData = PSCardNumberLiveData(
                    cardTypeLiveData = MutableLiveData(PSCreditCardType.UNKNOWN),
                    isValidLiveData = MutableLiveData(false)
                ),
                psTheme = provideDefaultPSTheme(),
                separator = numbersSeparator,
                eventHandler = eventHandler
            )
        }
    }

    @Test
    fun `IF PSCardNumberField not animating top label PERFORMING just click TRIGGER label without animation display`() {
        // Arrange
        val animateTop = false
        val cardNumberStateInput = PSCardNumberStateImpl()
        cardNumberStateInput.value = ""
        cardNumberStateInput.isFocused = false
        cardNumberStateInput.isValidInUi = true
        sut(state = cardNumberStateInput, animateTopLabelText = animateTop)

        // Act
        cardNumberNoAnimationLabel().performClick()

        // Assert
        cardNumberNoAnimationLabel().assertIsDisplayed()
    }

    @Test
    fun `IF PSCardNumberField not animating top label, and not valid in ui PERFORMING just click TRIGGER label without animation display`() {
        // Arrange
        val animateTop = false
        val cardNumberStateInput = PSCardNumberStateImpl()
        cardNumberStateInput.value = ""
        cardNumberStateInput.isFocused = false
        cardNumberStateInput.isValidInUi = false
        sut(state = cardNumberStateInput, animateTopLabelText = animateTop)

        // Act
        cardNumberNoAnimationLabel().performClick()

        // Assert
        cardNumberNoAnimationLabel().assertIsDisplayed()
    }

}