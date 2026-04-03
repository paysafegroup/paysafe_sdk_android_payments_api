/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.cardnumber

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.paysafe.android.hostedfields.PSTheme
import com.paysafe.android.hostedfields.R
import com.paysafe.android.hostedfields.domain.model.CardNumberSeparator
import com.paysafe.android.hostedfields.domain.model.PSCardFieldInputEvent
import com.paysafe.android.hostedfields.domain.model.PSCardNumberState
import com.paysafe.android.hostedfields.domain.model.PSCardNumberStateImpl

import com.paysafe.android.hostedfields.model.DefaultPSCardFieldEventHandler
import com.paysafe.android.hostedfields.model.PSCardFieldEventHandler

import com.paysafe.android.hostedfields.provideDefaultPSTheme
import com.paysafe.android.hostedfields.util.CardPreview
import com.paysafe.android.hostedfields.util.PS_CARD_NUMBER_TEST_TAG
import com.paysafe.android.hostedfields.util.TextLabelWithPSTheme
import com.paysafe.android.hostedfields.util.TextPlaceholderWithPSTheme
import com.paysafe.android.hostedfields.util.cardNumberVisualTransformation
import com.paysafe.android.hostedfields.util.CompactFieldWrapper
import com.paysafe.android.hostedfields.util.keyboardActionFromIme
import com.paysafe.android.hostedfields.util.roundedCornerShapeWithPSTheme
import com.paysafe.android.hostedfields.util.textFieldColorsWithPSTheme
import com.paysafe.android.hostedfields.util.textStyleWithPSTheme
import com.paysafe.android.hostedfields.valid.CardNumberChecks
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType

//region HOSTED FIELD: Card Number
@Composable
private fun CardNumberBrandIcon(
    cardType: PSCreditCardType,
    modifier: Modifier = Modifier
) {
    if (cardType != PSCreditCardType.UNKNOWN) {
        Image(
            painter = painterResource(id = getCreditCardIcon(cardType)),
            contentDescription = null,
            modifier = modifier
        )
    }
}

fun getCreditCardIcon(creditCardType: PSCreditCardType) = when (creditCardType) {
    PSCreditCardType.VISA -> R.drawable.ic_cc_visa
    PSCreditCardType.MASTERCARD -> R.drawable.ic_cc_mastercard
    PSCreditCardType.AMEX -> R.drawable.ic_cc_amex
    PSCreditCardType.DISCOVER -> R.drawable.ic_cc_discover
    else -> 0
}

private fun onCardNumberChange(
    cardNumberState: PSCardNumberState,
    cardTypeLiveData: MutableLiveData<PSCreditCardType>,
    isValidLiveData: MutableLiveData<Boolean>,
    eventHandler: PSCardFieldEventHandler,
    clearsErrorOnInput: Boolean = false
): (String) -> Unit = { input ->
    val inputData = CardNumberChecks.inputProtection(input, cardNumberState.type, eventHandler::handleEvent)

    if (cardNumberState.value != inputData.first) { // is new value distinct?
        val isValid = CardNumberChecks.validations(inputData.first)
        val noInvalidCharacters = inputData.first == input

        // Security check to avoid double trigger for 'onFieldValueChange'; if 'input' contains an
        // invalid character its value would be different from the one returned by 'inputProtection'
        if (noInvalidCharacters) eventHandler.handleEvent(PSCardFieldInputEvent.FIELD_VALUE_CHANGE)
        eventHandler.handleEvent(if (isValid) PSCardFieldInputEvent.VALID else PSCardFieldInputEvent.INVALID)

        if (clearsErrorOnInput) {
            cardNumberState.isValidInUi = true
        }

        isValidLiveData.postValue(isValid)
    }
    cardNumberState.value = inputData.first
    cardNumberState.type = inputData.second
    cardNumberState.placeholder = inputData.third

    cardTypeLiveData.postValue(cardNumberState.type)
}

private fun onDonePressed(
    cardNumberState: PSCardNumberState,
    focusManager: FocusManager,
    validatesEmptyFieldOnBlur: Boolean = true
): (KeyboardActionScope.() -> Unit) = {
    focusManager.clearFocus()
    cardNumberState.isValidInUi = if (!validatesEmptyFieldOnBlur && cardNumberState.value.isEmpty()) {
        true
    } else {
        CardNumberChecks.validations(cardNumberState.value)
    }
}

private fun onCardNumberFocusChange(
    focusState: FocusState,
    cardNumberState: PSCardNumberState,
    eventHandler: PSCardFieldEventHandler,
    validatesEmptyFieldOnBlur: Boolean = true
) {
    if (focusState.isFocused) {
        eventHandler.handleEvent(PSCardFieldInputEvent.FOCUS)
    } else {
        eventHandler.handleEvent(PSCardFieldInputEvent.BLUR)
    }
    val isInactive = !focusState.isFocused
    cardNumberState.isFocused = focusState.isFocused
    if (isInactive && cardNumberState.alreadyShown) {
        cardNumberState.isValidInUi = if (!validatesEmptyFieldOnBlur && cardNumberState.value.isEmpty()) {
            true
        } else {
            CardNumberChecks.validations(cardNumberState.value)
        }
    }
    cardNumberState.alreadyShown = true
}

@Composable
internal fun PSCardNumber(
    fieldConfig: PSCardNumberFieldConfig,
    textConfig: PSCardNumberTextConfig,
    cardNumberLiveData: PSCardNumberLiveData,
    psTheme: PSTheme,
    uiConfig: PSCardNumberUIConfig = PSCardNumberUIConfig(),
    validationConfig: PSCardNumberValidationConfig = PSCardNumberValidationConfig(),
    eventHandler: PSCardFieldEventHandler
) {
    val onValueChange: (String) -> Unit = onCardNumberChange(
        fieldConfig.state,
        cardNumberLiveData.cardTypeLiveData,
        cardNumberLiveData.isValidLiveData,
        eventHandler,
        validationConfig.clearsErrorOnInput
    )
    val focusManager = LocalFocusManager.current
    val keyboardImeAction: ImeAction = ImeAction.Done
    val onKeyboardAction: (KeyboardActionScope.() -> Unit) =
        onDonePressed(fieldConfig.state, focusManager, validationConfig.validatesEmptyFieldOnBlur)
    val shape = roundedCornerShapeWithPSTheme(psTheme)

    CompactFieldWrapper(
        compactFieldHeight = uiConfig.compactFieldHeight,
        modifier = fieldConfig.modifier.modifier
            .testTag(PS_CARD_NUMBER_TEST_TAG)
            .onFocusChanged { onCardNumberFocusChange(it, fieldConfig.state, eventHandler, validationConfig.validatesEmptyFieldOnBlur) },
        isFocused = fieldConfig.state.isFocused,
        isError = !fieldConfig.state.isValidInUi,
        psTheme = psTheme,
        shape = shape
    ) { innerModifier ->
        OutlinedTextField(
            value = fieldConfig.state.value,
            onValueChange = onValueChange,
            // Texts //
            label = if (textConfig.animateTopLabelText) {
                {
                    TextLabelWithPSTheme(
                        labelText = textConfig.labelText,
                        psTheme = psTheme
                    )
                }
            } else null,
            placeholder = {
                TextPlaceholderWithPSTheme(
                    placeholderText = textConfig.placeholderText
                        ?: stringResource(id = R.string.card_number_hint),
                    psTheme = psTheme
                )
            },
            // Keyboard Settings //
            keyboardOptions = KeyboardOptions(
                imeAction = keyboardImeAction, keyboardType = KeyboardType.Number
            ),
            keyboardActions = keyboardActionFromIme(keyboardImeAction, onKeyboardAction),
            // Optical //
            visualTransformation = cardNumberVisualTransformation(fieldConfig.state.type, uiConfig.separator),
            trailingIcon = if (uiConfig.showBrandIcon) {
                { CardNumberBrandIcon(fieldConfig.state.type, fieldConfig.modifier.cardBrandModifier) }
            } else null,
            // Extra //
            singleLine = true,
            isError = !fieldConfig.state.isValidInUi,
            modifier = innerModifier,
            colors = textFieldColorsWithPSTheme(psTheme),
            shape = shape,
            textStyle = textStyleWithPSTheme(psTheme)
        )
    }
}
//endregion

//region Previews
@CardPreview
@Composable
internal fun Default() {
    PreviewPSCardNumber()
}

@CardPreview
@Composable
internal fun InputSeparatorWhitespace() {
    val cardNumber = PSCardNumberStateImpl()
    cardNumber.value = "5234567890123456"
    cardNumber.type = PSCreditCardType.MASTERCARD
    PreviewPSCardNumber(
        cardNumber = cardNumber,
        separator = CardNumberSeparator.WHITESPACE
    )
}

@CardPreview
@Composable
internal fun InputSeparatorNone() {
    val cardNumber = PSCardNumberStateImpl()
    cardNumber.value = "6234567890123456"
    cardNumber.type = PSCreditCardType.DISCOVER
    PreviewPSCardNumber(
        cardNumber = cardNumber,
        separator = CardNumberSeparator.NONE
    )
}

@CardPreview
@Composable
internal fun InputSeparatorDashVisa() {
    val cardNumber = PSCardNumberStateImpl()
    cardNumber.value = "4234567890123456"
    cardNumber.type = PSCreditCardType.VISA
    PreviewPSCardNumber(
        cardNumber = cardNumber,
        separator = CardNumberSeparator.DASH
    )
}

@CardPreview
@Composable
internal fun InputSeparatorSlashAmex() {
    val cardNumber = PSCardNumberStateImpl()
    cardNumber.value = "343456789012345"
    cardNumber.type = PSCreditCardType.AMEX
    PreviewPSCardNumber(
        cardNumber = cardNumber,
        separator = CardNumberSeparator.SLASH
    )
}

@CardPreview
@Composable
internal fun Error() {
    val cardNumber = PSCardNumberStateImpl()
    cardNumber.value = "1234"
    cardNumber.isValidInUi = false
    PreviewPSCardNumber(cardNumber)
}

@Composable
internal fun PreviewPSCardNumber(
    cardNumber: PSCardNumberState = PSCardNumberStateImpl(),
    separator: CardNumberSeparator = CardNumberSeparator.WHITESPACE
) {
    val eventHandler = DefaultPSCardFieldEventHandler(MutableLiveData(false))
    PSCardNumber(
        fieldConfig = PSCardNumberFieldConfig(
            state = cardNumber,
            modifier = PSCardNumberModifier(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                cardBrandModifier = Modifier.padding(end = 16.dp)
            )
        ),
        textConfig = PSCardNumberTextConfig(
            labelText = "Card number",
            animateTopLabelText = true
        ),
        cardNumberLiveData = PSCardNumberLiveData(
            cardTypeLiveData = MutableLiveData(PSCreditCardType.UNKNOWN),
            isValidLiveData = MutableLiveData(false)
        ),
        psTheme = provideDefaultPSTheme(),
        uiConfig = PSCardNumberUIConfig(separator = separator),
        eventHandler = eventHandler
    )
}
//endregion