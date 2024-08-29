package com.paysafe.android.hostedfields.util


import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.compose.ui.test.junit4.createComposeRule
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WrapperToAvoidPasteTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testShowMenu() {
        // Arrange
        val rect = Rect(0f, 0f, 100f, 100f) // Properly initialize the Rect object
        val onCopyRequested = mockk<() -> Unit>(relaxed = true)
        val onPasteRequested = mockk<() -> Unit>(relaxed = true)
        val onCutRequested = mockk<() -> Unit>(relaxed = true)
        val onSelectAllRequested = mockk<() -> Unit>(relaxed = true)

        // Act
        WrapperToAvoidPaste.showMenu(rect, onCopyRequested, onPasteRequested, onCutRequested, onSelectAllRequested)

        // Assert
        // Since it's a NOOP implementation, we just verify that it doesn't throw any exceptions
    }


    @Test
    fun testHide() {
        // Act
        WrapperToAvoidPaste.hide()

        // Assert
        // Since it's a NOOP implementation, we just verify that it doesn't throw any exceptions
    }

    @Test
    fun testStatus() {
        // Assert
        assertEquals(TextToolbarStatus.Hidden, WrapperToAvoidPaste.status)
    }
}