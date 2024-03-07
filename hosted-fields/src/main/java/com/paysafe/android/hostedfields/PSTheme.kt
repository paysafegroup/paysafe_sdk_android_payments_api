/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.FontRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource

/**
 * PSTheme used for styling Paysafe components
 */
data class PSTheme(
    /** Card component background color. */
    @ColorInt
    var backgroundColor: Int,
    /** Card component border color in default state. */
    @ColorInt
    var borderColor: Int,
    /** Card component border color in focused state. */
    @ColorInt
    var focusedBorderColor: Int,
    /** Card component border corner radius. */
    @Dimension
    var borderCornerRadius: Float,
    /** Card component border & placeholder color for invalid/error state. */
    @ColorInt
    var errorColor: Int,
    /** Card component text input color. */
    @ColorInt
    var textInputColor: Int,
    /** Card component text input font size. */
    @Dimension
    var textInputFontSize: Float,
    /** Card component text input font family. */
    @FontRes
    var textInputFontFamily: Int? = null,
    /** Card component placeholder text color. */
    @ColorInt
    var placeholderColor: Int,
    /** Card component placeholder text font size. */
    @Dimension
    var placeholderFontSize: Float,
    /** Card component placeholder text font family. */
    @FontRes
    var placeholderFontFamily: Int? = null,
    /** Card component hint text color. */
    @ColorInt
    var hintColor: Int,
    /** Card component hint text font size. */
    @Dimension
    var hintFontSize: Float,
    /** Card component hint text font family. */
    @FontRes
    var hintFontFamily: Int? = null,
    /** Card component expiry picker/dialog button background color. */
    @ColorInt
    var expiryPickerButtonBackgroundColor: Int,
    /** Card component expiry picker/dialog button text color. */
    @ColorInt
    var expiryPickerButtonTextColor: Int
)

internal fun provideDefaultPSTheme(
    context: Context
): PSTheme = PSTheme(
    backgroundColor = context.getColor(R.color.ps_card_background),
    borderColor = context.getColor(R.color.ps_card_border),
    focusedBorderColor = context.getColor(R.color.ps_card_focused_border),
    borderCornerRadius = context.resources.getDimension(R.dimen.ps_card_border_corner_radius),
    errorColor = context.getColor(R.color.ps_card_error),
    textInputColor = context.getColor(R.color.ps_card_text_input),
    textInputFontSize = context.resources.getDimension(R.dimen.ps_card_text_input_font_size),
    placeholderColor = context.getColor(R.color.ps_card_placeholder),
    placeholderFontSize = context.resources.getDimension(R.dimen.ps_card_placeholder_font_size),
    hintColor = context.getColor(R.color.ps_card_hint),
    hintFontSize = context.resources.getDimension(R.dimen.ps_card_hint_font_size),
    expiryPickerButtonBackgroundColor = context.getColor(R.color.ps_expiry_picker_button_background),
    expiryPickerButtonTextColor = context.getColor(R.color.ps_expiry_picker_button_text)
)

@Composable
internal fun provideDefaultPSTheme(): PSTheme = PSTheme(
    backgroundColor = colorResource(id = R.color.ps_card_background).toArgb(),
    borderColor = colorResource(id = R.color.ps_card_border).toArgb(),
    focusedBorderColor = colorResource(id = R.color.ps_card_focused_border).toArgb(),
    borderCornerRadius = dimensionResource(id = R.dimen.ps_card_border_corner_radius).value,
    errorColor = colorResource(id = R.color.ps_card_error).toArgb(),
    textInputColor = colorResource(id = R.color.ps_card_text_input).toArgb(),
    textInputFontSize = with(LocalDensity.current) {
        dimensionResource(id = R.dimen.ps_card_text_input_font_size).toPx()
    },
    placeholderColor = colorResource(id = R.color.ps_card_placeholder).toArgb(),
    placeholderFontSize = with(LocalDensity.current) {
        dimensionResource(id = R.dimen.ps_card_placeholder_font_size).toPx()
    },
    hintColor = colorResource(id = R.color.ps_card_hint).toArgb(),
    hintFontSize = with(LocalDensity.current) {
        dimensionResource(id = R.dimen.ps_card_hint_font_size).toPx()
    },
    expiryPickerButtonBackgroundColor = colorResource(id = R.color.ps_expiry_picker_button_background).toArgb(),
    expiryPickerButtonTextColor = colorResource(id = R.color.ps_expiry_picker_button_text).toArgb()
)