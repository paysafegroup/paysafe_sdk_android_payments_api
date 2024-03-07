/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.util

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus

internal val avoidCursorHandle = TextSelectionColors(
    handleColor = Transparent,
    backgroundColor = Transparent,
)

internal object WrapperToAvoidPaste : TextToolbar {
    override fun showMenu(
        rect: Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?
    ) {
        // NOOP
    }

    override fun hide() {
        // NOOP
    }

    override val status: TextToolbarStatus = TextToolbarStatus.Hidden
}