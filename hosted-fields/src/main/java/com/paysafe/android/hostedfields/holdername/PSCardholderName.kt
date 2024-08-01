/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.holdername

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.paysafe.android.hostedfields.PSTheme
import com.paysafe.android.hostedfields.R
import com.paysafe.android.hostedfields.domain.model.PSCardFieldInputEvent
import com.paysafe.android.hostedfields.domain.model.PSCardholderNameState
import com.paysafe.android.hostedfields.domain.model.PSCardholderNameStateImpl
import com.paysafe.android.hostedfields.model.DefaultPSCardFieldEventHandler
import com.paysafe.android.hostedfields.model.PSCardFieldEventHandler

import com.paysafe.android.hostedfields.provideDefaultPSTheme
import com.paysafe.android.hostedfields.util.CardPreview
import com.paysafe.android.hostedfields.util.PS_CARD_HOLDER_NAME_TEST_TAG
import com.paysafe.android.hostedfields.util.TextLabelWithPSTheme
import com.paysafe.android.hostedfields.util.TextPlaceholderWithPSTheme
import com.paysafe.android.hostedfields.util.keyboardActionFromIme
import com.paysafe.android.hostedfields.util.roundedCornerShapeWithPSTheme
import com.paysafe.android.hostedfields.util.textFieldColorsWithPSTheme
import com.paysafe.android.hostedfields.util.textStyleWithPSTheme
import com.paysafe.android.hostedfields.valid.CardholderNameChecks

//region HOSTED FIELD: Cardholder Name
private fun onHolderNameChange(
    cardholderNameState: PSCardholderNameState,
    eventHandler: PSCardFieldEventHandler,
): (String) -> Unit = {
    val newValue = CardholderNameChecks.inputProtection(it, eventHandler::handleEvent)
    if (cardholderNameState.value != newValue) { // is new value distinct?
        val isValid = CardholderNameChecks.validations(newValue)
        val noInvalidCharacters = newValue == it

        // Security check to avoid double trigger for 'onFieldValueChange'; if 'it' contains an
        // invalid character its value would be different from the one returned by 'inputProtection'
        if (noInvalidCharacters) eventHandler.handleEvent(PSCardFieldInputEvent.FIELD_VALUE_CHANGE)
        eventHandler.handleEvent(if (isValid) PSCardFieldInputEvent.VALID else PSCardFieldInputEvent.INVALID)

        cardholderNameState.isValidInUi = isValid
    }
    cardholderNameState.value = newValue
}

@OptIn(ExperimentalComposeUiApi::class)
private fun onDonePressed(
    holderNameState: PSCardholderNameState,
    keyboardController: SoftwareKeyboardController?
): (KeyboardActionScope.() -> Unit) = {
    keyboardController?.hide()
    holderNameState.isValidInUi = CardholderNameChecks.validations(holderNameState.value)
}


private fun onHolderNameFocusChange(
    focusState: FocusState,
    holderNameState: PSCardholderNameState,
    eventHandler: PSCardFieldEventHandler
) {
    if (focusState.isFocused) eventHandler.handleEvent(PSCardFieldInputEvent.FOCUS)
    val isInactive = !focusState.isFocused
    holderNameState.isFocused = focusState.isFocused
    if (isInactive && holderNameState.alreadyShown) {
        holderNameState.isValidInUi = CardholderNameChecks.validations(holderNameState.value)
    }
    holderNameState.alreadyShown = true
}
@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun PSCardholderName(
    state: PSCardholderNameState,
    modifier: Modifier = Modifier,
    labelText: String,
    placeholderText: String? = null,
    animateTopLabelText: Boolean,
    psTheme: PSTheme,
    eventHandler: PSCardFieldEventHandler,
    onValueChange: (String) -> Unit = onHolderNameChange(state, eventHandler),
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val keyboardImeAction: ImeAction = ImeAction.Done
    val onKeyboardAction: (KeyboardActionScope.() -> Unit) =
        onDonePressed(state, keyboardController)
    OutlinedTextField(
        value = state.value,
        onValueChange = onValueChange,
        label = {
            if (animateTopLabelText) {
                TextLabelWithPSTheme(
                    labelText = labelText,
                    psTheme = psTheme
                )
            }
        },
        placeholder = {
            TextPlaceholderWithPSTheme(
                placeholderText = placeholderText
                    ?: stringResource(id = R.string.card_holder_name_hint),
                psTheme = psTheme
            )
        },
        // Keyboard Settings //
        keyboardOptions = KeyboardOptions(
            imeAction = keyboardImeAction,
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Words
        ),
        keyboardActions = keyboardActionFromIme(keyboardImeAction, onKeyboardAction),
        // Extra //
        singleLine = true,
        isError = !state.isValidInUi,
        modifier = modifier
            .testTag(PS_CARD_HOLDER_NAME_TEST_TAG)
            .onFocusChanged { onHolderNameFocusChange(it, state, eventHandler) },
        colors = textFieldColorsWithPSTheme(psTheme),
        shape = roundedCornerShapeWithPSTheme(psTheme),
        textStyle = textStyleWithPSTheme(psTheme)
    )
}
//endregion

//region Previews
@CardPreview
@Composable
internal fun DefaultPSCardholderName() {
    PreviewPSCardholderName()
}

@CardPreview
@Composable
internal fun InputPSCardholderName() {
    val holderNameState = PSCardholderNameStateImpl()
    holderNameState.value = "John Smith"

    PreviewPSCardholderName(holderNameState)
}

@CardPreview
@Composable
internal fun ErrorPSCardholderName() {
    val holderNameState = PSCardholderNameStateImpl()
    holderNameState.value = "Wrong " // Space at the end
    holderNameState.isValidInUi = false

    PreviewPSCardholderName(holderNameState)
}

@Composable
internal fun PreviewPSCardholderName(
    holderNameState: PSCardholderNameState = PSCardholderNameStateImpl()
) {
    val isValidLiveData = remember { MutableLiveData(false) }
    val eventHandler = DefaultPSCardFieldEventHandler(isValidLiveData)

    PSCardholderName(
        state = holderNameState,
        animateTopLabelText = true,
        psTheme = provideDefaultPSTheme(),
        labelText = "Name on card",
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        eventHandler = eventHandler
    )
}
//endregion