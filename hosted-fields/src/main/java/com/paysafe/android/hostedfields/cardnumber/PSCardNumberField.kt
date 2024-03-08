/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.cardnumber

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import com.paysafe.android.hostedfields.PSTheme
import com.paysafe.android.hostedfields.cvv.PSCvvView
import com.paysafe.android.hostedfields.model.CardNumberSeparator
import com.paysafe.android.hostedfields.model.PSCardFieldInputEvent
import com.paysafe.android.hostedfields.model.PSCardNumberState
import com.paysafe.android.hostedfields.util.PS_CARD_NUMBER_NO_ANIM_LABEL_TEST_TAG
import com.paysafe.android.hostedfields.util.TextLabelReplacement
import com.paysafe.android.hostedfields.util.WrapperToAvoidPaste
import com.paysafe.android.hostedfields.util.avoidCursorHandle
import com.paysafe.android.hostedfields.util.rememberCardNumberState

/**
 * Composable to provide credit card number component for user interface.
 *
 * @param cardNumberState State to store credit card number text.
 * @param cardNumberModifier Compose modifier for [PSCardNumber] & [CardNumberBrandIcon] to decorate or add behavior.
 * @param labelText Helper label shown inside [OutlinedTextField].
 * @param placeholderText Helper placeholder shown inside [OutlinedTextField].
 * @param animateTopLabelText If 'true' it will show the default animation for [OutlinedTextField], otherwise the label will remain in place.
 * @param cardNumberLiveData Live data to store credit card type, bundled with [PSCvvView] and if card number is valid.
 * @param onEvent Callback function that reacts to several [PSCardFieldInputEvent].
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PSCardNumberField(
    cardNumberState: PSCardNumberState = rememberCardNumberState(),
    cardNumberModifier: PSCardNumberModifier,
    labelText: String,
    placeholderText: String?,
    animateTopLabelText: Boolean,
    cardNumberLiveData: PSCardNumberLiveData,
    psTheme: PSTheme,
    separator: CardNumberSeparator,
    onEvent: ((PSCardFieldInputEvent) -> Unit)? = null
) {
    CompositionLocalProvider(
        LocalTextToolbar provides WrapperToAvoidPaste,
        LocalTextSelectionColors provides avoidCursorHandle
    ) {
        Box(
            modifier = Modifier.semantics { testTagsAsResourceId = true }
        ) {
            PSCardNumber(
                state = cardNumberState,
                cardNumberModifier = cardNumberModifier,
                labelText = labelText,
                placeholderText = placeholderText,
                animateTopLabelText = animateTopLabelText,
                cardNumberLiveData = cardNumberLiveData,
                psTheme = psTheme,
                separator = separator,
                onEvent = onEvent
            )
            if (cardNumberState.showLabelWithoutAnimation(animateTopLabelText, labelText)) {
                TextLabelReplacement(
                    labelText = labelText,
                    isValidInUI = cardNumberState.isValidInUi,
                    psTheme = psTheme,
                    modifier = Modifier
                        .testTag(PS_CARD_NUMBER_NO_ANIM_LABEL_TEST_TAG)
                        .align(Alignment.Center)
                )
            }
        }
    }
}