/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.expirydate

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.paysafe.android.hostedfields.PSTheme
import com.paysafe.android.hostedfields.R
import com.paysafe.android.hostedfields.domain.model.PSCardFieldInputEvent
import com.paysafe.android.hostedfields.domain.model.PSExpiryDateState
import com.paysafe.android.hostedfields.domain.model.PSExpiryDateStateImpl
import com.paysafe.android.hostedfields.model.DefaultPSCardFieldEventHandler
import com.paysafe.android.hostedfields.model.PSCardFieldEventHandler
import com.paysafe.android.hostedfields.provideDefaultPSTheme
import com.paysafe.android.hostedfields.util.CardPreview
import com.paysafe.android.hostedfields.util.PS_EXPIRY_DATE_TEXT_TEST_TAG
import com.paysafe.android.hostedfields.util.TextLabelWithPSTheme
import com.paysafe.android.hostedfields.util.TextPlaceholderWithPSTheme
import com.paysafe.android.hostedfields.util.expiryDateVisualTransformation
import com.paysafe.android.hostedfields.util.keyboardActionFromIme
import com.paysafe.android.hostedfields.util.roundedCornerShapeWithPSTheme
import com.paysafe.android.hostedfields.util.textFieldColorsWithPSTheme
import com.paysafe.android.hostedfields.util.textStyleWithPSTheme
import com.paysafe.android.hostedfields.util.uniformFieldBorder
import com.paysafe.android.hostedfields.valid.ExpiryDateChecks

//region HOSTED FIELD: Expiry Date
private fun onExpiryDateChange(
    expiryDateState: PSExpiryDateState,
    isValidLiveData: MutableLiveData<Boolean>,
    eventHandler: PSCardFieldEventHandler,
    clearsErrorOnInput: Boolean = false
): (String) -> Unit = {
    val newValue = ExpiryDateChecks.inputProtection(it, eventHandler::handleEvent)
    if (expiryDateState.value != newValue) { // is new value distinct?
        val isValid = ExpiryDateChecks.validations(newValue)
        val noInvalidCharacters = newValue == it

        // Security check to avoid double trigger for 'onFieldValueChange'; if 'it' contains an
        // invalid character its value would be different from the one returned by 'inputProtection'
        if (noInvalidCharacters) eventHandler.handleEvent(PSCardFieldInputEvent.FIELD_VALUE_CHANGE)
        eventHandler.handleEvent(if (isValid) PSCardFieldInputEvent.VALID else PSCardFieldInputEvent.INVALID)

        if (clearsErrorOnInput) {
            expiryDateState.isValidInUi = true
        }

        isValidLiveData.postValue(isValid)
    }
    expiryDateState.value = newValue
}

private fun onDonePressed(
    expiryDateState: PSExpiryDateState,
    focusManager: FocusManager,
    validatesEmptyFieldOnBlur: Boolean = true
): (KeyboardActionScope.() -> Unit) = {
    focusManager.clearFocus()
    expiryDateState.isValidInUi = if (!validatesEmptyFieldOnBlur && expiryDateState.value.isEmpty()) {
        true
    } else {
        ExpiryDateChecks.validations(expiryDateState.value)
    }
}

private fun onExpiryDateFocusChange(
    focusState: FocusState,
    expiryDateState: PSExpiryDateState,
    eventHandler: PSCardFieldEventHandler,
    validatesEmptyFieldOnBlur: Boolean = true
) {
    if (focusState.isFocused) {
        eventHandler.handleEvent(PSCardFieldInputEvent.FOCUS)
    } else {
        eventHandler.handleEvent(PSCardFieldInputEvent.BLUR)
    }
    val isInactive = !focusState.isFocused
    expiryDateState.isFocused = focusState.isFocused
    if (isInactive && expiryDateState.alreadyShown) {
        expiryDateState.isValidInUi = if (!validatesEmptyFieldOnBlur && expiryDateState.value.isEmpty()) {
            true
        } else {
            ExpiryDateChecks.validations(expiryDateState.value)
        }
    }
    expiryDateState.alreadyShown = true
}

@Composable
@JvmSynthetic
fun PSExpiryDateText(
    state: PSExpiryDateState,
    modifier: Modifier = Modifier,
    labelText: String,
    placeholderText: String? = null,
    animateTopLabelText: Boolean,
    isValidLiveData: MutableLiveData<Boolean>,
    psTheme: PSTheme,
    eventHandler: PSCardFieldEventHandler,
    clearsErrorOnInput: Boolean = false,
    validatesEmptyFieldOnBlur: Boolean = true
) {
    val onValueChange: (String) -> Unit = onExpiryDateChange(state, isValidLiveData, eventHandler, clearsErrorOnInput)
    val focusManager = LocalFocusManager.current
    val keyboardImeAction: ImeAction = ImeAction.Done
    val onKeyboardAction: (KeyboardActionScope.() -> Unit) =
        onDonePressed(state, focusManager, validatesEmptyFieldOnBlur)
    val shape = roundedCornerShapeWithPSTheme(psTheme)
    OutlinedTextField(
        value = state.value,
        onValueChange = onValueChange,
        // Texts //
        label = if (animateTopLabelText) {
            {
                TextLabelWithPSTheme(
                    labelText = labelText,
                    psTheme = psTheme
                )
            }
        } else null,
        placeholder = {
            TextPlaceholderWithPSTheme(
                placeholderText = placeholderText
                    ?: stringResource(id = R.string.card_expiry_date_hint),
                psTheme = psTheme
            )
        },
        // Keyboard Settings //
        keyboardOptions = KeyboardOptions(
            imeAction = keyboardImeAction, keyboardType = KeyboardType.Number
        ),
        keyboardActions = keyboardActionFromIme(keyboardImeAction, onKeyboardAction),
        // Optical //
        visualTransformation = expiryDateVisualTransformation(),
        // Extra //
        singleLine = true,
        isError = !state.isValidInUi,
        modifier = modifier
            .testTag(PS_EXPIRY_DATE_TEXT_TEST_TAG)
            .onFocusChanged { onExpiryDateFocusChange(it, state, eventHandler, validatesEmptyFieldOnBlur) }
            .uniformFieldBorder(state.isFocused, !state.isValidInUi, psTheme, shape),
        colors = textFieldColorsWithPSTheme(psTheme),
        shape = shape,
        textStyle = textStyleWithPSTheme(psTheme)
    )
}
//endregion

//region Previews
@CardPreview
@Composable
internal fun DefaultExpiryDateText() {
    PreviewPSExpiryDateText()
}

@CardPreview
@Composable
internal fun InputExpiryDateText() {
    val expiryDateState = PSExpiryDateStateImpl()
    expiryDateState.value = "1227"

    PreviewPSExpiryDateText(expiryDateState)
}

@CardPreview
@Composable
internal fun ErrorExpiryDateText() {
    val expiryDateState = PSExpiryDateStateImpl()
    expiryDateState.value = "9876"
    expiryDateState.isValidInUi = false

    PreviewPSExpiryDateText(expiryDateState)
}

@Composable
internal fun PreviewPSExpiryDateText(
    expiryDateState: PSExpiryDateState = PSExpiryDateStateImpl()
) {
    val isValidLiveData = MutableLiveData(false)
    val eventHandler = DefaultPSCardFieldEventHandler(isValidLiveData)

    PSExpiryDateText(
        state = expiryDateState,
        animateTopLabelText = true,
        labelText = "Expiry date",
        isValidLiveData = isValidLiveData,
        psTheme = provideDefaultPSTheme(),
        eventHandler = eventHandler,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    )
}
//endregion