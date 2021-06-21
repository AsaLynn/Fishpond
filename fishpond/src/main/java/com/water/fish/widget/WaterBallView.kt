package com.water.fish.widget

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.SoundPool
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.water.fish.R

/**
 * WaterBallView
 * 跟随重力传感器移动的小水球.
 */
class WaterBallView : SurfaceView, SurfaceHolder.Callback, Runnable {

    var soundEnabled = false

    var rollEnabled = false

    private var ballBitmap: Bitmap? = null

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

    private var mPaint: Paint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
    }

    private val mPaintTran: Paint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        color = Color.TRANSPARENT
    }

    private val mSensorManager: SensorManager by lazy {
        context.getSystemService(Activity.SENSOR_SERVICE) as SensorManager
    }

    //声音池
    private val mSoundPool: SoundPool = SoundPool(2, AudioManager.STREAM_MUSIC, 0)

    private val soundIds: MutableMap<String, Any> by lazy {
        mutableMapOf(
            "soundId1" to mSoundPool.load(context, R.raw.bong, 2),
            "soundId2" to mSoundPool.load(context, R.raw.a, 1)
        )
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.setZOrderOnTop(true)
        this.holder.setFormat(PixelFormat.TRANSLUCENT)
        ballBitmap = BitmapFactory.decodeResource(resources, R.drawable.ball)
        holder.addCallback(this)
    }

    /**
     * 当SurfaceHolder被创建的时候回调，一般在这个函数开启绘图线程
     */
    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.i(TAG, "surfaceCreated: ")

        mWidth = width
        mHeight = height

        mX = (mWidth / 2).toFloat()
        mY = (mHeight / 2).toFloat()

        lastx = mX
        lasty = mY


        computeRollingArea()

        //获取传感器的类型(TYPE_ACCELEROMETER:加速度传感器)
        val sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mSensorManager.registerListener(
            mBallSensorEventListener,
            sensor,
            SensorManager.SENSOR_DELAY_GAME
        )
        mSoundPool.load(context, R.raw.bong, 2)
        start()
        Thread(this).start()
    }


    /**
     * 大小或格式发生变化时触发,
     */
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.i(TAG, "surfaceChanged: ")
    }

    private val mBallSensorEventListener by lazy {
        BallSensorEventListener()
    }

    /**
     * 当SurfaceHolder被销毁的时候回调.
     */
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.i(TAG, "surfaceDestroyed: ")
        mSensorManager.unregisterListener(mBallSensorEventListener)
        stop()
    }

    private fun computeRollingArea() {
        areaWidth = mWidth
        areaHeight = mHeight

        ballBitmap?.let {
            areaWidth = mWidth - it.width
            areaHeight = mHeight - it.height
        }
    }

    /**
     * 传感器的变化监听.
     */
    private inner class BallSensorEventListener : SensorEventListener {

        override fun onSensorChanged(event: SensorEvent) {
            //定z坐标轴上的位置(加速度)
            gz = event.values[2]
            //定y坐标轴上的位置(加速度)
            gy = event.values[1] - 0.105.toFloat()
            //定x坐标轴上的位置(加速度)
            gx = event.values[0] - 0.75.toFloat()
            Log.d(TAG, "[gx:$gx,gy:$gy,gz:$gz]")

            vx -= gx / 2
            vy += gy / 2
            vx = vx * 199 / 200
            vy = vy * 199 / 200
            mX += vx / 2
            mY += vy / 2
            if (mX < 0) {
                mX = 0f
                if (lastx == mX) {
                    vx = 0f
                } else {
                    vx = -vx * 3 / 4
                    //音频地址，音量，音量，优先级，声音播放速率
                    if (soundEnabled) {
                        mSoundPool.play(
                            soundIds["soundId1"] as Int,
                            1f,
                            1f,
                            1,
                            0,
                            1f
                        )
                    }
                }
            } else if (mX > areaWidth) {
                mX = areaWidth.toFloat()
                if (lastx == mX) {
                    vx = 0f
                } else {
                    vx = -vx * 3 / 4
                    //音频地址，音量，音量，优先级，声音播放速率
                    if (soundEnabled) {
                        mSoundPool.play(
                            soundIds["soundId1"] as Int,
                            1f,
                            1f,
                            1,
                            0,
                            1f
                        )
                    }
                }
            }
            if (mY < 0) {
                mY = 0f
                if (lasty == mY) {
                    vy = 0f
                } else {
                    vy = -vy * 3 / 4
                    //音频地址，音量，音量，优先级，声音播放速率
                    if (soundEnabled) {
                        mSoundPool.play(
                            soundIds["soundId1"] as Int,
                            1f,
                            1f,
                            1,
                            0,
                            1f
                        )
                    }
                }
            } else if (mY > areaHeight) {
                mY = areaHeight.toFloat()
                if (lasty == mY) {
                    vy = 0f
                } else {
                    vy = -vy * 3 / 4
                    //音频地址，音量，音量，优先级，声音播放速率
                    if (soundEnabled){
                        mSoundPool.play(
                            soundIds["soundId1"] as Int,
                            1f,
                            1f,
                            1,
                            0,
                            1f
                        )
                    }
                }
            }
            if (soundEnabled){
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
            }
            lastx = mX
            lasty = mY
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

        }
    }

    private fun drawBall() {
        val canvas = holder.lockCanvas()
        try {
            canvas.drawPaint(mPaintTran)
            canvas.drawBitmap(ballBitmap!!, mX, mY, mPaint)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (holder.surface.isValid) {
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }

    private fun start() {
        isFocusable = true
        isFocusableInTouchMode = true
        running = true
    }

    private fun stop() {
        running = false
        isFocusable = false
        isFocusableInTouchMode = false
    }

    companion object {
        private const val TAG = "WaterBallView"
    }

    override fun run() {
        while (running) {
            /*if(currentThread().isInterrupted){
                return
            }*/
            //val startTime = System.currentTimeMillis()

            synchronized(holder) {
                drawBall()
            }

            /*val endTime = System.currentTimeMillis()
            var difftime = (endTime - startTime).toInt()
            Log.i(TAG, "run,time: $difftime")
            while (difftime in 1..20) {
                difftime = (System.currentTimeMillis() - startTime).toInt()
                yield()
            }*/
        }
    }

    fun decodeBallResource(id: Int) {
        ballBitmap = BitmapFactory.decodeResource(resources, id)
        computeRollingArea()
    }
}