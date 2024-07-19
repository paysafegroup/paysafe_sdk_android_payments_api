/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.expirydate

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paysafe.android.hostedfields.PSTheme
import com.paysafe.android.hostedfields.R
import com.paysafe.android.hostedfields.provideDefaultPSTheme
import com.paysafe.android.hostedfields.util.PREVIEW_BACKGROUND_COLOR
import com.paysafe.android.hostedfields.util.PS_MONTH_YEAR_PICKER_DIALOG_CONFIRM_TEST_TAG
import com.paysafe.android.hostedfields.util.PS_MONTH_YEAR_PICKER_DIALOG_TEST_TAG
import com.paysafe.android.hostedfields.util.pickerButtonColorsWithPSTheme
import com.paysafe.android.hostedfields.valid.ExpiryDateChecks.Companion.HALF_DATE_INDEX
import com.paysafe.android.hostedfields.valid.ExpiryDateChecks.Companion.MAX_CHARS_FOR_EXPIRY_DATE
import com.paysafe.android.hostedfields.valid.ExpiryDateChecks.Companion.MONTH_YEAR_SEPARATOR
import com.paysafe.android.hostedfields.valid.ExpiryDateChecks.Companion.START_DATE_INDEX
import com.paysafe.android.hostedfields.valid.ExpiryDateChecks.Companion.START_YEAR_INDEX_AFTER_SEPARATOR
import java.util.Calendar

/** Month placeholder letters to be displayed(no month selected) in the dialog title. */
private const val MONTH_MM = "MM"

/** Year placeholder letters to be displayed(no year selected) in the dialog title. */
private const val YEAR_YYYY = "YYYY"

/**
 * Extract month digits as text from month year input.
 *
 * @param monthYearFourDigits is raw text for month year digits, without extra characters.
 *
 * @return month as a text digits, if basic checks doesn't pass, an empty [String].
 */
private fun transformMonthFromInput(monthYearFourDigits: String): String {
    if (monthYearFourDigits.isBlank() || monthYearFourDigits.length < 2) return ""
    val month = monthYearFourDigits.substring(START_DATE_INDEX, HALF_DATE_INDEX)
    if (month == "00") return ""
    return month
}

/**
 * Extract year digits as text from month year input.
 *
 * @param monthYearFourDigits is raw text for month year digits, without extra characters.
 *
 * @return year as a text(4 digits), if basic checks doesn't pass, an empty [String].
 */
private fun transformYearFromInput(monthYearFourDigits: String): String {
    if (monthYearFourDigits.isBlank() || monthYearFourDigits.length != MAX_CHARS_FOR_EXPIRY_DATE)
        return ""
    monthYearFourDigits.substring(HALF_DATE_INDEX).toIntOrNull()?.run {
        return (2000 + this).toString()
    }
    return ""
}

/**
 * Organize month, year texts to be displayed at the top of the dialog.
 *
 * @param monthYearFourDigits is raw text for month year digits, without extra characters.
 *
 * @return month, year, and dialog title in MM / YYYY format.
 */
private fun transformMonthYearFromInput(
    monthYearFourDigits: String
): Triple<String, String, String> {
    val monthText = transformMonthFromInput(monthYearFourDigits)
    val yearText = transformYearFromInput(monthYearFourDigits)

    var monthYearText = if (monthText.isBlank()) {
        MONTH_MM
    } else {
        monthText
    }
    monthYearText += MONTH_YEAR_SEPARATOR
    monthYearText += if (yearText.isBlank()) {
        YEAR_YYYY
    } else {
        yearText
    }

    return Triple(monthText, yearText, monthYearText)
}

/**
 * After the expiry date is selected by the user; month and year data are transformed to be returned
 * to the input field. No special characters are included, because, the transformations handle the
 * expiry date in the text field.
 *
 * @param dateWithSeparator contains month, [MONTH_YEAR_SEPARATOR] and year, the complete format.
 *
 * @return month and year as text, without special characters.
 */
fun transformExpiryDateForOutput(dateWithSeparator: String): String {
    if (dateWithSeparator == "$MONTH_MM${MONTH_YEAR_SEPARATOR}$YEAR_YYYY") return ""
    val monthText = dateWithSeparator.substring(START_DATE_INDEX, HALF_DATE_INDEX)
    val month = monthText.toIntOrNull()
    val yearText = dateWithSeparator.substring(START_YEAR_INDEX_AFTER_SEPARATOR)
    val year = yearText.toIntOrNull()

    var output = ""
    if (month != null) {
        output = monthText
    }
    if (year != null) {
        if (month == null) {
            output += "00"
        }
        output += (year - 2000).toString()
    }
    return output
}

data class DialogButtons(
    val confirmText: String,
    val cancelText: String
)

val defaultMonthYearFontSize = 24.sp
val selectedMonthYearFontSize = 26.sp
val defaultMonthYearFontStyle = FontWeight.Normal
val selectedMonthYearFontStyle = FontWeight.Bold

/**
 * Dialog to choose month and year, implemented as a composable function.
 *
 * @param title The dialog's title displayed at the top.
 * @param inputMonthYear 4 digits text for month and year, after transformations the dialog UI is configured.
 * @param showDialog Composable flag to display the dialog, show it if it's true.
 * @param dialogButtons Dialog buttons label for positive & cancel actions.
 * @param onDialogConfirm Function to be executed when the positive action button is clicked.
 * @param onDialogCancel Function to be executed when the cancel action button is clicked.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PSMonthYearPickerDialog(
    title: String,
    inputMonthYear: String = "",
    showDialog: Boolean,
    psTheme: PSTheme,
    dialogButtons: DialogButtons = DialogButtons(
        confirmText = stringResource(id = R.string.expiry_date_dialog_confirm_button),
        cancelText = stringResource(id = R.string.expiry_date_dialog_cancel_button)
    ),
    onDialogConfirm: (String) -> Unit,
    onDialogCancel: () -> Unit
) {
    val monthYearInput = transformMonthYearFromInput(inputMonthYear)
    val selectedMonth = rememberSaveable { mutableStateOf(monthYearInput.first) }
    val selectedYear = rememberSaveable { mutableStateOf(monthYearInput.second) }
    val subtitleMonthYear = rememberSaveable { mutableStateOf(monthYearInput.third) }

    if (!showDialog) return
    AlertDialog(
        containerColor = Color(psTheme.backgroundColor),
        titleContentColor = Color(psTheme.placeholderColor),
        modifier = Modifier
            .semantics { testTagsAsResourceId = true }
            .testTag(PS_MONTH_YEAR_PICKER_DIALOG_TEST_TAG)
            .heightIn(max = 510.dp)
            .padding(32.dp),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = title, fontSize = 14.sp)
                Text(text = subtitleMonthYear.value, fontSize = 24.sp)
            }
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                MonthColumn(
                    psTheme = psTheme,
                    selectedMonth = selectedMonth,
                    subtitleMonthYear = subtitleMonthYear
                )
                YearColumn(
                    psTheme = psTheme,
                    selectedYear = selectedYear,
                    subtitleMonthYear = subtitleMonthYear
                )
            }
        },
        confirmButton = {
            Button(
                colors = pickerButtonColorsWithPSTheme(psTheme),
                onClick = { onDialogConfirm(transformExpiryDateForOutput(subtitleMonthYear.value)) },
                modifier = Modifier.testTag(PS_MONTH_YEAR_PICKER_DIALOG_CONFIRM_TEST_TAG)
            ) {
                Text(dialogButtons.confirmText)
            }
        },
        dismissButton = {
            Button(
                colors = pickerButtonColorsWithPSTheme(psTheme),
                onClick = { onDialogCancel() }
            ) {
                Text(dialogButtons.cancelText)
            }
        },
        onDismissRequest = { onDialogCancel() }
    )
}

@Composable
private fun RowScope.MonthColumn(
    psTheme: PSTheme,
    selectedMonth: MutableState<String>,
    subtitleMonthYear: MutableState<String>
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f)
    ) {
        val monthNumbers = (1..12).map { it.toString().padStart(2, '0') }
        items(items = monthNumbers) { monthNumber ->
            val isNewMonthEqualsToPrevious = selectedMonth.value == monthNumber
            Text(
                text = monthNumber,
                color = Color(if (isNewMonthEqualsToPrevious) psTheme.textInputColor else psTheme.placeholderColor),
                fontSize = if (isNewMonthEqualsToPrevious) selectedMonthYearFontSize else defaultMonthYearFontSize,
                fontWeight = if (isNewMonthEqualsToPrevious) selectedMonthYearFontStyle else defaultMonthYearFontStyle,
                modifier = Modifier.clickable {
                    if (selectedMonth.value == monthNumber) {
                        selectedMonth.value = ""
                        subtitleMonthYear.value = MONTH_MM + subtitleMonthYear.value.substring(2)
                    } else {
                        selectedMonth.value = monthNumber
                        subtitleMonthYear.value =
                            selectedMonth.value + subtitleMonthYear.value.substring(2)
                    }
                }
            )
        }
    }
}

@Composable
private fun RowScope.YearColumn(
    psTheme: PSTheme,
    selectedYear: MutableState<String>,
    subtitleMonthYear: MutableState<String>
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f)
    ) {
        val firstYearToShow = Calendar.getInstance()[Calendar.YEAR]
        val lastYearToShow = firstYearToShow + 20
        val years = (firstYearToShow..lastYearToShow).map { it.toString() }
        items(years) { year ->
            val isNewYearEqualsToPrevious = selectedYear.value == year
            Text(
                text = year,
                color = Color(if (isNewYearEqualsToPrevious) psTheme.textInputColor else psTheme.placeholderColor),
                fontSize = if (isNewYearEqualsToPrevious) selectedMonthYearFontSize else defaultMonthYearFontSize,
                fontWeight = if (isNewYearEqualsToPrevious) selectedMonthYearFontStyle else defaultMonthYearFontStyle,
                modifier = Modifier.clickable {
                    if (selectedYear.value == year) {
                        selectedYear.value = ""
                        subtitleMonthYear.value =
                            subtitleMonthYear.value.substring(0, 5) + YEAR_YYYY
                    } else {
                        selectedYear.value = year
                        subtitleMonthYear.value =
                            subtitleMonthYear.value.substring(0, 5) + selectedYear.value
                    }
                }
            )
        }
    }
}

@DialogPreview
@Composable
internal fun DefaultMonthYearPickerDialog() {
    PSMonthYearPickerDialog(
        title = "Select Expiry Date",
        showDialog = true,
        psTheme = provideDefaultPSTheme(),
        onDialogConfirm = {},
        onDialogCancel = {}
    )
}

@DialogPreview
@Composable
internal fun WithInputMonthYearPickerDialog() {
    PSMonthYearPickerDialog(
        title = "Select Expiry Date",
        inputMonthYear = "0225",
        showDialog = true,
        psTheme = provideDefaultPSTheme(),
        onDialogConfirm = {},
        onDialogCancel = {}
    )
}

@Preview(
    name = "Day",
    widthDp = 400,
    heightDp = 600,
    showBackground = true,
    backgroundColor = PREVIEW_BACKGROUND_COLOR
)
@Preview(
    name = "Night",
    widthDp = 400,
    heightDp = 600,
    showBackground = true,
    backgroundColor = PREVIEW_BACKGROUND_COLOR,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class DialogPreview