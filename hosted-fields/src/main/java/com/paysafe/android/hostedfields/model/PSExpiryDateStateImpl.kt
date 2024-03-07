/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue
import com.paysafe.android.hostedfields.util.EXPIRY_DATE_ALREADY_SHOWN_INDEX
import com.paysafe.android.hostedfields.util.EXPIRY_DATE_PICKER_OPEN_INDEX
import com.paysafe.android.hostedfields.util.EXPIRY_DATE_VALID_INDEX
import com.paysafe.android.hostedfields.util.EXPIRY_DATE_VALUE_INDEX
import com.paysafe.android.hostedfields.valid.ExpiryDateChecks

class PSExpiryDateStateImpl(
    value: String = "",
    isValidInUi: Boolean = true,
    isPickerOpen: Boolean = false,
    alreadyShown: Boolean = false
) : PSExpiryDateState {

    override var value: String by mutableStateOf(value)
    override var isValidInUi: Boolean by mutableStateOf(isValidInUi)
    override var isPickerOpen: Boolean by mutableStateOf(isPickerOpen)
    override var alreadyShown: Boolean by mutableStateOf(alreadyShown)

    override fun isEmpty() = value.isEmpty()
    override fun isValid() = ExpiryDateChecks.validations(value)

    companion object {
        val Saver = Saver<PSExpiryDateStateImpl, List<Any>>(
            save = {
                listOf(
                    it.value,
                    it.isValidInUi,
                    it.isPickerOpen,
                    it.alreadyShown
                )
            },
            restore = {
                PSExpiryDateStateImpl(
                    it[EXPIRY_DATE_VALUE_INDEX] as String,
                    it[EXPIRY_DATE_VALID_INDEX] as Boolean,
                    it[EXPIRY_DATE_PICKER_OPEN_INDEX] as Boolean,
                    it[EXPIRY_DATE_ALREADY_SHOWN_INDEX] as Boolean
                )
            }
        )
    }

}