/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.valid

import com.paysafe.android.hostedfields.model.PSCardFieldInputEvent
import java.util.Calendar

internal class ExpiryDateChecks {

    companion object {
        const val MONTH_YEAR_SEPARATOR = " / "
        const val TWO_DIGIT_THOUSAND_BASE = "20"

        private const val JANUARY = 1
        private const val DECEMBER = 12
        private const val INDEX_IS_NOT_RELEVANT = -1

        const val START_DATE_INDEX = 0
        const val HALF_DATE_INDEX = 2
        const val MAX_CHARS_FOR_EXPIRY_DATE = 4
        const val START_YEAR_INDEX_AFTER_SEPARATOR = HALF_DATE_INDEX + MONTH_YEAR_SEPARATOR.length

        private val todayCalendar = Calendar.getInstance()

        internal fun inputProtection(
            newExpiryDate: String,
            onEvent: ((PSCardFieldInputEvent) -> Unit)? = null
        ) = newExpiryDate.filter { char ->
            val isCharValid = char.isDigit()
            if (!isCharValid) onEvent?.invoke(PSCardFieldInputEvent.INVALID_CHARACTER)
            isCharValid
        }.take(MAX_CHARS_FOR_EXPIRY_DATE).map { charInDate ->
            charInDate to INDEX_IS_NOT_RELEVANT
        }.fold("") { accumulator, (char, _) ->
            if (accumulator == "000") {
                accumulator
            } else {
                accumulator + char
            }
        }.take(MAX_CHARS_FOR_EXPIRY_DATE)

        private fun monthYearDigitsValidations(month: Int?, year: Int?) = month == null
                || year == null
                || month !in JANUARY..DECEMBER

        private fun isAfterCurrentDate(month: Int, twoDigitYear: Int, current: Calendar): Boolean {
            val inputYear = 2000 + twoDigitYear
            val currentYear = current[Calendar.YEAR]
            val currentMonth = current[Calendar.MONTH] + 1

            if (inputYear > currentYear) {
                return true
            } else if (inputYear == currentYear) {
                return month >= currentMonth
            }
            return false
        }

        internal fun validations(expiryDateText: String, today: Calendar = todayCalendar): Boolean {
            if (expiryDateText.trim().length != MAX_CHARS_FOR_EXPIRY_DATE) return false

            val month = expiryDateText.substring(START_DATE_INDEX, HALF_DATE_INDEX).toIntOrNull()
            val year = expiryDateText.substring(HALF_DATE_INDEX).toIntOrNull()
            if (monthYearDigitsValidations(month, year)) return false

            return isAfterCurrentDate(month!!, year!!, today)
        }
    }

}