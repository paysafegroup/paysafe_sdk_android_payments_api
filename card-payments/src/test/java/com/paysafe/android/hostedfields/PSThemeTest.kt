/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields

import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class PSThemeTest {

    @Test
    fun `IF context is provided THEN provideDefaultPSTheme RETURNS not null data`() {
        // Arrange
        val input = RuntimeEnvironment.getApplication().baseContext

        // Act
        val output = provideDefaultPSTheme(input)

        // Assert
        assertNotNull(output.backgroundColor)
        assertNotNull(output.borderColor)
        assertNotNull(output.focusedBorderColor)
        assertNotNull(output.borderCornerRadius)
        assertNotNull(output.errorColor)
        assertNotNull(output.textInputColor)
        assertNotNull(output.textInputFontSize)
        assertNotNull(output.placeholderColor)
        assertNotNull(output.placeholderFontSize)
        assertNotNull(output.hintColor)
        assertNotNull(output.hintFontSize)
        assertNotNull(output.expiryPickerButtonBackgroundColor)
        assertNotNull(output.expiryPickerButtonTextColor)
    }

}