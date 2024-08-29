package com.paysafe.android.hostedfields.model

import androidx.lifecycle.MutableLiveData
import com.paysafe.android.hostedfields.domain.model.PSCardFieldInputEvent

fun interface PSCardFieldEventHandler {
    fun handleEvent(event: PSCardFieldInputEvent)
}

class DefaultPSCardFieldEventHandler(
    private val isValidLiveData: MutableLiveData<Boolean>
) : PSCardFieldEventHandler {
    override fun handleEvent(event: PSCardFieldInputEvent) {
        when (event) {
            PSCardFieldInputEvent.FIELD_VALUE_CHANGE -> {
                // Action for FIELD VALUE CHANGE
            }
            PSCardFieldInputEvent.VALID -> {
                isValidLiveData.postValue(true)
            }
            PSCardFieldInputEvent.INVALID -> {
                isValidLiveData.postValue(false)
            }
            PSCardFieldInputEvent.FOCUS -> {
                // Action for FOCUS
            }
            PSCardFieldInputEvent.INVALID_CHARACTER -> {
                // Action for INVALID_CHARACTER if is needed
            }
        }
    }
}

