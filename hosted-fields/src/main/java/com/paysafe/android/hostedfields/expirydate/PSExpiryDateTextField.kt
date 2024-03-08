/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.expirydate

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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.paysafe.android.hostedfields.PSTheme
import com.paysafe.android.hostedfields.model.PSCardFieldInputEvent
import com.paysafe.android.hostedfields.model.PSExpiryDateState
import com.paysafe.android.hostedfields.util.PS_EXPIRY_DATE_TEXT_NO_ANIM_LABEL_TEST_TAG
import com.paysafe.android.hostedfields.util.TextLabelReplacement
import com.paysafe.android.hostedfields.util.WrapperToAvoidPaste
import com.paysafe.android.hostedfields.util.avoidCursorHandle
import com.paysafe.android.hostedfields.util.rememberExpiryDateState

/**
 * Composable to provide expiry date text component for user interface.
 *
 * @param expiryDateState State to store expiry date text.
 * @param modifier Compose modifier for [PSExpiryDateText] to decorate or add behavior.
 * @param labelText Helper label shown inside [OutlinedTextField].
 * @param placeholderText Helper placeholder shown inside [OutlinedTextField].
 * @param animateTopLabelText If 'true' it will show the default animation for [OutlinedTextField], otherwise the label will remain in place.
 * @param isValidLiveData [LiveData] that stores if expiration date is valid.
 * @param onEvent Callback function that reacts to several [PSCardFieldInputEvent].
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PSExpiryDateTextField(
    expiryDateState: PSExpiryDateState = rememberExpiryDateState(),
    modifier: Modifier,
    labelText: String,
    placeholderText: String?,
    animateTopLabelText: Boolean,
    psTheme: PSTheme,
    isValidLiveData: MutableLiveData<Boolean>,
    onEvent: ((PSCardFieldInputEvent) -> Unit)? = null
) {
    CompositionLocalProvider(
        LocalTextToolbar provides WrapperToAvoidPaste,
        LocalTextSelectionColors provides avoidCursorHandle
    ) {
        Box(
            modifier = Modifier.semantics { testTagsAsResourceId = true }
        ) {
            PSExpiryDateText(
                state = expiryDateState,
                modifier = modifier,
                labelText = labelText,
                placeholderText = placeholderText,
                animateTopLabelText = animateTopLabelText,
                isValidLiveData = isValidLiveData,
                psTheme = psTheme,
                onEvent = onEvent
            )
            if (expiryDateState.showLabelWithoutAnimation(animateTopLabelText, labelText)) {
                TextLabelReplacement(
                    labelText = labelText,
                    isValidInUI = expiryDateState.isValidInUi,
                    psTheme = psTheme,
                    modifier = Modifier
                        .testTag(PS_EXPIRY_DATE_TEXT_NO_ANIM_LABEL_TEST_TAG)
                        .align(Alignment.Center)
                )
            }
        }
    }
}