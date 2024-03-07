/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.cvv

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
import com.paysafe.android.hostedfields.model.PSCvvStateImpl
import com.paysafe.android.hostedfields.valid.CvvChecks
import com.paysafe.android.hostedfields.view.PSCardView
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType

/**
 * Wrapper class to use [PSCvvField] in XML layouts, it's mandatory to inherit from
 * [AbstractComposeView].
 */
class PSCvvView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : PSCardView(context, attrs, defStyleAttr) {

    private val cvvState = mutableStateOf(PSCvvStateImpl())
    private val _isValidLiveData = MutableLiveData(false)
    private val hintString = provideHint(attrs)
    private val isMasked = provideIsMasked(attrs)
    
    internal val data: String
        get() = cvvState.value.value

    val isValidLiveData: LiveData<Boolean> get() = _isValidLiveData

    /** Property to set or get the PSCreditCardType of this field. */
    var cardType: PSCreditCardType
        get() = cvvState.value.cardType
        set(value) {
            cvvState.value.cardType = value
        }

    override fun isEmpty() = data.isEmpty()
    override fun isValid() = CvvChecks.validations(data, cvvState.value.cardType)

    override val placeholderString: String = resources.getString(R.string.card_cvv_placeholder)

    override fun reset() {
        val previousCardType = cvvState.value.cardType
        cvvState.value = PSCvvStateImpl(type = previousCardType)
        clearFocus()
    }

    @Composable
    override fun Content() = PSCvvField(
        cvvState = cvvState.value,
        modifier = Modifier.fillMaxWidth(),
        labelText = placeholderString,
        placeholderText = hintString,
        psTheme = psTheme,
        isMasked = isMasked,
        isValidLiveData = _isValidLiveData,
        onEvent = onEvent
    )

    private fun provideIsMasked(attrs: AttributeSet?): Boolean {
        val styledAttributes = context.theme.obtainStyledAttributes(
            /* set = */ attrs,
            /* attrs = */ R.styleable.PSCvvView,
            /* defStyleAttr = */ 0,
            /* defStyleRes = */ 0
        )
        try {
            return styledAttributes.getBoolean(R.styleable.PSCvvView_psIsMasked, false)
        } finally {
            styledAttributes.recycle()
        }
    }

}