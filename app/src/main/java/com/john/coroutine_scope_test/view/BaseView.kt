package com.john.coroutine_scope_test.view

import android.content.Context
import android.view.View

abstract class BaseView {
    abstract fun getRootView(): View
    fun getContext(): Context = getRootView().context
}