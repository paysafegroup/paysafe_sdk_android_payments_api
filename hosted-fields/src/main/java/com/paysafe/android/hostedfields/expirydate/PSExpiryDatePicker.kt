/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.expirydate

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paysafe.android.hostedfields.PSTheme
import com.paysafe.android.hostedfields.R
import com.paysafe.android.hostedfields.domain.model.PSExpiryDateState
import com.paysafe.android.hostedfields.domain.model.PSExpiryDateStateImpl
import com.paysafe.android.hostedfields.provideDefaultPSTheme
import com.paysafe.android.hostedfields.util.CardPreview
import com.paysafe.android.hostedfields.util.PS_EXPIRY_DATE_PICKER_TEST_TAG
import com.paysafe.android.hostedfields.util.TextLabelWithPSTheme
import com.paysafe.android.hostedfields.util.TextPlaceholderWithPSTheme
import com.paysafe.android.hostedfields.util.expiryDateVisualTransformation
import com.paysafe.android.hostedfields.util.roundedCornerShapeWithPSTheme
import com.paysafe.android.hostedfields.util.textFieldColorsWithPSTheme
import com.paysafe.android.hostedfields.util.textStyleWithPSTheme
import com.paysafe.android.hostedfields.valid.ExpiryDateChecks

//region HOSTED FIELD: Expiry Date
fun onExpiryDateFocusChange(
    focusState: FocusState,
    expiryDateState: PSExpiryDateState,
) {
    val isInactive = !focusState.isFocused
    expiryDateState.isFocused = focusState.isFocused
    if (isInactive && expiryDateState.alreadyShown) {
        expiryDateState.isValidInUi = ExpiryDateChecks.validations(expiryDateState.value)
    }
    expiryDateState.alreadyShown = true
}

@Composable
internal fun EmptyViewJustToClearFocus(modifier: Modifier = Modifier) {
    Box(modifier = modifier.then(Modifier.focusable(true))) {}
}

@Composable
@JvmSynthetic
fun PSExpiryDatePicker(
    state: PSExpiryDateState,
    labelText: String,
    placeholderText: String? = null,
    animateTopLabelText: Boolean,
    modifier: Modifier = Modifier,
    psTheme: PSTheme
) {
    OutlinedTextField(
        value = state.value,
        onValueChange = {},
        // Texts //
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
                    ?: stringResource(id = R.string.card_expiry_date_hint),
                psTheme = psTheme
            )
        },
        // Optical //
        visualTransformation = expiryDateVisualTransformation(),
        colors = textFieldColorsWithPSTheme(psTheme, state.isValidInUi),
        shape = roundedCornerShapeWithPSTheme(psTheme),
        textStyle = textStyleWithPSTheme(psTheme),
        // Extra //
        enabled = false,
        readOnly = true,
        singleLine = true,
        isError = !state.isValidInUi,
        modifier = modifier
            .testTag(PS_EXPIRY_DATE_PICKER_TEST_TAG)
            .onFocusChanged { onExpiryDateFocusChange(it, state) }
    )
}
//endregion

//region Previews
@CardPreview
@Composable
internal fun DefaultExpiryDatePicker() {
    PreviewPSExpiryDatePicker()
}

@CardPreview
@Composable
internal fun InputExpiryDatePicker() {
    val expiryDateState = PSExpiryDateStateImpl()
    expiryDateState.value = "1030"

    PreviewPSExpiryDatePicker(expiryDateState)
}

@CardPreview
@Composable
internal fun ErrorExpiryDatePicker() {
    val expiryDateState = PSExpiryDateStateImpl()
    expiryDateState.value = "11"
    expiryDateState.isValidInUi = false

    PreviewPSExpiryDatePicker(expiryDateState)
}

@Composable
internal fun PreviewPSExpiryDatePicker(
    expiryDateState: PSExpiryDateState = PSExpiryDateStateImpl()
) {
    PSExpiryDatePicker(
        state = expiryDateState,
        animateTopLabelText = true,
        labelText = "Expiry date",
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        psTheme = provideDefaultPSTheme()
    )
}
//endregion