package com.john.coroutine_scope_test.model

import com.john.coroutine_scope_test.common.sdk.CoroutineScopeSDK
import com.john.coroutine_scope_test.type.BtnTagEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.Exception

class MainBrain {
    private val mCoroutineScopeSDK = CoroutineScopeSDK()
    private var mJob: Job? = null
    fun processCallbackEvent(tag: String) {
        when(tag) {
            BtnTagEnum.Btn1.name -> {
                simulationCoroutineScope()
            }
        }
    }

    fun release() {
        mJob?.cancel()
    }

    private fun simulationCoroutineScope() {
        mJob = mCoroutineScopeSDK.coroutineScopeLaunch({
            testFun2()
        }, {
            when(it) {
                is IOException -> {
                    println("IOException $it")
                }
                is Exception -> {
                    println("Exception $it")
                }
            }
        })
    }

    private suspend fun testFun1() {
        withContext(Dispatchers.IO) {
            throw Exception("error1")
        }
    }

    private suspend fun testFun2() {
        withContext(Dispatchers.IO) {
            throw IOException("error2")
        }
    }
}