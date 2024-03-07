/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.util

import android.content.Context
import android.util.TypedValue
import android.widget.Toast

fun Context.dpToPx(dp: Int): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dp.toFloat(),
    resources.displayMetrics
)

fun Context.longToast(textToDisplay: String) {
    Toast.makeText(this, textToDisplay, Toast.LENGTH_LONG).show()
}