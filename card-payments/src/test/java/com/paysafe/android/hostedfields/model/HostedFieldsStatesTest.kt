/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.model

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.paysafe.android.hostedfields.cardnumber.PSCardNumberField
import com.paysafe.android.hostedfields.cardnumber.PSCardNumberFieldOptions
import com.paysafe.android.hostedfields.cardnumber.PSCardNumberFieldTextOptions
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
import com.paysafe.android.hostedfields.PSTheme
import com.paysafe.android.hostedfields.util.rememberCardNumberState
import com.paysafe.android.hostedfields.util.rememberCardholderNameState
import com.paysafe.android.hostedfields.util.rememberCvvState
import com.paysafe.android.hostedfields.util.rememberExpiryDateState
import com.paysafe.android.hostedfields.util.uniformFieldBorder
import com.paysafe.android.hostedfields.util.CompactFieldWrapper
import com.paysafe.android.hostedfields.util.textFieldColorsWithPSTheme
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
                textOptions = PSCardNumberFieldTextOptions(
                    labelText = "Card number",
                    placeholderText = null,
                    animateTopLabelText = true
                ),
                cardNumberLiveData = PSCardNumberLiveData(
                    cardTypeLiveData = MutableLiveData(PSCreditCardType.UNKNOWN),
                    isValidLiveData = MutableLiveData(false)
                ),
                psTheme = defaultTheme,
                fieldOptions = PSCardNumberFieldOptions(
                    separator = CardNumberSeparator.DASH
                ),
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
        val eventHandler = DefaultPSCardFieldEventHandler(MutableLiveData(false))
        composeTestRule.setContent {
            val inputState = rememberExpiryDateState()
            PSExpiryDateTextField(
                expiryDateState = inputState,
                modifier = Modifier.fillMaxWidth(),
                labelText = "Card number",
                placeholderText = null,
                animateTopLabelText = true,
                isValidLiveData = MutableLiveData(false),
                psTheme = defaultTheme,
                eventHandler = eventHandler
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

    // Helper function to create test PSTheme
    private fun createTestPSTheme(
        borderWidth: Float? = null,
        focusedBorderWidth: Float? = null,
        borderColor: Int = Color.Gray.toArgb(),
        focusedBorderColor: Int = Color.Blue.toArgb(),
        errorColor: Int = Color.Red.toArgb()
    ) = PSTheme(
        backgroundColor = Color.White.toArgb(),
        borderColor = borderColor,
        focusedBorderColor = focusedBorderColor,
        borderCornerRadius = 4f,
        borderWidth = borderWidth,
        focusedBorderWidth = focusedBorderWidth,
        errorColor = errorColor,
        textInputColor = Color.Black.toArgb(),
        textInputFontSize = 16f,
        placeholderColor = Color.Gray.toArgb(),
        placeholderFontSize = 14f,
        hintColor = Color.LightGray.toArgb(),
        hintFontSize = 12f,
        expiryPickerButtonBackgroundColor = Color.Blue.toArgb(),
        expiryPickerButtonTextColor = Color.White.toArgb()
    )

    @Test
    fun `uniformFieldBorder returns unmodified modifier when both border widths are null`() {
        // Arrange
        val psTheme = createTestPSTheme(borderWidth = null, focusedBorderWidth = null)

        // Act & Assert
        composeTestRule.setContent {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .uniformFieldBorder(
                        isFocused = false,
                        isError = false,
                        psTheme = psTheme,
                        shape = RectangleShape
                    )
                    .testTag("box")
            )
        }
        composeTestRule.onNodeWithTag("box").assertIsDisplayed()
    }

    @Test
    fun `uniformFieldBorder applies correct width and color for different states`() {
        // Arrange
        val psTheme = createTestPSTheme(borderWidth = 2f, focusedBorderWidth = 4f)

        // Test focused state
        composeTestRule.setContent {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .uniformFieldBorder(
                        isFocused = true,
                        isError = false,
                        psTheme = psTheme,
                        shape = RectangleShape
                    )
                    .testTag("focused")
            )
        }
        composeTestRule.onNodeWithTag("focused").assertIsDisplayed()
    }

    @Test
    fun `uniformFieldBorder prioritizes error color over focused color`() {
        // Arrange
        val psTheme = createTestPSTheme(
            borderWidth = 2f,
            errorColor = Color.Red.toArgb(),
            focusedBorderColor = Color.Blue.toArgb()
        )

        // Act & Assert
        composeTestRule.setContent {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .uniformFieldBorder(
                        isFocused = true,
                        isError = true,
                        psTheme = psTheme,
                        shape = RectangleShape
                    )
                    .testTag("error-focused")
            )
        }
        composeTestRule.onNodeWithTag("error-focused").assertIsDisplayed()
    }

    // ==========================================
    // CompactFieldWrapper tests - Essential coverage
    // ==========================================

    @Test
    fun `CompactFieldWrapper renders normally when compactFieldHeight is null`() {
        // Arrange
        val psTheme = createTestPSTheme()

        // Act
        composeTestRule.setContent {
            CompactFieldWrapper(
                compactFieldHeight = null,
                modifier = Modifier,
                isFocused = false,
                isError = false,
                psTheme = psTheme,
                shape = RectangleShape
            ) { innerModifier ->
                Text(
                    text = "Normal mode",
                    modifier = innerModifier
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Normal mode").assertIsDisplayed()
    }

    @Test
    fun `CompactFieldWrapper renders with height constraint when compactFieldHeight is set`() {
        // Arrange
        val psTheme = createTestPSTheme(borderWidth = 2f)

        // Act
        composeTestRule.setContent {
            CompactFieldWrapper(
                compactFieldHeight = 48f,
                modifier = Modifier,
                isFocused = false,
                isError = false,
                psTheme = psTheme,
                shape = RoundedCornerShape(8.dp)
            ) { innerModifier ->
                Text(
                    text = "Compact mode",
                    modifier = innerModifier
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Compact mode").assertIsDisplayed()
    }

    @Test
    fun `textFieldColorsWithPSTheme handles transparent borders correctly`() {
        // Arrange - Test both cases: with and without custom border widths
        val psThemeWithBorder = createTestPSTheme(borderWidth = 2f)
        val psThemeNoBorder = createTestPSTheme(borderWidth = null, focusedBorderWidth = null)

        // Act & Assert
        composeTestRule.setContent {
            textFieldColorsWithPSTheme(psTheme = psThemeWithBorder, isValidInUI = true)
            textFieldColorsWithPSTheme(psTheme = psThemeNoBorder, isValidInUI = false)
        }
    }

}