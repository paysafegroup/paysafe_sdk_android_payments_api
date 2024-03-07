package com.paysafe.android.core.data.mapper

import com.paysafe.android.core.data.error.PSErrorSerializable
import com.paysafe.android.core.domain.model.PSError
import com.paysafe.android.core.util.tryParseInt

internal fun PSErrorSerializable.toDomain() = PSError(
    code = code?.tryParseInt() ?: 0,
    message = message ?: "",
    details = if (details.isNullOrEmpty()) listOf() else details
)