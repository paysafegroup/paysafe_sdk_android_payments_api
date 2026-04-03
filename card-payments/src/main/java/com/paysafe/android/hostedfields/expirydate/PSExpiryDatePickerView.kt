/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.expirydate

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.paysafe.android.hostedfields.R
import com.paysafe.android.hostedfields.domain.model.PSExpiryDateStateImpl
import com.paysafe.android.hostedfields.model.DefaultPSCardFieldEventHandler
import com.paysafe.android.hostedfields.model.PSCardFieldEventHandler
import com.paysafe.android.hostedfields.valid.ExpiryDateChecks
import com.paysafe.android.hostedfields.valid.ExpiryDateChecks.Companion.HALF_DATE_INDEX
import com.paysafe.android.hostedfields.valid.ExpiryDateChecks.Companion.TWO_DIGIT_THOUSAND_BASE
import com.paysafe.android.hostedfields.view.PSCardView

/**
 * Wrapper class to use [PSExpiryDatePickerField] in XML layouts, it's mandatory to inherit from
 * [AbstractComposeView].
 */
class PSExpiryDatePickerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : PSCardView(context, attrs, defStyleAttr), PSExpiryDateView {

    private val expiryDateState = mutableStateOf(PSExpiryDateStateImpl())
    private val _isValidLiveData = MutableLiveData(false)
    private val hintString = provideHint(attrs)
    private val animateTopPlaceholderLabel = provideAnimateTopPlaceholderLabel(attrs)
    private val labelTextState = mutableStateOf(provideLabelText(attrs))

    /** Property to enable the month data retrieval from expiry date. */
    override val monthData: String
        get() = expiryDateState.value.value.take(HALF_DATE_INDEX)

    /** Property to enable the year data retrieval from expiry date. */
    override val yearData: String
        get() = TWO_DIGIT_THOUSAND_BASE + expiryDateState.value.value.takeLast(HALF_DATE_INDEX)

    /** Read-only [LiveData] to check if expiry date is valid. */
    override val isValidLiveData: LiveData<Boolean> get() = _isValidLiveData
    override val viewContext: Context
        get() = context

    /** Property to set or get the label text of this field. */
    var labelText: String
        get() = labelTextState.value
        set(value) {
            labelTextState.value = value
        }

    override fun isEmpty() = monthData.isEmpty() && yearData.isEmpty()
    override fun isValid() = ExpiryDateChecks.validations(expiryDateState.value.value)

    override val placeholderString: String
        get() = labelTextState.value

    override fun reset() {
        expiryDateState.value = PSExpiryDateStateImpl()
        if (clearsFocusOnReset) clearFocus()
    }

    @Composable
    override fun Content() {
        val handler = eventHandler ?: onEvent?.let { callback ->
            PSCardFieldEventHandler { event -> callback(event) }
        } ?: DefaultPSCardFieldEventHandler(_isValidLiveData)

        PSExpiryDatePickerField(
            expiryDateState = expiryDateState.value,
            modifier = Modifier.fillMaxWidth(),
            labelText = placeholderString,
            placeholderText = hintString,
            animateTopLabelText = animateTopPlaceholderLabel,
            isValidLiveData = _isValidLiveData,
            psTheme = psTheme,
            eventHandler = handler,
            validatesEmptyFieldOnBlur = validatesEmptyFieldOnBlur
        )
    }

    private fun provideLabelText(attrs: AttributeSet?): String {
        val styledAttributes = context.theme.obtainStyledAttributes(
            /* set = */ attrs,
            /* attrs = */ R.styleable.PSExpiryDatePickerView,
            /* defStyleAttr = */ 0,
            /* defStyleRes = */ 0
        )
        try {
            return styledAttributes.getString(R.styleable.PSExpiryDatePickerView_label_text)
                ?: resources.getString(R.string.card_expiry_date_placeholder)
        } finally {
            styledAttributes.recycle()
        }
    }

}