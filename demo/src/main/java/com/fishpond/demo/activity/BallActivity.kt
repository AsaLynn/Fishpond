package com.fishpond.demo.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.hardware.SensorManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Window
import android.view.WindowManager
import com.water.ball.WaterBallView

/**
 * Ball
 * BallActivity.
 */
class BallActivity : Activity() {

    companion object {
        @JvmStatic
        fun jumpTo(context: Context) {
            context.startActivity(Intent(context, BallActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //没有titlebar，全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE) //隐藏标题
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        //设置全屏
        //保持屏幕竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val myManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val mymeMetrics = DisplayMetrics()
        myManager.defaultDisplay.getMetrics(mymeMetrics)
        val width = mymeMetrics.widthPixels
        val height = mymeMetrics.heightPixels - 125
        val myview = WaterBallView(
            this,
            sensorManager,
            width,
            height
        )
        setContentView(myview)
    }
}