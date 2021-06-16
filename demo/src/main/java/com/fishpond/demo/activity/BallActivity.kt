package com.fishpond.demo.activity

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import com.fishpond.demo.R
import com.fishpond.demo.base.MyBaseActivity
import com.fishpond.demo.databinding.ActivityBallBinding
import com.fishpond.demo.fragment.BlankAFragment

/**
 * Ball
 * BallActivity.
 */
class BallActivity : MyBaseActivity() {

    companion object {
        @JvmStatic
        fun jumpTo(context: Context) {
            context.startActivity(Intent(context, BallActivity::class.java))
        }

        private const val TAG = "BallActivity"
    }

    private val mBinding by lazy {
        ActivityBallBinding.inflate(layoutInflater)
    }

    override fun onCreateRootView(): View = mBinding.root

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //全屏显示
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

        val view = WaterBallView(
            *//*width,
            height,*//*
            this
        )
        setContentView(view)
        view.start()
    }*/

    private val frag by lazy {
        BlankAFragment.newInstance()
    }

    override fun onInitView() {

        supportFragmentManager.beginTransaction()
            .replace(R.id.flContainer, frag)
            .commitAllowingStateLoss()

        mBinding.run {


            btnBegin.setOnClickListener {
                Log.i(TAG, "btnBegin: ")
                /*supportFragmentManager.beginTransaction()
                    .replace(R.id.flContainer, frag)
                    .commitAllowingStateLoss()*/
            }

            btnStop.setOnClickListener {
                Log.i(TAG, "btnStop: ")
                /*supportFragmentManager.beginTransaction()
                    .remove(frag)
                    .commitAllowingStateLoss()*/
            }

        }
    }


}