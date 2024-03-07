/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.util

import android.util.Base64
import java.nio.charset.Charset

fun String.base64Decode(charset: Charset = Charsets.UTF_8) =
    String(Base64.decode(this, Base64.DEFAULT), charset)

fun String.base64Encode(charset: Charset = Charsets.UTF_8) =
    String(Base64.encode(this.toByteArray(charset), Base64.DEFAULT), charset)