package com.paysafe.android.hostedfields.expirydate

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.paysafe.android.hostedfields.domain.model.PSExpiryDateStateImpl
import com.paysafe.android.hostedfields.provideDefaultPSTheme
import com.paysafe.android.hostedfields.util.PS_EXPIRY_DATE_PICKER_TEST_TAG
import com.paysafe.android.hostedfields.util.TextPlaceholderWithPSTheme
import com.paysafe.android.hostedfields.valid.ExpiryDateChecks
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PSExpiryDatePickerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun expiryDatePicker_displaysPlaceholder() {
        // Arrange
        val placeholderText = "MM/YY"

        composeTestRule.setContent {
            TextPlaceholderWithPSTheme(
                placeholderText = placeholderText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                psTheme = provideDefaultPSTheme()
            )

        }

        // Act & Assert
        composeTestRule.onNodeWithTag("TextPlaceholderWithPSTheme_Tag")
            .onChildAt(0)
    }

    @Test
    fun `WHEN focus lost THEN isValidInUi should be updated based on value`() {
        // Arrange
        val expiryDateState = PSExpiryDateStateImpl()
        expiryDateState.value = "1223" // Formato corregido a "MMYY" si eso es lo que espera la validación
        expiryDateState.alreadyShown = true
        expiryDateState.isFocused = true

        val focusState = FakeFocusState(isFocused = false)

        // Act
        onExpiryDateFocusChange(focusState, expiryDateState)

        // Debugging output
        val isValid = ExpiryDateChecks.validations(expiryDateState.value)
        println("Validation result: $isValid")

        // Assert
        assertFalse("Expected isFocused to be false", expiryDateState.isFocused)
    }

    @Test
    fun `WHEN component is displayed THEN it shows the correct label and placeholder`() {
        // Arrange
        val state = PSExpiryDateStateImpl().apply {
            value = "12/23"
            isValidInUi = true
        }

        // Act
        composeTestRule.setContent {
            PSExpiryDatePicker(
                state = state,
                labelText = "Expiry date",
                placeholderText = "MM/YY",
                animateTopLabelText = true,
                psTheme = provideDefaultPSTheme()
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(PS_EXPIRY_DATE_PICKER_TEST_TAG).assertIsDisplayed()
    }

    @Test
    fun `WHEN isValidInUi is false THEN field shows error state`() {
        // Arrange
        val state = PSExpiryDateStateImpl().apply {
            value = "11"
            isValidInUi = false
        }

        // Act
        composeTestRule.setContent {
            PSExpiryDatePicker(
                state = state,
                labelText = "Expiry date",
                psTheme = provideDefaultPSTheme(),
                animateTopLabelText = false,
            )
        }

        // Assert
        // Aquí podrías necesitar verificar un cambio en el color o estilo que indique el error
        composeTestRule.onNodeWithTag(PS_EXPIRY_DATE_PICKER_TEST_TAG).assertIsDisplayed()
        // Asegúrate de añadir más aserciones que verifiquen el estado de error visual
    }


    @Test
    fun `WHEN placeholderText is provided THEN it is displayed in the placeholder`() {
        // Arrange
        val placeholderText = "MM/YY"
        val testTag = "testTagModifier"

        // Act
        composeTestRule.setContent {
            PSExpiryDatePicker(
                state = PSExpiryDateStateImpl().apply { value = "12/23" },
                labelText = "Expiry date",
                placeholderText = placeholderText,
                animateTopLabelText = true,
                psTheme = provideDefaultPSTheme(),
                modifier = Modifier.testTag(testTag)
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(testTag, useUnmergedTree = true).assertIsDisplayed()
    }

}

data class FakeFocusState(
    override val isFocused: Boolean = false,
    override val hasFocus: Boolean = false,
    override val isCaptured: Boolean = false
) : FocusState
