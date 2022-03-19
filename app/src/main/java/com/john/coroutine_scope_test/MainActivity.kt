package com.john.coroutine_scope_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.john.coroutine_scope_test.model.MainBrain
import com.john.coroutine_scope_test.view.MainView

class MainActivity : AppCompatActivity() {
    //View
    private lateinit var mMainView: MainView
    //Model
    private val mainBrain = MainBrain()
    //ViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setContentView(mMainView.getRootView())
        setupView()
        initEvent()

    }

    override fun onStop() {
        super.onStop()
        mainBrain.release()
    }

    private fun initView() {
        mMainView = MainView(this)
    }

    private fun setupView() {
        mMainView.initView()
    }

    private fun initEvent() {
        mMainView.initEvent {
            mainBrain.processCallbackEvent(it)
        }
    }
}