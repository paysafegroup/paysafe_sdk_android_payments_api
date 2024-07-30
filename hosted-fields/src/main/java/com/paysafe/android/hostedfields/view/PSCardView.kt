/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.AbstractComposeView
import com.paysafe.android.hostedfields.PSTheme
import com.paysafe.android.hostedfields.R
import com.paysafe.android.hostedfields.model.PSCardFieldEventHandler
import com.paysafe.android.hostedfields.model.PSCardFieldInputEvent
import com.paysafe.android.hostedfields.provideDefaultPSTheme

private const val UNDEFINED_COLOUR: Int = -1_234_567_890
private const val UNDEFINED_FONT: Int = -1
private const val UNDEFINED_DIMENSION: Float = 1_234_567F

abstract class PSCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AbstractComposeView(context, attrs, defStyleAttr) {
    abstract fun isEmpty(): Boolean
    abstract fun isValid(): Boolean

    abstract fun reset()

    fun resetPSTheme() {
        psTheme = provideDefaultPSTheme(context)
    }

    protected fun provideHint(attrs: AttributeSet?): String? {
        val styledAttributes = context.theme.obtainStyledAttributes(
            /* set = */ attrs,
            /* attrs = */ R.styleable.PSCardView,
            /* defStyleAttr = */ 0,
            /* defStyleRes = */ 0
        )
        try {
            return styledAttributes.getString(R.styleable.PSCardView_psHint)
        } finally {
            styledAttributes.recycle()
        }
    }

    protected fun provideAnimateTopPlaceholderLabel(attrs: AttributeSet?): Boolean {
        val styledAttributes = context.theme.obtainStyledAttributes(
            /* set = */ attrs,
            /* attrs = */ R.styleable.PSCardView,
            /* defStyleAttr = */ 0,
            /* defStyleRes = */ 0
        )
        try {
            return styledAttributes.getBoolean(
                /* index = */ R.styleable.PSCardView_psAnimateTopPlaceholderLabel,
                /* defValue = */ true
            )
        } finally {
            styledAttributes.recycle()
        }
    }

    abstract val placeholderString: String

    open var eventHandler: PSCardFieldEventHandler? = null
    open var onEvent: ((PSCardFieldInputEvent) -> Unit)? = null

    var psTheme: PSTheme
        get() = mutablePSTheme.value
        set(value) {
            mutablePSTheme.value = value
        }

    private val attributesPSTheme: PSTheme = providePSTheme(attrs)
    private val mutablePSTheme = mutableStateOf(attributesPSTheme)

    private fun providePSTheme(attrs: AttributeSet?): PSTheme {
        val styledAttributes = context.theme.obtainStyledAttributes(
            /* set = */ attrs,
            /* attrs = */ R.styleable.PSCardView,
            /* defStyleAttr = */ 0,
            /* defStyleRes = */ R.style.PSTheme
        )

        val theme: PSTheme

        try {
            val styledAttributesPSTheme = createPSThemeFromStyledAttributes(styledAttributes)
            theme = if (styledAttributesPSTheme.canBeUsed())
                styledAttributesPSTheme
            else
                provideDefaultPSTheme(context)
        } finally {
            styledAttributes.recycle()
        }

        return theme
    }

    private fun PSTheme.canBeUsed(): Boolean =
        isViewAndPlaceholderDefined() &&
                isViewBorderDefined() &&
                isTextInputAndHintDefined() &&
                isExpiryPickerDefined()

    private fun PSTheme.isViewAndPlaceholderDefined(): Boolean =
        backgroundColor != UNDEFINED_COLOUR &&
                errorColor != UNDEFINED_COLOUR &&
                placeholderColor != UNDEFINED_COLOUR &&
                placeholderFontSize != UNDEFINED_DIMENSION

    private fun PSTheme.isViewBorderDefined(): Boolean =
        borderColor != UNDEFINED_COLOUR &&
                focusedBorderColor != UNDEFINED_COLOUR &&
                borderCornerRadius != UNDEFINED_DIMENSION

    private fun PSTheme.isTextInputAndHintDefined(): Boolean =
        textInputColor != UNDEFINED_COLOUR &&
                textInputFontSize != UNDEFINED_DIMENSION &&
                hintColor != UNDEFINED_COLOUR &&
                hintFontSize != UNDEFINED_DIMENSION


    private fun PSTheme.isExpiryPickerDefined(): Boolean =
        expiryPickerButtonBackgroundColor != UNDEFINED_COLOUR &&
                expiryPickerButtonTextColor != UNDEFINED_COLOUR

    private fun createPSThemeFromStyledAttributes(styledAttributes: TypedArray): PSTheme {
        val theme = styledAttributes.toPSTheme()

        val textInputFontFamily = if (theme.textInputFontFamily == UNDEFINED_FONT)
            null
        else
            theme.textInputFontFamily
        val placeholderFontFamily = if (theme.placeholderFontFamily == UNDEFINED_FONT)
            null
        else
            theme.placeholderFontFamily
        val hintFontFamily = if (theme.hintFontFamily == UNDEFINED_FONT)
            null
        else
            theme.hintFontFamily

        return theme.copy(
            textInputFontFamily = textInputFontFamily,
            placeholderFontFamily = placeholderFontFamily,
            hintFontFamily = hintFontFamily
        )
    }

    private fun TypedArray.toPSTheme() = PSTheme(
        backgroundColor = getColor(R.styleable.PSCardView_psBackgroundColor, UNDEFINED_COLOUR),
        borderColor = getColor(R.styleable.PSCardView_psBorderColor, UNDEFINED_COLOUR),
        focusedBorderColor = getColor(
            R.styleable.PSCardView_psFocusedBorderColor,
            UNDEFINED_COLOUR
        ),
        borderCornerRadius = getDimension(
            R.styleable.PSCardView_psBorderCornerRadius,
            UNDEFINED_DIMENSION
        ),
        errorColor = getColor(R.styleable.PSCardView_psErrorColor, UNDEFINED_COLOUR),
        textInputColor = getColor(R.styleable.PSCardView_psTextInputColor, UNDEFINED_COLOUR),
        textInputFontSize = getDimension(
            R.styleable.PSCardView_psTextInputFontSize,
            UNDEFINED_DIMENSION
        ),
        textInputFontFamily = getResourceId(
            R.styleable.PSCardView_psTextInputFontFamily,
            UNDEFINED_FONT
        ),
        placeholderColor = getColor(R.styleable.PSCardView_psPlaceholderColor, UNDEFINED_COLOUR),
        placeholderFontSize = getDimension(
            R.styleable.PSCardView_psPlaceholderFontSize,
            UNDEFINED_DIMENSION
        ),
        placeholderFontFamily = getResourceId(
            R.styleable.PSCardView_psPlaceholderFontFamily,
            UNDEFINED_FONT
        ),
        hintColor = getColor(R.styleable.PSCardView_psHintColor, UNDEFINED_COLOUR),
        hintFontSize = getDimension(R.styleable.PSCardView_psHintFontSize, UNDEFINED_DIMENSION),
        hintFontFamily = getResourceId(R.styleable.PSCardView_psHintFontFamily, UNDEFINED_FONT),
        expiryPickerButtonBackgroundColor = getColor(
            R.styleable.PSCardView_psExpiryPickerButtonBackgroundColor,
            UNDEFINED_COLOUR
        ),
        expiryPickerButtonTextColor = getColor(
            R.styleable.PSCardView_psExpiryPickerButtonTextColor,
            UNDEFINED_COLOUR
        )
    )
}