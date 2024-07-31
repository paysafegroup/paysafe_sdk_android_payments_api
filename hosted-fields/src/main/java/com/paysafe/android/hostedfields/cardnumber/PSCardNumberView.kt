/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.cardnumber

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import com.paysafe.android.hostedfields.R
import com.paysafe.android.hostedfields.model.CardNumberSeparator
import com.paysafe.android.hostedfields.model.DefaultPSCardFieldEventHandler
import com.paysafe.android.hostedfields.model.PSCardNumberStateImpl
import com.paysafe.android.hostedfields.valid.CardNumberChecks
import com.paysafe.android.hostedfields.view.PSCardView
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType

/**
 * Wrapper class to use [PSCardNumberField] in XML layouts, it's mandatory to inherit from
 * [AbstractComposeView].
 */
class PSCardNumberView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : PSCardView(context, attrs, defStyleAttr) {

    private val creditCardNumberState = mutableStateOf(PSCardNumberStateImpl())
    private val _isValidLiveData = MutableLiveData(false)
    private val _cardTypeLiveData = MutableLiveData(PSCreditCardType.UNKNOWN)
    private val hintString = provideHint(attrs)
    private val animateTopPlaceholderLabel = provideAnimateTopPlaceholderLabel(attrs)
    private val separator = provideSeparator(attrs)

    @get:JvmSynthetic
    internal val data: String
        get() = creditCardNumberState.value.value

    internal val cardTypeLiveData: LiveData<PSCreditCardType> =
        _cardTypeLiveData.distinctUntilChanged()

    val isValidLiveData: LiveData<Boolean> get() = _isValidLiveData

    override fun isEmpty() = data.isEmpty()
    override fun isValid() = CardNumberChecks.validations(data)

    override val placeholderString: String = resources.getString(R.string.card_number_placeholder)

    override fun reset() {
        creditCardNumberState.value = PSCardNumberStateImpl()
        clearFocus()
    }

    @Composable
    override fun Content() = PSCardNumberField(
        cardNumberState = creditCardNumberState.value,
        cardNumberModifier = PSCardNumberModifier(
            modifier = Modifier.fillMaxWidth(),
            cardBrandModifier = Modifier.padding(end = 16.dp)
        ),
        labelText = placeholderString,
        placeholderText = hintString,
        animateTopLabelText = animateTopPlaceholderLabel,
        cardNumberLiveData = PSCardNumberLiveData(
            cardTypeLiveData = _cardTypeLiveData,
            isValidLiveData = _isValidLiveData
        ),
        psTheme = psTheme,
        separator = separator,
        eventHandler = eventHandler ?: DefaultPSCardFieldEventHandler(_isValidLiveData)
    )

    private fun provideSeparator(attrs: AttributeSet?): CardNumberSeparator {
        val styledAttributes = context.theme.obtainStyledAttributes(
            /* set = */ attrs,
            /* attrs = */ R.styleable.PSCardNumberView,
            /* defStyleAttr = */ 0,
            /* defStyleRes = */ 0
        )
        val enumValue = styledAttributes.getInt(R.styleable.PSCardNumberView_psSeparator, 0)
        try {
            return CardNumberSeparator[enumValue]
        } finally {
            styledAttributes.recycle()
        }
    }
}