/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.holdername

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
import com.paysafe.android.hostedfields.domain.model.PSCardholderNameStateImpl
import com.paysafe.android.hostedfields.model.DefaultPSCardFieldEventHandler
import com.paysafe.android.hostedfields.valid.CardholderNameChecks
import com.paysafe.android.hostedfields.view.PSCardView

/**
 * Wrapper class to use [PSCardholderNameField] in XML layouts, it's mandatory to inherit from
 * [AbstractComposeView].
 */
class PSCardholderNameView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : PSCardView(context, attrs, defStyleAttr) {

    private val cardHolderNameState = mutableStateOf(PSCardholderNameStateImpl())
    private val _isValidLiveData = MutableLiveData(false)
    private val hintString = provideHint(attrs)
    private val animateTopPlaceholderLabel = provideAnimateTopPlaceholderLabel(attrs)
    private val labelTextState = mutableStateOf(provideLabelText(attrs))

    val isValidLiveData: LiveData<Boolean> get() = _isValidLiveData

    @get:JvmSynthetic
    internal val data: String
        get() = cardHolderNameState.value.value

    /** Property to set or get the label text of this field. */
    var labelText: String
        get() = labelTextState.value
        set(value) {
            labelTextState.value = value
        }

    override fun isEmpty() = data.isEmpty()
    override fun isValid() = CardholderNameChecks.validations(data)

    override val placeholderString: String
        get() = labelTextState.value

    override fun reset() {
        cardHolderNameState.value = PSCardholderNameStateImpl()
        if (clearsFocusOnReset) clearFocus()
    }

    @Composable
    override fun Content() = PSCardholderNameField(
        holderNameState = cardHolderNameState.value,
        modifier = Modifier.fillMaxWidth(),
        labelText = placeholderString,
        placeholderText = hintString,
        animateTopLabelText = animateTopPlaceholderLabel,
        isValidLiveData = _isValidLiveData,
        psTheme = psTheme,
        eventHandler = eventHandler ?: DefaultPSCardFieldEventHandler(_isValidLiveData),
        clearsErrorOnInput = clearsErrorOnInput,
        validatesEmptyFieldOnBlur = validatesEmptyFieldOnBlur
    )

    private fun provideLabelText(attrs: AttributeSet?): String {
        val styledAttributes = context.theme.obtainStyledAttributes(
            /* set = */ attrs,
            /* attrs = */ R.styleable.PSCardholderNameView,
            /* defStyleAttr = */ 0,
            /* defStyleRes = */ 0
        )
        try {
            return styledAttributes.getString(R.styleable.PSCardholderNameView_label_text)
                ?: resources.getString(R.string.card_holder_name_placeholder)
        } finally {
            styledAttributes.recycle()
        }
    }
}
