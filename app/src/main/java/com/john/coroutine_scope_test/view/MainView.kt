package com.john.coroutine_scope_test.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.john.coroutine_scope_test.databinding.ActivityMainBinding
import com.john.coroutine_scope_test.type.BtnTagEnum

class MainView(
    mActivity: AppCompatActivity,
    viewGroup: ViewGroup? = null,
    attachToParent: Boolean = false
): BaseView() {
    private val mBinding = ActivityMainBinding.inflate(LayoutInflater.from(mActivity), viewGroup, attachToParent)
    private var mListener: ((String) -> Unit)? = null
    override fun getRootView() = mBinding.root

    fun initView() {
        mBinding.btnBlockTest.tag = BtnTagEnum.BlockingBtn.name
        mBinding.btnAsyncTest.tag = BtnTagEnum.AsyncBtn.name
        mBinding.btnWithContextTest.tag = BtnTagEnum.WithContextBtn.name
    }

    fun initEvent(listener: (String) -> Unit) {
        this.mListener = listener
        mBinding.btnBlockTest.setOnClickListener { view ->
            mListener?.also { it(view.tag.toString()) }
        }
        mBinding.btnAsyncTest.setOnClickListener { view ->
            mListener?.also { it(view.tag.toString()) }
        }
        mBinding.btnWithContextTest.setOnClickListener { view ->
            mListener?.also { it(view.tag.toString()) }
        }
    }
}