/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.expirydate

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
import com.paysafe.android.hostedfields.model.PSExpiryDateState
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
 * @param isValidLiveData [LiveData] that stores if expiration date is valid.
 * @param onEvent Callback function that reacts to several [PSCardFieldInputEvent].
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PSExpiryDateTextField(
    expiryDateState: PSExpiryDateState = rememberExpiryDateState(),
    modifier: Modifier,
    labelText: String?,
    placeholderText: String?,
    psTheme: PSTheme,
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
            PSExpiryDateText(
                state = expiryDateState,
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