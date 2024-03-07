/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@JvmSynthetic
fun CoroutineScope.launchCatching(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit
) = CatchResult(this, launch(context) {
    try {
        block()
    } catch (it: Throwable) {
        cancel(it.message ?: it.toString(), it)
    }
})

class CatchResult(
    private val scope: CoroutineScope,
    private val job: Job,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
) {
    fun onFailure(handler: (Exception) -> Unit) =
        job.invokeOnCompletion { throwable ->
            throwable?.cause?.let {
                scope.launch(mainDispatcher) {
                    handler(Exception(it.message))
                }
            }
        }
}
