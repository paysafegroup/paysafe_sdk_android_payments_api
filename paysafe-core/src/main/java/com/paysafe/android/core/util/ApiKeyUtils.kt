/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.util

private const val API_KEY_CONTENT_SIZE = 2
private const val API_KEY_CONTENT_DELIMITER = ':'

fun isApiKeyEmpty(apiKey: String): Boolean = apiKey.isBlank()

fun isApiKeyInvalid(apiKey: String): Boolean = getApiKeyContentList(apiKey) == null

fun getUserNameFromApiKey(apiKey: String): String? {
    val contentList = getApiKeyContentList(apiKey)
    return if (contentList.isNullOrEmpty())
        null
    else
        contentList.first()
}

private fun getApiKeyContentList(apiKey: String): List<String>? {
    val decodedApiKey: String
    try {
        decodedApiKey = apiKey.base64Decode()
    } catch (exception: IllegalArgumentException) {
        return null
    }
    val apiKeyContentList = decodedApiKey.split(
        API_KEY_CONTENT_DELIMITER,
        limit = API_KEY_CONTENT_SIZE
    )
    return apiKeyContentList.takeIf { list ->
        list.size == API_KEY_CONTENT_SIZE &&
                list.none { it.isBlank() }
    }
}
