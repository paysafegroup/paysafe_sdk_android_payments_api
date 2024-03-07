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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.paysafe.android.hostedfields.PSTheme
import com.paysafe.android.hostedfields.R
import com.paysafe.android.hostedfields.model.PSCardFieldInputEvent
import com.paysafe.android.hostedfields.model.PSExpiryDateState
import com.paysafe.android.hostedfields.model.PSExpiryDateStateImpl
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
import com.paysafe.android.hostedfields.valid.ExpiryDateChecks

//region HOSTED FIELD: Expiry Date
private fun onExpiryDateChange(
    expiryDateState: PSExpiryDateState,
    isValidLiveData: MutableLiveData<Boolean>,
    onEvent: ((PSCardFieldInputEvent) -> Unit)? = null
): (String) -> Unit = {
    val newValue = ExpiryDateChecks.inputProtection(it, onEvent)
    if (expiryDateState.value != newValue) { // is new value distinct?
        val isValid = ExpiryDateChecks.validations(newValue)
        val noInvalidCharacters = newValue == it

        // Security check to avoid double trigger for 'onFieldValueChange'; if 'it' contains an
        // invalid character its value would be different from the one returned by 'inputProtection'
        if (noInvalidCharacters) onEvent?.invoke(PSCardFieldInputEvent.FIELD_VALUE_CHANGE)
        onEvent?.invoke(if (isValid) PSCardFieldInputEvent.VALID else PSCardFieldInputEvent.INVALID)

        isValidLiveData.postValue(isValid)
    }
    expiryDateState.value = newValue
}

@OptIn(ExperimentalComposeUiApi::class)
private fun onDonePressed(
    expiryDateState: PSExpiryDateState,
    keyboardController: SoftwareKeyboardController?
): (KeyboardActionScope.() -> Unit) = {
    keyboardController?.hide()
    expiryDateState.isValidInUi = ExpiryDateChecks.validations(expiryDateState.value)
}

private fun onExpiryDateFocusChange(
    focusState: FocusState,
    expiryDateState: PSExpiryDateState,
    onEvent: ((PSCardFieldInputEvent) -> Unit)? = null
) {
    if (focusState.isFocused) onEvent?.invoke(PSCardFieldInputEvent.FOCUS)
    val isInactive = !focusState.isFocused
    if (isInactive && expiryDateState.alreadyShown) {
        expiryDateState.isValidInUi = ExpiryDateChecks.validations(expiryDateState.value)
    }
    expiryDateState.alreadyShown = true
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@JvmSynthetic
fun PSExpiryDateText(
    state: PSExpiryDateState,
    modifier: Modifier = Modifier,
    labelText: String? = null,
    placeholderText: String? = null,
    isValidLiveData: MutableLiveData<Boolean>,
    psTheme: PSTheme,
    onEvent: ((PSCardFieldInputEvent) -> Unit)? = null
) {
    val onValueChange: (String) -> Unit = onExpiryDateChange(state, isValidLiveData, onEvent)
    val keyboardController = LocalSoftwareKeyboardController.current
    val keyboardImeAction: ImeAction = ImeAction.Done
    val onKeyboardAction: (KeyboardActionScope.() -> Unit) =
        onDonePressed(state, keyboardController)
    OutlinedTextField(
        value = state.value,
        onValueChange = onValueChange,
        // Texts //
        label = {
            TextLabelWithPSTheme(
                labelText = labelText
                    ?: stringResource(id = R.string.card_expiry_date_placeholder),
                psTheme = psTheme
            )
        },
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
            .onFocusChanged { onExpiryDateFocusChange(it, state, onEvent) },
        colors = textFieldColorsWithPSTheme(psTheme),
        shape = roundedCornerShapeWithPSTheme(psTheme),
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
    PSExpiryDateText(
        state = expiryDateState,
        isValidLiveData = MutableLiveData(false),
        psTheme = provideDefaultPSTheme(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    )
}
//endregion