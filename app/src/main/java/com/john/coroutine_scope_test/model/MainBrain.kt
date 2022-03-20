package com.john.coroutine_scope_test.model

import com.john.coroutine_scope_test.common.sdk.CoroutineScopeSDK
import com.john.coroutine_scope_test.type.BtnTagEnum
import kotlinx.coroutines.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.LongUnaryOperator

class MainBrain {
    private val mCoroutineScopeSDK = CoroutineScopeSDK()
    private var mJob: Job? = null
    private var mListener: (() -> Unit)? = null
    /**
     * 測試時，有些Btn裡面有兩種測試
     * 不要一次打開
     * 分開測試，方能知道運行狀況
     * @since V1.0
     * @author John
     */
    fun processCallbackEvent(tag: String, listener: () -> Unit) {
        mListener = listener
        when(tag) {
            BtnTagEnum.BlockingBtn.name -> {
                /**
                 * 出現問題: Inappropriate blocking method call
                 * 原因這樣使用，雖然可以正常使用，但卻讓CoroutineScope失去了意義
                 * simulationBlocking搭配simulationBlocking2測試
                 * 再看simulationWithContext搭配simulationWithContext2的測試
                 * 他會堵塞CoroutineScope並失去暫停當前線程的功能
                 * 2022/03/20 05:05:24.686: A1
                 * 2022/03/20 05:05:25.692: A2 11
                 * 2022/03/20 05:05:25.694: B1
                 * 2022/03/20 05:05:26.696: B2 22
                 */
                simulationBlocking()
                simulationBlocking2()
                /**
                 * runBlocking建議搭配async使用，因為async是併發，就算一條線被堵塞，也不會有什麼影響
                 * withContext只是切換線程，把當前的暫停，但依然只有一條線在跑，並不是併發
                 * 如果使用runBlocking將會把所有線程賭塞
                 * 2022/03/20 05:31:10.833: B1
                 * 2022/03/20 05:31:11.836: B2
                 * 2022/03/20 05:31:12.840: result -> 77
                 * */
                //simulationBlocking3()
            }
            BtnTagEnum.WithContextBtn.name -> {
                /**
                 * 很明顯看到擁有Coroutine Scope暫停的功能
                 * 2022/03/20 05:05:40.129: B1
                 * 2022/03/20 05:05:40.129: A1
                 * 2022/03/20 05:05:41.131: A2 20
                 * 2022/03/20 05:05:41.133: B2 10
                 * */
                simulationWithContext()
                simulationWithContext2()
            }
            BtnTagEnum.AsyncBtn.name -> {
                /**
                 * 很明顯看出他是併發
                 * 但通常async會搭配await取得他返回值
                 * 可以同時有n個async在使用await
                 * 等待他們完成後，做處理
                 * 參考simulationASYNC2
                 * -----經過此處A-----
                 * -----經過此處B-----
                 * 2022/03/20 05:14:51.139: D1
                 * 2022/03/20 05:14:51.143: D2
                 * 2022/03/20 05:14:51.647: DD1
                 * 2022/03/20 05:14:51.649: DD2
                 * */
                simulationASYNC()
                /**
                 * async屬於併發
                 * async擁有await的功能
                 * 等待async內部完成再繼續往下做
                 * delay是暫停當前線程，因此其他線程依然可以繼續動作
                 * 使用兩個async並將結果相加
                 * 2022/03/20 05:06:00.061: C1
                 * 2022/03/20 05:06:00.063: CC1
                 * 2022/03/20 05:06:00.565: CCC1 -> 50
                 * */
                //simulationASYNC2()
            }
        }
    }

    fun release() {
        mJob?.cancel()
    }

    private fun showCurSystemTime(str: String) {
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS", Locale.US)
        val date = Date(System.currentTimeMillis())
        println("${sdf.format(date)}: $str")
    }

    private fun simulationBlocking() {
        mJob = mCoroutineScopeSDK.coroutineScopeLaunch({
            val result = runBlocking(Dispatchers.IO) {
                showCurSystemTime("A1")
                delay(1000)
                11
            }
            showCurSystemTime("A2 $result")
        }, {

        })
    }

    private fun simulationBlocking2() {
        mJob = mCoroutineScopeSDK.coroutineScopeLaunch({
            val result = runBlocking(Dispatchers.IO) {
                showCurSystemTime("B1")
                delay(1000)
                22
            }
            showCurSystemTime("B2 $result")
        }, {

        })
    }

    private fun simulationBlocking3() {
        mJob = mCoroutineScopeSDK.coroutineScopeLaunch({
            val async = async(Dispatchers.IO) {
                val result = runBlocking {
                    showCurSystemTime("B1")
                    delay(1000)
                    33
                }
                val result2 = runBlocking {
                    showCurSystemTime("B2")
                    delay(1000)
                    44
                }
                result + result2
            }
            showCurSystemTime("result -> ${async.await()}")
        }, {

        })
    }

    private fun simulationWithContext() {
        mJob = mCoroutineScopeSDK.coroutineScopeLaunch({
            val result = withContext(Dispatchers.IO) {
                showCurSystemTime("A1")
                delay(1000)
                20
            }
            showCurSystemTime("A2 $result")
        }, {

        })
    }

    private fun simulationWithContext2() {
        mJob = mCoroutineScopeSDK.coroutineScopeLaunch({
            val result = withContext(Dispatchers.IO) {
                showCurSystemTime("B1")
                delay(1000)
                10
            }
            showCurSystemTime("B2 $result")
        }, {

        })
    }


    private fun simulationASYNC() {
        mJob = mCoroutineScopeSDK.coroutineScopeLaunch({
            async {
                showCurSystemTime("D1")
                delay(500)
                showCurSystemTime("DD1")
                30
            }
            println("-----經過此處A----- ")
            async {
                showCurSystemTime("D2")
                delay(500)
                showCurSystemTime("DD2")
                30
            }
            println("-----經過此處B----- ")
        }, {

        })
    }

    private fun simulationASYNC2() {
        mJob = mCoroutineScopeSDK.coroutineScopeLaunch({
            val async = async {
                showCurSystemTime("C1")
                delay(10)
                20
            }
            val async2 = async {
                showCurSystemTime("CC1")
                delay(500)
                30
            }
            showCurSystemTime("CCC1 -> ${async.await()+async2.await()}")
        }, {

        })
    }
}