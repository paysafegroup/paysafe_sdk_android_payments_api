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
import com.paysafe.android.hostedfields.valid.ExpiryDateChecks
import com.paysafe.android.hostedfields.valid.ExpiryDateChecks.Companion.HALF_DATE_INDEX
import com.paysafe.android.hostedfields.valid.ExpiryDateChecks.Companion.TWO_DIGIT_THOUSAND_BASE
import com.paysafe.android.hostedfields.view.PSCardView

/**
 * Wrapper class to use [PSExpiryDateTextField] in XML layouts, it's mandatory to inherit from
 * [AbstractComposeView].
 */
class PSExpiryDateTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : PSCardView(context, attrs, defStyleAttr), PSExpiryDateView {

    private val expiryDateState = mutableStateOf(PSExpiryDateStateImpl())
    private val _isValidLiveData = MutableLiveData(false)
    private val hintString = provideHint(attrs)
    private val animateTopPlaceholderLabel = provideAnimateTopPlaceholderLabel(attrs)

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

    override fun isEmpty() = monthData.isEmpty() && yearData.isEmpty()
    override fun isValid() = ExpiryDateChecks.validations(expiryDateState.value.value)

    override val placeholderString: String =
        resources.getString(R.string.card_expiry_date_placeholder)

    override fun reset() {
        expiryDateState.value = PSExpiryDateStateImpl()
        clearFocus()
    }

    @Composable
    override fun Content() = PSExpiryDateTextField(
        expiryDateState = expiryDateState.value,
        modifier = Modifier.fillMaxWidth(),
        labelText = placeholderString,
        placeholderText = hintString,
        animateTopLabelText = animateTopPlaceholderLabel,
        isValidLiveData = _isValidLiveData,
        psTheme = psTheme,
        onEvent = onEvent
    )

}