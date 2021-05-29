package com.fishpond.demo

import android.content.Context
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.fishpond.demo.databinding.ActivityMainBinding
import com.zxn.mvvm.ext.jumpInTo
import com.zxn.mvvm.view.BaseActivity
import com.zxn.tablayout.listener.CustomTabEntity
import com.zxn.tablayout.listener.OnTabSelectListener

/**
 * 主页
 *  Created by zxn on 2021/3/24.
 */
class MainActivity : BaseActivity() {

    companion object {

        @JvmStatic
        fun jumpTo(context: Context) {
            jumpInTo<MainActivity>(context)
        }
    }

    override val layoutResId: Int = R.layout.activity_main

    override val layoutRoot: View by lazy {
        mViewBinding.root
    }

    private val mViewBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun createObserver() {

    }

    private var tabIndex = 0 //默认为0

    override fun onInitView() {

        mViewBinding.ctlMenu.setTabData(
            mutableListOf<CustomTabEntity>(
                TabEntity("饮水", ""),
                TabEntity("健康", ""),
                TabEntity("我的", "")
            ),
            (this@MainActivity as FragmentActivity),
            R.id.flContainer,
            mutableListOf(
                SeaFragment.newInstance(),
                BlankAFragment.newInstance(),
                BlankBFragment.newInstance(),
            )
        )

        /**
         * 监听tab
         */
        mViewBinding.ctlMenu.setOnTabSelectListener(object : OnTabSelectListener {

            override fun onTabSelect(position: Int) {
                Log.d("MainActivity", "onTabSelect${position}")
                if (position == 1) {
                } else {
                    tabIndex = position
                }
            }

            override fun onTabReselect(position: Int) {
                //再次选择
                Log.d("MainActivity", "onTabReselect${position}")
            }
        })

    }


    override fun registerEventBus(isRegister: Boolean) {

    }

    fun currentTab(): Int = mViewBinding.ctlMenu.currentTab


}