package com.water.ball

import android.content.Context
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.SoundPool
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.water.water.mysensor.R
import java.util.*

/**
 * WaterBallView
 * 跟随重力传感器移动的小水球
 */
class WaterBallView(context: Context, sensorManager: SensorManager, w: Int, h: Int) :
    SurfaceView(context), SurfaceHolder.Callback {

    private var ball: Bitmap? = null

    private var BACK: Bitmap? = null

    private var mWidth: Int = 0

    private var mHeight: Int = 0

    private var areaWidth = 0
    private var areaHeight = 0
    private var gx = 0f
    private var gy = 0f
    private var gz = 0f
    private var lastx = 0f
    private var lasty = 0f

    private var mX = 0f
    private var mY = 0f

    private var vx = 0f
    private var vy = 0f
    private var running = false
    var mycanvas: Canvas
    var mypaint: Paint

    private val mViewHolder: SurfaceHolder by lazy {
        this.holder.apply {
            mViewHolder.addCallback(this@WaterBallView)
        }
    }

    private val mSensorManager: SensorManager

    //声音池
    private val mSoundPool: SoundPool = SoundPool(2, AudioManager.STREAM_MUSIC, 0)

    override fun surfaceCreated(holder: SurfaceHolder) {
        running = true
        mX = (mWidth / 2).toFloat()
        mY = (mHeight / 2).toFloat()
        lastx = mX
        lasty = mY
        ball = BitmapFactory.decodeResource(this.resources, R.drawable.ball)
        BACK = BitmapFactory.decodeResource(this.resources, R.drawable.back)
        areaWidth = mWidth - ball?.width!!
        areaHeight = mHeight - ball?.height!!
        val myoritationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mSensorManager.registerListener(
            mySensorEventListener(),
            myoritationSensor,
            SensorManager.SENSOR_DELAY_GAME
        )
        mSoundPool.load(context, R.raw.bong, 2)
        mythread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        running = false
    }

    private inner class mySensorEventListener : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            gz = event.values[2]
            gx = event.values[0] - 0.75.toFloat() //定x坐标轴上的位置
            gy = event.values[1] - 0.105.toFloat() //定x坐标轴上的位置
            Log.d("gz", gz.toString())
            vx = vx - gx / 2
            vy = vy + gy / 2
            vx = vx * 199 / 200
            vy = vy * 199 / 200
            mX = mX + vx / 2
            mY = mY + vy / 2
            if (mX < 0) {
                mX = 0f
                if (lastx == mX) {
                    vx = 0f
                } else {
                    vx = -vx * 3 / 4
                    mSoundPool.play(
                        soundIds["soundId1"] as Int,
                        1f,
                        1f,
                        1,
                        0,
                        1f
                    ) //音频地址，音量，音量，优先级，声音播放速率
                }
            } else if (mX > areaWidth) {
                mX = areaWidth.toFloat()
                if (lastx == mX) {
                    vx = 0f
                } else {
                    vx = -vx * 3 / 4
                    mSoundPool.play(
                        soundIds["soundId1"] as Int,
                        1f,
                        1f,
                        1,
                        0,
                        1f
                    ) //音频地址，音量，音量，优先级，声音播放速率
                }
            }
            if (mY < 0) {
                mY = 0f
                if (lasty == mY) {
                    vy = 0f
                } else {
                    vy = -vy * 3 / 4
                    mSoundPool.play(
                        soundIds["soundId1"] as Int,
                        1f,
                        1f,
                        1,
                        0,
                        1f
                    ) //音频地址，音量，音量，优先级，声音播放速率
                }
            } else if (mY > areaHeight) {
                mY = areaHeight.toFloat()
                if (lasty == mY) {
                    vy = 0f
                } else {
                    vy = -vy * 3 / 4
                    mSoundPool.play(
                        soundIds["soundId1"] as Int,
                        1f,
                        1f,
                        1,
                        0,
                        1f
                    ) //音频地址，音量，音量，优先级，声音播放速率
                }
            }
            if (gz > 19) {
                val thread: Thread = object : Thread() {
                    override fun run() {
                        super.run()
                        Log.d("gz", gz.toString())
                        mSoundPool.play(soundIds["soundId2"] as Int, 1f, 1f, 1, 0, 1f)
                    }
                }
                thread.start()
            }
            lastx = mX
            lasty = mY
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    private val mythread: Thread = object : Thread() {
        override fun run() {
            super.run()
            while (running) {
                val starttime = System.currentTimeMillis()
                synchronized(mViewHolder) {
                    mycanvas = mViewHolder.lockCanvas()
                    draw()
                    mViewHolder.unlockCanvasAndPost(mycanvas)
                }
                val endtime = System.currentTimeMillis()
                var difftime = (endtime - starttime).toInt()
                while (difftime < 20) {
                    difftime = (System.currentTimeMillis() - starttime).toInt()
                    yield()
                }
            }
        }
    }

    private fun draw() {
        mycanvas.drawBitmap(BACK!!, 0f, 0f, mypaint)
        mycanvas.drawBitmap(ball!!, mX, mY, mypaint)
    }

    companion object {

        //音频服务管理器
//        private var audioManager
//                : AudioManager
//        private var mcontext: Context
    }

    private val soundIds: MutableMap<String, Any> by lazy {
        mutableMapOf("soundId1" to mSoundPool.load(context, R.raw.bong, 2),
            "soundId2" to mSoundPool.load(context, R.raw.a, 1))
    }

    init {
        this.isFocusable = true
        this.isFocusableInTouchMode = true
        //mcontext = context
        mWidth = w
        mHeight = h
        // 参数分别为：声音池中最多可同时存在的声音个数；
        //mSoundPool = SoundPool(2, AudioManager.STREAM_MUSIC, 0)
        // AudioManager.STREAM_MUSIC；第三个参数现在无意义默认0

        /*val soundId1 = mSoundPool.load(context, R.raw.bong, 2)
        val soundId2 = mSoundPool.load(context, R.raw.a, 1)
        soundIds = HashMap()
        soundIds["soundId1"] = soundId1
        soundIds["soundId2"] = soundId2*/

        /*mViewHolder = this.holder
        mViewHolder.addCallback(this)*/

        mSensorManager = sensorManager
        mycanvas = Canvas()
        mypaint = Paint()
        mypaint.color = Color.WHITE
        //audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
}