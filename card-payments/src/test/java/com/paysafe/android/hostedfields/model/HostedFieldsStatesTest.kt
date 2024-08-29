/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.model

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.paysafe.android.hostedfields.cardnumber.PSCardNumberField
import com.paysafe.android.hostedfields.cardnumber.PSCardNumberLiveData
import com.paysafe.android.hostedfields.cardnumber.PSCardNumberModifier
import com.paysafe.android.hostedfields.cvv.PSCvvField
import com.paysafe.android.hostedfields.domain.model.CardNumberSeparator
import com.paysafe.android.hostedfields.domain.model.PSCardNumberStateImpl
import com.paysafe.android.hostedfields.domain.model.PSCardholderNameStateImpl
import com.paysafe.android.hostedfields.domain.model.PSCvvStateImpl
import com.paysafe.android.hostedfields.domain.model.PSExpiryDateStateImpl
import com.paysafe.android.hostedfields.expirydate.PSExpiryDateTextField
import com.paysafe.android.hostedfields.holdername.PSCardholderNameField
import com.paysafe.android.hostedfields.provideDefaultPSTheme
import com.paysafe.android.hostedfields.util.rememberCardNumberState
import com.paysafe.android.hostedfields.util.rememberCardholderNameState
import com.paysafe.android.hostedfields.util.rememberCvvState
import com.paysafe.android.hostedfields.util.rememberExpiryDateState
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class HostedFieldsStatesTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val defaultTheme = provideDefaultPSTheme(
        RuntimeEnvironment.getApplication().baseContext
    )

    @Test
    fun `IF card number state is created THEN changing rememberCardNumberState TRIGGER update`() {
        composeTestRule.setContent {
            val inputState = rememberCardNumberState()
            val eventHandler = DefaultPSCardFieldEventHandler(MutableLiveData(false))
            PSCardNumberField(
                cardNumberState = inputState,
                cardNumberModifier = PSCardNumberModifier(
                    modifier = Modifier.fillMaxWidth(),
                    cardBrandModifier = Modifier.padding(end = 16.dp)
                ),
                labelText = "Card number",
                placeholderText = null,
                animateTopLabelText = true,
                cardNumberLiveData = PSCardNumberLiveData(
                    cardTypeLiveData = MutableLiveData(PSCreditCardType.UNKNOWN),
                    isValidLiveData = MutableLiveData(false)
                ),
                psTheme = defaultTheme,
                separator = CardNumberSeparator.DASH,
                eventHandler = eventHandler
            )
            inputState.isValidInUi = true
        }
    }

    @Test
    fun `IF holder name state is created THEN changing rememberCardholderNameState TRIGGER update`() {
        composeTestRule.setContent {
            val inputState = rememberCardholderNameState()
            PSCardholderNameField(
                holderNameState = inputState,
                modifier = Modifier.fillMaxWidth(),
                labelText = "Card number",
                placeholderText = null,
                animateTopLabelText = false,
                isValidLiveData = MutableLiveData(false),
                psTheme = defaultTheme
            )
            inputState.isValidInUi = true
        }
    }

    @Test
    fun `IF expiry date state is created THEN changing rememberExpiryDateState TRIGGER update`() {
        composeTestRule.setContent {
            val inputState = rememberExpiryDateState()
            PSExpiryDateTextField(
                expiryDateState = inputState,
                modifier = Modifier.fillMaxWidth(),
                labelText = "Card number",
                placeholderText = null,
                animateTopLabelText = true,
                isValidLiveData = MutableLiveData(false),
                psTheme = defaultTheme
            )
            inputState.isValidInUi = true
        }
    }

    @Test
    fun `IF cvv state is created THEN changing rememberCvvState TRIGGER update`() {
        composeTestRule.setContent {
            val inputState = rememberCvvState()
            val eventHandler = DefaultPSCardFieldEventHandler(MutableLiveData(false))
            PSCvvField(
                cvvState = inputState,
                modifier = Modifier.fillMaxWidth(),
                labelText = "Card number",
                placeholderText = null,
                animateTopLabelText = false,
                psTheme = defaultTheme,
                isMasked = false,
                isValidLiveData = MutableLiveData(false),
                eventHandler = eventHandler
            )
            inputState.isValidInUi = true
        }
    }

    @Test
    fun `Saver should restore state correctly`() {
        // Arrange
        val savedState = listOf(
            "John Doe",
            true,
            false,
            true
        )

        // Act
        val restoredState = PSCardholderNameStateImpl.Saver.restore(savedState)!!

        // Assert
        assertEquals("John Doe", restoredState.value)
        assertEquals(true, restoredState.isFocused)
        assertEquals(false, restoredState.isValidInUi)
        assertEquals(true, restoredState.alreadyShown)
    }

    @Test
    fun `PSCardNumberStateImpl Saver should restore state correctly`() {
        // Arrange
        val savedState = listOf(
            "1234567812345678",
            true,
            PSCreditCardType.VISA,
            "XXXX XXXX XXXX 5678",
            false,
            true
        )

        // Act
        val restoredState = PSCardNumberStateImpl.Saver.restore(savedState)!!

        // Assert
        assertEquals("1234567812345678", restoredState.value)
        assertEquals(true, restoredState.isFocused)
        assertEquals(PSCreditCardType.VISA, restoredState.type)
        assertEquals("XXXX XXXX XXXX 5678", restoredState.placeholder)
        assertEquals(false, restoredState.isValidInUi)
        assertEquals(true, restoredState.alreadyShown)
    }


    @Test
    fun `PSCvvStateImpl Saver should restore state correctly`() {
        // Arrange
        val savedState = listOf(
            "123",
            true,
            PSCreditCardType.VISA,
            false,
            true
        )

        // Act
        val restoredState = PSCvvStateImpl.Saver.restore(savedState)!!

        // Assert
        assertEquals("123", restoredState.value)
        assertEquals(true, restoredState.isFocused)
        assertEquals(PSCreditCardType.VISA, restoredState.cardType)
        assertEquals(false, restoredState.isValidInUi)
        assertEquals(true, restoredState.alreadyShown)
    }

    @Test
    fun `PSExpiryDateStateImpl Saver should restore state correctly`() {
        // Arrange
        val savedState = listOf(
            "12/25",
            true,
            false,
            true,
            true
        )

        // Act
        val restoredState = PSExpiryDateStateImpl.Saver.restore(savedState)!!

        // Assert
        assertEquals("12/25", restoredState.value)
        assertEquals(true, restoredState.isFocused)
        assertEquals(false, restoredState.isValidInUi)
        assertEquals(true, restoredState.isPickerOpen)
        assertEquals(true, restoredState.alreadyShown)
    }


}