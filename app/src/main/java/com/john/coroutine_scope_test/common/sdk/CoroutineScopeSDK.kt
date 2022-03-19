package com.john.coroutine_scope_test.common.sdk

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CoroutineScopeSDK {
    fun coroutineScopeLaunch(
        block: suspend CoroutineScope.() -> Unit,
        errorCallback: (Throwable) -> Unit
    ) = coroutineScopeLaunch(Dispatchers.Main, block, errorCallback)

    fun coroutineScopeLaunch(
        context: CoroutineContext,
        block: suspend CoroutineScope.() -> Unit,
        errorCallback: (Throwable) -> Unit
    ): Job {
        val handler = CoroutineExceptionHandler { _, exception ->
            errorCallback(exception)
        }
        return CoroutineScope(context).launch(handler) {
            block()
        }
    }
}