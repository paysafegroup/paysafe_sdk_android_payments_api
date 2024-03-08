/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.cvv

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
import com.paysafe.android.hostedfields.model.PSCvvState
import com.paysafe.android.hostedfields.util.PS_CVV_NO_ANIM_LABEL_TEST_TAG
import com.paysafe.android.hostedfields.util.TextLabelReplacement
import com.paysafe.android.hostedfields.util.WrapperToAvoidPaste
import com.paysafe.android.hostedfields.util.avoidCursorHandle
import com.paysafe.android.hostedfields.util.rememberCvvState

/**
 * Composable to provide card verification value component for user interface.
 *
 * @param cvvState State to store card verification value text.
 * @param modifier Compose modifier for [PSCvv] to decorate or add behavior.
 * @param labelText Helper label shown inside [OutlinedTextField].
 * @param placeholderText Helper placeholder shown inside [OutlinedTextField].
 * @param animateTopLabelText If 'true' it will show the default animation for [OutlinedTextField], otherwise the label will remain in place.
 * @param isValidLiveData [LiveData] that stores if card verification value is valid.
 * @param onEvent Callback function that reacts to several [PSCardFieldInputEvent].
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
@JvmSynthetic
fun PSCvvField(
    cvvState: PSCvvState = rememberCvvState(),
    modifier: Modifier,
    labelText: String,
    placeholderText: String?,
    animateTopLabelText: Boolean,
    psTheme: PSTheme,
    isMasked: Boolean,
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
            PSCvv(
                state = cvvState,
                modifier = modifier,
                labelText = labelText,
                placeholderText = placeholderText,
                animateTopLabelText = animateTopLabelText,
                isValidLiveData = isValidLiveData,
                psTheme = psTheme,
                isMasked = isMasked,
                onEvent = onEvent
            )
            if (cvvState.showLabelWithoutAnimation(animateTopLabelText, labelText)) {
                TextLabelReplacement(
                    labelText = labelText,
                    isValidInUI = cvvState.isValidInUi,
                    psTheme = psTheme,
                    modifier = Modifier
                        .testTag(PS_CVV_NO_ANIM_LABEL_TEST_TAG)
                        .align(Alignment.Center)
                )
            }
        }
    }
}