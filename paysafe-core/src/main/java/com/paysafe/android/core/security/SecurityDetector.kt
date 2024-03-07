/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.security

internal interface SecurityDetector {

    fun isEmulator(): Boolean

    fun isRootedDevice(): Boolean

}