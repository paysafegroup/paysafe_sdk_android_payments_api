/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.cvv

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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.paysafe.android.hostedfields.PSTheme
import com.paysafe.android.hostedfields.R
import com.paysafe.android.hostedfields.model.PSCardFieldInputEvent
import com.paysafe.android.hostedfields.model.PSCvvState
import com.paysafe.android.hostedfields.model.PSCvvStateImpl
import com.paysafe.android.hostedfields.provideDefaultPSTheme
import com.paysafe.android.hostedfields.util.CardPreview
import com.paysafe.android.hostedfields.util.PS_CVV_TEST_TAG
import com.paysafe.android.hostedfields.util.TextLabelWithPSTheme
import com.paysafe.android.hostedfields.util.TextPlaceholderWithPSTheme
import com.paysafe.android.hostedfields.util.keyboardActionFromIme
import com.paysafe.android.hostedfields.util.roundedCornerShapeWithPSTheme
import com.paysafe.android.hostedfields.util.textFieldColorsWithPSTheme
import com.paysafe.android.hostedfields.util.textStyleWithPSTheme
import com.paysafe.android.hostedfields.valid.CvvChecks

//region HOSTED FIELD: Cvv
private fun onCvvChange(
    cvvState: PSCvvState,
    isValidLiveData: MutableLiveData<Boolean>,
    onEvent: ((PSCardFieldInputEvent) -> Unit)? = null
): (String) -> Unit = {
    val newValue = CvvChecks.inputProtection(it, cvvState.cardType, onEvent)
    if (cvvState.value != newValue) { // is new value distinct?
        val isValid = CvvChecks.validations(newValue, cvvState.cardType)
        val noInvalidCharacters = newValue == it

        // Security check to avoid double trigger for 'onFieldValueChange'; if 'it' contains an
        // invalid character its value would be different from the one returned by 'inputProtection'
        if (noInvalidCharacters) onEvent?.invoke(PSCardFieldInputEvent.FIELD_VALUE_CHANGE)
        onEvent?.invoke(if (isValid) PSCardFieldInputEvent.VALID else PSCardFieldInputEvent.INVALID)

        isValidLiveData.postValue(isValid)
    }
    cvvState.value = newValue
}

@OptIn(ExperimentalComposeUiApi::class)
private fun onDonePressed(
    cvvState: PSCvvState,
    keyboardController: SoftwareKeyboardController?
): (KeyboardActionScope.() -> Unit) = {
    keyboardController?.hide()
    cvvState.isValidInUi = CvvChecks.validations(cvvState.value, cvvState.cardType)
}

private fun onCvvFocusChange(
    focusState: FocusState,
    cvvState: PSCvvState,
    onEvent: ((PSCardFieldInputEvent) -> Unit)? = null
) {
    if (focusState.isFocused) onEvent?.invoke(PSCardFieldInputEvent.FOCUS)
    val isInactive = !focusState.isFocused
    if (isInactive && cvvState.alreadyShown) {
        cvvState.isValidInUi = CvvChecks.validations(cvvState.value, cvvState.cardType)
    }
    cvvState.alreadyShown = true
}

private fun provideVisualTransformation(isMasked: Boolean): VisualTransformation =
    if (isMasked)
        PasswordVisualTransformation()
    else
        VisualTransformation.None

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun PSCvv(
    state: PSCvvState,
    modifier: Modifier = Modifier,
    labelText: String? = null,
    placeholderText: String? = null,
    isValidLiveData: MutableLiveData<Boolean>,
    psTheme: PSTheme,
    isMasked: Boolean,
    onEvent: ((PSCardFieldInputEvent) -> Unit)? = null,
) {
    val onValueChange: (String) -> Unit = onCvvChange(state, isValidLiveData, onEvent)
    val keyboardController = LocalSoftwareKeyboardController.current
    val keyboardImeAction: ImeAction = ImeAction.Done
    val onKeyboardAction: (KeyboardActionScope.() -> Unit) =
        onDonePressed(state, keyboardController)
    OutlinedTextField(
        value = state.value,
        onValueChange = onValueChange,
        label = {
            TextLabelWithPSTheme(
                labelText = labelText
                    ?: stringResource(id = R.string.card_cvv_placeholder),
                psTheme = psTheme
            )
        },
        placeholder = {
            TextPlaceholderWithPSTheme(
                placeholderText = placeholderText
                    ?: stringResource(id = R.string.card_cvv_hint),
                psTheme = psTheme
            )
        },
        visualTransformation = provideVisualTransformation(isMasked),
        // Keyboard Settings //
        keyboardOptions = KeyboardOptions(
            imeAction = keyboardImeAction, keyboardType = KeyboardType.Number
        ),
        keyboardActions = keyboardActionFromIme(keyboardImeAction, onKeyboardAction),
        // Extra //
        singleLine = true,
        isError = !state.isValidInUi,
        modifier = modifier
            .testTag(PS_CVV_TEST_TAG)
            .onFocusChanged { onCvvFocusChange(it, state, onEvent) },
        colors = textFieldColorsWithPSTheme(psTheme),
        shape = roundedCornerShapeWithPSTheme(psTheme),
        textStyle = textStyleWithPSTheme(psTheme)
    )
}
//endregion

//region Previews
@CardPreview
@Composable
internal fun Default() {
    PreviewPSCvv()
}

@CardPreview
@Composable
internal fun Input() {
    val cvvState = PSCvvStateImpl()
    cvvState.value = "123"

    PreviewPSCvv(cvvState)
}

@CardPreview
@Composable
internal fun InputMasked() {
    val cvvState = PSCvvStateImpl()
    cvvState.value = "123"

    PreviewPSCvv(
        cvvState = cvvState,
        isMasked = true
    )
}

@CardPreview
@Composable
internal fun Error() {
    val cvvState = PSCvvStateImpl()
    cvvState.value = "88"
    cvvState.isValidInUi = false

    PreviewPSCvv(cvvState)
}

@CardPreview
@Composable
internal fun ErrorMasked() {
    val cvvState = PSCvvStateImpl()
    cvvState.value = "88"
    cvvState.isValidInUi = false

    PreviewPSCvv(
        cvvState = cvvState,
        isMasked = true
    )
}

@Composable
internal fun PreviewPSCvv(
    cvvState: PSCvvState = PSCvvStateImpl(),
    isMasked: Boolean = false
) {
    PSCvv(
        state = cvvState,
        isValidLiveData = MutableLiveData(false),
        psTheme = provideDefaultPSTheme(),
        isMasked = isMasked,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    )
}
//endregion