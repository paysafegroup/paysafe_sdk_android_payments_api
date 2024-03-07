/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.holdername

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
import com.paysafe.android.hostedfields.model.PSCardholderNameState
import com.paysafe.android.hostedfields.util.WrapperToAvoidPaste
import com.paysafe.android.hostedfields.util.avoidCursorHandle
import com.paysafe.android.hostedfields.util.rememberCardholderNameState

/**
 * Composable to provide card holder name component for user interface.
 *
 * @param holderNameState State to store card holder name text.
 * @param modifier Compose modifier for [PSCardholderName] to decorate or add behavior.
 * @param labelText Helper label shown inside [OutlinedTextField].
 * @param placeholderText Helper placeholder shown inside [OutlinedTextField].
 * @param isValidLiveData [LiveData] that stores if card holder name is valid.
 * @param onEvent Callback function that reacts to several [PSCardFieldInputEvent].
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PSCardholderNameField(
    holderNameState: PSCardholderNameState = rememberCardholderNameState(),
    modifier: Modifier,
    labelText: String?,
    placeholderText: String?,
    isValidLiveData: MutableLiveData<Boolean>,
    psTheme: PSTheme,
    onEvent: ((PSCardFieldInputEvent) -> Unit)? = null
) {
    CompositionLocalProvider(
        LocalTextToolbar provides WrapperToAvoidPaste,
        LocalTextSelectionColors provides avoidCursorHandle
    ) {
        Column(
            modifier = Modifier.semantics { testTagsAsResourceId = true }
        ) {
            PSCardholderName(
                state = holderNameState,
                modifier = modifier,
                labelText = labelText,
                placeholderText = placeholderText,
                isValidLiveData = isValidLiveData,
                psTheme = psTheme,
                onEvent = onEvent
            )
        }
    }
}