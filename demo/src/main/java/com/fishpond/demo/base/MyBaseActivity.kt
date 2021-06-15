package com.fishpond.demo.base

import android.view.View
import com.zxn.mvvm.view.BaseActivity

/**
 * Created by zxn on 2021/5/30.
 */
abstract class MyBaseActivity :BaseActivity(){

    override val layoutResId: Int = 0

    override fun onCreateRootView(): View? = null

    override fun createObserver() {

    }

    override fun registerEventBus(isRegister: Boolean) {

    }

}