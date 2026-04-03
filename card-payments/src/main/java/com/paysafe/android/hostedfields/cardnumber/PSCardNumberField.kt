/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.cardnumber

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
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
import com.paysafe.android.hostedfields.domain.model.PSCardNumberState

import com.paysafe.android.hostedfields.model.PSCardFieldEventHandler

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
 * @param textOptions Configuration for text display settings (label, placeholder, animation).
 * @param cardNumberLiveData Live data to store credit card type, bundled with [PSCvvView] and if card number is valid.
 * @param psTheme Theme configuration for the field.
 * @param fieldOptions Configuration for UI and validation settings.
 * @param eventHandler Handler for field events.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PSCardNumberField(
    cardNumberState: PSCardNumberState = rememberCardNumberState(),
    cardNumberModifier: PSCardNumberModifier,
    textOptions: PSCardNumberFieldTextOptions,
    cardNumberLiveData: PSCardNumberLiveData,
    psTheme: PSTheme,
    fieldOptions: PSCardNumberFieldOptions = PSCardNumberFieldOptions(),
    eventHandler: PSCardFieldEventHandler
) {
    CompositionLocalProvider(
        LocalTextToolbar provides WrapperToAvoidPaste,
        LocalTextSelectionColors provides avoidCursorHandle
    ) {
        Box(
            modifier = Modifier.semantics { testTagsAsResourceId = true }
        ) {
            PSCardNumber(
                fieldConfig = PSCardNumberFieldConfig(
                    state = cardNumberState,
                    modifier = cardNumberModifier
                ),
                textConfig = PSCardNumberTextConfig(
                    labelText = textOptions.labelText,
                    placeholderText = textOptions.placeholderText,
                    animateTopLabelText = textOptions.animateTopLabelText
                ),
                cardNumberLiveData = cardNumberLiveData,
                psTheme = psTheme,
                uiConfig = PSCardNumberUIConfig(
                    separator = fieldOptions.separator,
                    showBrandIcon = fieldOptions.showBrandIcon,
                    compactFieldHeight = fieldOptions.compactFieldHeight
                ),
                validationConfig = PSCardNumberValidationConfig(
                    clearsErrorOnInput = fieldOptions.clearsErrorOnInput,
                    validatesEmptyFieldOnBlur = fieldOptions.validatesEmptyFieldOnBlur
                ),
                eventHandler = eventHandler
            )
            if (cardNumberState.showLabelWithoutAnimation(textOptions.animateTopLabelText, textOptions.labelText)) {
                TextLabelReplacement(
                    labelText = textOptions.labelText,
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