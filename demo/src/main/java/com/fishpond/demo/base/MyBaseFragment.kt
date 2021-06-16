package com.fishpond.demo.base

import com.zxn.mvvm.view.BaseFragment

/**
 *  Created by zxn on 2021/6/16.
 **/
abstract class MyBaseFragment : BaseFragment() {

    override fun createObserver() {

    }

    override fun lazyLoadData() {

    }

    override fun onLoading(isLoading: Boolean) {

    }

    override fun registerEventBus(isRegister: Boolean) {

    }
}