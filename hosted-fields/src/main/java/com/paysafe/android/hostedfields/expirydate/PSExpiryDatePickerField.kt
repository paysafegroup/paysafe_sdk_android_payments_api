/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.expirydate

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.paysafe.android.hostedfields.PSTheme
import com.paysafe.android.hostedfields.R
import com.paysafe.android.hostedfields.domain.model.PSCardFieldInputEvent
import com.paysafe.android.hostedfields.domain.model.PSExpiryDateState
import com.paysafe.android.hostedfields.util.PS_EXPIRY_DATE_PICKER_NO_ANIM_LABEL_TEST_TAG
import com.paysafe.android.hostedfields.util.TextLabelReplacement
import com.paysafe.android.hostedfields.util.WrapperToAvoidPaste
import com.paysafe.android.hostedfields.util.avoidCursorHandle
import com.paysafe.android.hostedfields.util.rememberExpiryDateState
import com.paysafe.android.hostedfields.valid.ExpiryDateChecks

/**
 * Composable to provide expiry date picker(month/year) component for user interface.
 *
 * @param expiryDateState State to store expiry date text.
 * @param modifier Compose modifier for [PSExpiryDatePicker] to decorate or add behavior.
 * @param labelText Helper label shown inside [OutlinedTextField].
 * @param placeholderText Helper placeholder shown inside [OutlinedTextField].
 * @param animateTopLabelText If 'true' it will show the default animation for [OutlinedTextField], otherwise the label will remain in place.
 * @param isValidLiveData [LiveData] that stores if expiration date is valid.
 * @param onEvent Callback function that reacts to several [PSCardFieldInputEvent].
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PSExpiryDatePickerField(
    expiryDateState: PSExpiryDateState = rememberExpiryDateState(),
    modifier: Modifier,
    labelText: String,
    placeholderText: String?,
    animateTopLabelText: Boolean,
    isValidLiveData: MutableLiveData<Boolean>,
    psTheme: PSTheme,
    onEvent: ((PSCardFieldInputEvent) -> Unit)? = null
) {
    val requesterToClearFocus = remember { FocusRequester() }
    var alreadyHasFocus by rememberSaveable { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val pickerTitleText: String = stringResource(id = R.string.expiry_date_dialog_title)

    CompositionLocalProvider(
        LocalTextToolbar provides WrapperToAvoidPaste,
        LocalTextSelectionColors provides avoidCursorHandle
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { testTagsAsResourceId = true }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    requesterToClearFocus.requestFocus()
                    expiryDateState.isPickerOpen = true
                }
        ) {
            EmptyViewJustToClearFocus(
                Modifier
                    .focusRequester(requesterToClearFocus)
                    .onFocusChanged { focusState -> // Don't call 'onFocus' if already has focus
                        if (focusState.isFocused && !alreadyHasFocus) {
                            alreadyHasFocus = true
                            onEvent?.invoke(PSCardFieldInputEvent.FOCUS)
                        } else if (!focusState.isFocused) {
                            alreadyHasFocus = false
                        } else {
                            // NOOP
                        }
                    }
            )
            Box {
                PSExpiryDatePicker(
                    state = expiryDateState,
                    labelText = labelText,
                    placeholderText = placeholderText,
                    animateTopLabelText = animateTopLabelText,
                    modifier = modifier,
                    psTheme = psTheme
                )
                if (expiryDateState.showLabelWithoutAnimation(animateTopLabelText, labelText)) {
                    TextLabelReplacement(
                        labelText = labelText,
                        isValidInUI = expiryDateState.isValidInUi,
                        psTheme = psTheme,
                        modifier = Modifier
                            .testTag(PS_EXPIRY_DATE_PICKER_NO_ANIM_LABEL_TEST_TAG)
                            .align(Alignment.Center)
                    )
                }
            }
        }
        if (expiryDateState.isPickerOpen) {
            PSMonthYearPickerDialogIfPickerOpen(
                pickerTitleText = pickerTitleText,
                expiryDateState = expiryDateState,
                psTheme = psTheme,
                isValidLiveData = isValidLiveData,
                onEvent = onEvent
            )
        }
    }
}

@Composable
private fun PSMonthYearPickerDialogIfPickerOpen(
    pickerTitleText: String,
    expiryDateState: PSExpiryDateState,
    psTheme: PSTheme,
    isValidLiveData: MutableLiveData<Boolean>,
    onEvent: ((PSCardFieldInputEvent) -> Unit)?
) {
    PSMonthYearPickerDialog(
        title = pickerTitleText,
        inputMonthYear = expiryDateState.value,
        showDialog = expiryDateState.isPickerOpen,
        psTheme = psTheme,
        onDialogCancel = { expiryDateState.isPickerOpen = false },
        onDialogConfirm = {
            val isNewValueDifferent = expiryDateState.value != it
            expiryDateState.value = it
            expiryDateState.isPickerOpen = false
            expiryDateState.isValidInUi = ExpiryDateChecks.validations(it)

            if (isNewValueDifferent) {
                onEvent?.invoke(PSCardFieldInputEvent.FIELD_VALUE_CHANGE)
            }
            onEvent?.invoke(
                if (expiryDateState.isValidInUi) PSCardFieldInputEvent.VALID
                else PSCardFieldInputEvent.INVALID
            )

            isValidLiveData.postValue(expiryDateState.isValidInUi)
        }
    )
}