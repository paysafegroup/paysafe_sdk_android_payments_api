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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.paysafe.android.hostedfields.PSTheme
import com.paysafe.android.hostedfields.R
import com.paysafe.android.hostedfields.model.CardNumberSeparator
import com.paysafe.android.hostedfields.model.DefaultPSCardFieldEventHandler
import com.paysafe.android.hostedfields.model.PSCardFieldEventHandler
import com.paysafe.android.hostedfields.model.PSCardFieldInputEvent
import com.paysafe.android.hostedfields.model.PSCardNumberState
import com.paysafe.android.hostedfields.model.PSCardNumberStateImpl
import com.paysafe.android.hostedfields.provideDefaultPSTheme
import com.paysafe.android.hostedfields.util.CardPreview
import com.paysafe.android.hostedfields.util.PS_CARD_NUMBER_TEST_TAG
import com.paysafe.android.hostedfields.util.TextLabelWithPSTheme
import com.paysafe.android.hostedfields.util.TextPlaceholderWithPSTheme
import com.paysafe.android.hostedfields.util.cardNumberVisualTransformation
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
    eventHandler: PSCardFieldEventHandler
): (String) -> Unit = { input ->
    val inputData = CardNumberChecks.inputProtection(input, cardNumberState.type, eventHandler::handleEvent)

    if (cardNumberState.value != inputData.first) { // is new value distinct?
        val isValid = CardNumberChecks.validations(inputData.first)
        val noInvalidCharacters = inputData.first == input

        // Security check to avoid double trigger for 'onFieldValueChange'; if 'input' contains an
        // invalid character its value would be different from the one returned by 'inputProtection'
        if (noInvalidCharacters) eventHandler.handleEvent(PSCardFieldInputEvent.FIELD_VALUE_CHANGE)
        eventHandler.handleEvent(if (isValid) PSCardFieldInputEvent.VALID else PSCardFieldInputEvent.INVALID)

        isValidLiveData.postValue(isValid)
    }
    cardNumberState.value = inputData.first
    cardNumberState.type = inputData.second
    cardNumberState.placeholder = inputData.third

    cardTypeLiveData.postValue(cardNumberState.type)
}

@OptIn(ExperimentalComposeUiApi::class)
private fun onDonePressed(
    cardNumberState: PSCardNumberState,
    keyboardController: SoftwareKeyboardController?
): (KeyboardActionScope.() -> Unit) = {
    keyboardController?.hide()
    cardNumberState.isValidInUi = CardNumberChecks.validations(cardNumberState.value)
}

private fun onCardNumberFocusChange(
    focusState: FocusState,
    cardNumberState: PSCardNumberState,
    eventHandler: PSCardFieldEventHandler
) {
    if (focusState.isFocused) eventHandler.handleEvent(PSCardFieldInputEvent.FOCUS)
    val isInactive = !focusState.isFocused
    cardNumberState.isFocused = focusState.isFocused
    if (isInactive && cardNumberState.alreadyShown) {
        cardNumberState.isValidInUi = CardNumberChecks.validations(cardNumberState.value)
    }
    cardNumberState.alreadyShown = true
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun PSCardNumber(
    state: PSCardNumberState,
    cardNumberModifier: PSCardNumberModifier,
    labelText: String,
    placeholderText: String? = null,
    animateTopLabelText: Boolean,
    cardNumberLiveData: PSCardNumberLiveData,
    psTheme: PSTheme,
    separator: CardNumberSeparator,
    eventHandler: PSCardFieldEventHandler,
) {
    val onValueChange: (String) -> Unit = onCardNumberChange(
        state,
        cardNumberLiveData.cardTypeLiveData,
        cardNumberLiveData.isValidLiveData,
        eventHandler::handleEvent
    )
    val keyboardController = LocalSoftwareKeyboardController.current
    val keyboardImeAction: ImeAction = ImeAction.Done
    val onKeyboardAction: (KeyboardActionScope.() -> Unit) =
        onDonePressed(state, keyboardController)
    OutlinedTextField(
        value = state.value,
        onValueChange = onValueChange,
        // Texts //
        label = {
            if (animateTopLabelText) {
                TextLabelWithPSTheme(
                    labelText = labelText,
                    psTheme = psTheme
                )
            }
        },
        placeholder = {
            TextPlaceholderWithPSTheme(
                placeholderText = placeholderText
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
        visualTransformation = cardNumberVisualTransformation(state.type, separator),
        trailingIcon = { CardNumberBrandIcon(state.type, cardNumberModifier.cardBrandModifier) },
        // Extra //
        singleLine = true,
        isError = !state.isValidInUi,
        modifier = cardNumberModifier.modifier
            .testTag(PS_CARD_NUMBER_TEST_TAG)
            .onFocusChanged { onCardNumberFocusChange(it, state, eventHandler) },
        colors = textFieldColorsWithPSTheme(psTheme),
        shape = roundedCornerShapeWithPSTheme(psTheme),
        textStyle = textStyleWithPSTheme(psTheme)
    )
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
        state = cardNumber,
        cardNumberModifier = PSCardNumberModifier(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            cardBrandModifier = Modifier.padding(end = 16.dp)
        ),
        labelText = "Card number",
        animateTopLabelText = true,
        cardNumberLiveData = PSCardNumberLiveData(
            cardTypeLiveData = MutableLiveData(PSCreditCardType.UNKNOWN),
            isValidLiveData = MutableLiveData(false)
        ),
        psTheme = provideDefaultPSTheme(),
        separator = separator,
        eventHandler = eventHandler
    )
}
//endregion