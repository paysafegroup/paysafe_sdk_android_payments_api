/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.cvv

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.paysafe.android.hostedfields.PSTheme
import com.paysafe.android.hostedfields.model.PSCardFieldInputEvent
import com.paysafe.android.hostedfields.model.PSCvvState
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
 * @param isValidLiveData [LiveData] that stores if card verification value is valid.
 * @param onEvent Callback function that reacts to several [PSCardFieldInputEvent].
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
@JvmSynthetic
fun PSCvvField(
    cvvState: PSCvvState = rememberCvvState(),
    modifier: Modifier,
    labelText: String?,
    placeholderText: String?,
    psTheme: PSTheme,
    isMasked: Boolean,
    isValidLiveData: MutableLiveData<Boolean>,
    onEvent: ((PSCardFieldInputEvent) -> Unit)? = null
) {
    CompositionLocalProvider(
        LocalTextToolbar provides WrapperToAvoidPaste,
        LocalTextSelectionColors provides avoidCursorHandle
    ) {
        Column(
            modifier = Modifier.semantics { testTagsAsResourceId = true }
        ) {
            PSCvv(
                state = cvvState,
                modifier = modifier,
                labelText = labelText,
                placeholderText = placeholderText,
                isValidLiveData = isValidLiveData,
                psTheme = psTheme,
                isMasked = isMasked,
                onEvent = onEvent
            )
        }
    }
}