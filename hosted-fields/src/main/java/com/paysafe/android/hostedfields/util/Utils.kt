/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.util

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paysafe.android.hostedfields.PSTheme
import com.paysafe.android.hostedfields.cardnumber.CardNumberSpaces
import com.paysafe.android.hostedfields.expirydate.ExpiryDateSlash
import com.paysafe.android.hostedfields.model.CardNumberSeparator
import com.paysafe.android.hostedfields.model.PSCardNumberStateImpl
import com.paysafe.android.hostedfields.model.PSCardholderNameStateImpl
import com.paysafe.android.hostedfields.model.PSCvvStateImpl
import com.paysafe.android.hostedfields.model.PSExpiryDateStateImpl
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType

//region Constants
internal const val CARD_NUMBER_VALUE_INDEX = 0
internal const val CARD_NUMBER_FOCUSED_INDEX = 1
internal const val CARD_NUMBER_TYPE_INDEX = 2
internal const val CARD_NUMBER_PLACEHOLDER_INDEX = 3
internal const val CARD_NUMBER_VALID_INDEX = 4
internal const val CARD_NUMBER_ALREADY_SHOWN_INDEX = 5
internal const val CARDHOLDER_NAME_VALUE_INDEX = 0
internal const val CARDHOLDER_NAME_FOCUSED_INDEX = 1
internal const val CARDHOLDER_NAME_VALID_INDEX = 2
internal const val CARDHOLDER_NAME_ALREADY_SHOWN_INDEX = 3
internal const val EXPIRY_DATE_VALUE_INDEX = 0
internal const val EXPIRY_DATE_FOCUSED_INDEX = 1
internal const val EXPIRY_DATE_VALID_INDEX = 2
internal const val EXPIRY_DATE_PICKER_OPEN_INDEX = 3
internal const val EXPIRY_DATE_ALREADY_SHOWN_INDEX = 4
internal const val CVV_VALUE_INDEX = 0
internal const val CVV_FOCUSED_INDEX = 1
internal const val CVV_CARD_TYPE_INDEX = 2
internal const val CVV_VALID_INDEX = 3
internal const val CVV_ALREADY_SHOWN_INDEX = 4

internal const val PS_CARD_NUMBER_TEST_TAG = "cardNumberInputTextField"
internal const val PS_CARD_HOLDER_NAME_TEST_TAG = "cardholderNameInputTextField"
internal const val PS_EXPIRY_DATE_PICKER_TEST_TAG = "cardExpiryPickerTextField"
internal const val PS_EXPIRY_DATE_TEXT_TEST_TAG = "cardExpiryInputTextField"
internal const val PS_MONTH_YEAR_PICKER_DIALOG_TEST_TAG = "monthYearPickerDialog"
internal const val PS_MONTH_YEAR_PICKER_DIALOG_CONFIRM_TEST_TAG = "monthYearPickerDialogConfirm"
internal const val PS_CVV_TEST_TAG = "cardCVVInputTextField"

internal const val PS_CARD_NUMBER_NO_ANIM_LABEL_TEST_TAG = "cardNumberNoAnimationLabel"
internal const val PS_CARD_HOLDER_NAME_NO_ANIM_LABEL_TEST_TAG = "cardholderNameNoAnimationLabel"
internal const val PS_EXPIRY_DATE_PICKER_NO_ANIM_LABEL_TEST_TAG = "cardExpiryPickerNoAnimationLabel"
internal const val PS_EXPIRY_DATE_TEXT_NO_ANIM_LABEL_TEST_TAG = "cardExpiryTextNoAnimationLabel"
internal const val PS_CVV_NO_ANIM_LABEL_TEST_TAG = "cardCVVNoAnimationLabel"

internal const val PREVIEW_BACKGROUND_COLOR = 0xFFFFFFFF
internal const val PREVIEW_NIGHT_BACKGROUND_COLOR = 0xFF000000
//endregion

//region Visual Transformations
@Composable
internal fun cardNumberVisualTransformation(
    cardType: PSCreditCardType,
    separator: CardNumberSeparator
) = VisualTransformation { text ->
    CardNumberSpaces.separator = separator
    if (cardType == PSCreditCardType.AMEX) {
        TransformedText(
            CardNumberSpaces.formatAmexWithSeparator(text),
            CardNumberSpaces.amexSpacesMapping
        )
    } else {
        TransformedText(
            CardNumberSpaces.formatDefaultWithSeparator(text),
            CardNumberSpaces.defaultSpacesMapping
        )
    }
}

@Composable
internal fun expiryDateVisualTransformation() = VisualTransformation { text ->
    TransformedText(ExpiryDateSlash.formatWithSlash(text), ExpiryDateSlash.slashMapping)
}
//endregion

internal fun keyboardActionFromIme(
    imeAction: ImeAction,
    onKeyboardAction: (KeyboardActionScope.() -> Unit)?
) = when (imeAction) {
    ImeAction.Go -> KeyboardActions(onGo = onKeyboardAction)
    ImeAction.Next -> KeyboardActions(onNext = onKeyboardAction)
    ImeAction.Previous -> KeyboardActions(onPrevious = onKeyboardAction)
    ImeAction.Search -> KeyboardActions(onSearch = onKeyboardAction)
    ImeAction.Send -> KeyboardActions(onSend = onKeyboardAction)
    else -> KeyboardActions(onDone = onKeyboardAction)
}

//region Remember Saveables for Hosted Fields
@Composable
fun rememberCardNumberState() = rememberSaveable(
    saver = PSCardNumberStateImpl.Saver
) { PSCardNumberStateImpl() }

@Composable
fun rememberCardholderNameState() = rememberSaveable(
    saver = PSCardholderNameStateImpl.Saver
) { PSCardholderNameStateImpl() }

@Composable
fun rememberExpiryDateState() = rememberSaveable(
    saver = PSExpiryDateStateImpl.Saver
) { PSExpiryDateStateImpl() }

@Composable
fun rememberCvvState() = rememberSaveable(
    saver = PSCvvStateImpl.Saver
) { PSCvvStateImpl() }
//endregion

//region PSTheme
@Composable
internal fun textFieldColorsWithPSTheme(
    psTheme: PSTheme,
    isValidInUI: Boolean = true
): TextFieldColors = with(psTheme) {
    OutlinedTextFieldDefaults.colors(
        // container
        disabledContainerColor = Color(backgroundColor),
        unfocusedContainerColor = Color(backgroundColor),
        focusedContainerColor = Color(backgroundColor),
        errorContainerColor = Color(backgroundColor),
        // border <- default, focused & error borders
        disabledBorderColor = Color(if (isValidInUI) borderColor else errorColor),
        unfocusedBorderColor = Color(borderColor),
        focusedBorderColor = Color(focusedBorderColor),
        errorBorderColor = Color(errorColor),
        // text <- textInput
        disabledTextColor = Color(textInputColor),
        unfocusedTextColor = Color(textInputColor),
        focusedTextColor = Color(textInputColor),
        errorTextColor = Color(textInputColor),
        // label <- placeholder + error
        disabledLabelColor = Color(if (isValidInUI) placeholderColor else errorColor),
        unfocusedLabelColor = Color(placeholderColor),
        focusedLabelColor = Color(placeholderColor),
        errorLabelColor = Color(errorColor),
        // placeholder <- hint
        disabledPlaceholderColor = Color(hintColor),
        unfocusedPlaceholderColor = Color(hintColor),
        focusedPlaceholderColor = Color(hintColor),
        errorPlaceholderColor = Color(hintColor),
        // cursor
        cursorColor = Color(textInputColor),
        errorCursorColor = Color(textInputColor)
    )
}

@Composable
internal fun textStyleWithPSTheme(psTheme: PSTheme): TextStyle = with(psTheme) {
    LocalTextStyle.current.copy(
        fontSize = with(LocalDensity.current) {
            textInputFontSize.toSp()
        },
        fontFamily = textInputFontFamily?.let { FontFamily(Font(it)) }
    )
}

@Composable
internal fun roundedCornerShapeWithPSTheme(psTheme: PSTheme) =
    RoundedCornerShape(psTheme.borderCornerRadius)

@Composable
internal fun pickerButtonColorsWithPSTheme(
    psTheme: PSTheme
) = ButtonDefaults.buttonColors(
    containerColor = Color(psTheme.expiryPickerButtonBackgroundColor),
    contentColor = Color(psTheme.expiryPickerButtonTextColor)
)

@Composable
internal fun TextLabelWithPSTheme(
    labelText: String,
    psTheme: PSTheme
) = Text(
    text = labelText,
    style = with(psTheme) {
        LocalTextStyle.current.copy(
            fontSize = with(LocalDensity.current) {
                placeholderFontSize.toSp()
            },
            fontFamily = placeholderFontFamily?.let { FontFamily(Font(it)) }
        )
    }
)

@Composable
internal fun TextLabelReplacement(
    labelText: String,
    isValidInUI: Boolean,
    psTheme: PSTheme,
    modifier: Modifier
) = Text(
    text = labelText,
    color = with(psTheme) {
        Color(if (isValidInUI) placeholderColor else errorColor)
    },
    letterSpacing = 0.5.sp,
    style = with(psTheme) {
        LocalTextStyle.current.copy(
            fontSize = with(LocalDensity.current) {
                placeholderFontSize.toSp()
            },
            fontFamily = placeholderFontFamily?.let { FontFamily(Font(it)) }
        )
    },
    modifier = modifier
        .fillMaxSize()
        .padding(start = 16.dp, top = 8.dp)
)

@Composable
internal fun TextPlaceholderWithPSTheme(
    placeholderText: String,
    psTheme: PSTheme
) = Text(
    text = placeholderText,
    style = with(psTheme) {
        LocalTextStyle.current.copy(
            fontSize = with(LocalDensity.current) {
                hintFontSize.toSp()
            },
            fontFamily = hintFontFamily?.let { FontFamily(Font(it)) }
        )
    }
)

@Preview(
    name = "Day",
    widthDp = 320,
    heightDp = 100,
    showBackground = true,
    backgroundColor = PREVIEW_BACKGROUND_COLOR
)
@Preview(
    name = "Night",
    widthDp = 320,
    heightDp = 100,
    showBackground = true,
    backgroundColor = PREVIEW_NIGHT_BACKGROUND_COLOR,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class CardPreview

//endregion