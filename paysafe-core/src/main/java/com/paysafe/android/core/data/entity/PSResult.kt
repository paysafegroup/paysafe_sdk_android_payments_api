/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.data.entity


@JvmSynthetic
fun <T> resultAsCallback(result: PSResult<T>, resultCallback: PSResultCallback<T>) =
    when (result) {
        is PSResult.Success -> resultCallback.onSuccess(result.value)
        is PSResult.Failure -> resultCallback.onFailure(result.exception)
    }

/**
 * Callback for all Paysafe asynchronous SDK operations.
 */

interface PSResultCallback<T : Any?> {
    /**
     * Executed if asynchronous operation was successful.
     *
     * @param value Response value returned in the request.
     */
    fun onSuccess(value: T?)

    /**
     * Executed if asynchronous operation failed.
     *
     * @property exception Exception thrown by asynchronous operation.
     */
    fun onFailure(exception: Exception)
}

interface PSCallback<T> {
    /**
     * Executed if asynchronous operation was successful.
     *
     * @param value Response value returned in the request.
     */
    fun onSuccess(value: T)

    /**
     * Executed if asynchronous operation failed.
     *
     * @property exception Exception thrown by asynchronous operation.
     */
    fun onFailure(exception: Exception)
}

/**
 * Asynchronous call wrapper for all Paysafe async SDK operations.
 */
sealed class PSResult<out T : Any?> {
    /**
     * Wrapper used if a Paysafe asynchronous operation was successful.
     *
     * @property value Response value returned in the request.
     */
    class Success<T>(val value: T? = null) : PSResult<T>()

    /**
     * Wrapper used if a Paysafe asynchronous operation failed.
     *
     * @property exception Exception thrown by asynchronous operation.
     * @property reason Detailed information for the error.
     */
    class Failure(val exception: Exception, val reason: String? = null) : PSResult<Nothing>()
}

/**
 * Extension function to return the value inside PSResult.Success or throw the exception inside PSResult.Failure
 * @return T
 * @throws Exception
 */
@Throws(Exception::class)
inline fun <reified T> PSResult<T>.value(): T? =
    when (this) {
        is PSResult.Success -> value
        is PSResult.Failure -> throw exception
    }