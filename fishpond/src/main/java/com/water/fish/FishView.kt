package com.water.fish

import android.content.Context
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.Point
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.atan2

/**
 *  鱼儿
 *  Created by zxn on 2021/3/30.
 **/
class FishView : AppCompatImageView {

    companion object {
        private const val TAG = "FishView"
        private const val SPEED = 0.001
    }

    /**
     *
     */
    private var progress = 0.0


    private val pos = FloatArray(2)
    private val tan = FloatArray(2)

    private var path: Path? = null

    private var anim //鱼动画
            : AnimationDrawable? = null

    private var pMeasure: PathMeasure? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        Log.i(TAG, "constructor(): ")
    }

    init {
        Log.i(TAG, "init: ")
        layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    /**
     * 获取鱼下一个移动的点坐标
     */
    fun getNextMovePoi(): Point {
        progress =
            if (progress < 1) progress + SPEED else 1.0

        pMeasure!!.getPosTan(
            ((pMeasure!!.length * progress).toInt()).toFloat(),
            pos,
            tan
        )

        val degrees = (atan2(
            tan[1].toDouble(),
            tan[0].toDouble()
        ) * 180.0 / Math.PI).toFloat()

        rotation = degrees

        return Point(pos[0].toInt(), pos[1].toInt())

//        return if (progress < 1) {
//        }
    }

    //给鱼设置行走路线
    fun setPath(path: Path) {
        this.path = path
        progress = 0.0
        pMeasure = PathMeasure(path, false)
    }

    fun start() {
        anim?.start()
    }

    fun stop() {
        anim?.stop()
    }

    fun getPath(): Path? {
        return path
    }

    fun setAnim(anim: AnimationDrawable) {
        this.anim = anim
        adjustViewBounds = true
        setImageDrawable(anim)
//        fishWidth = anim.minimumWidth
//        fishHeight = anim.minimumHeight
    }

    //    //将鱼的图片切割出来
//    fun getFishAnim(): AnimationDrawable {
//        val fishBit = BitmapFactory.decodeResource(context!!.resources, R.mipmap.fishs)
//        fishWidth = fishBit.width / 7
//        fishHeight = fishBit.height
//        val anim = AnimationDrawable()
//        for (i in 0..6) {
//            val btmap = Bitmap.createBitmap(fishBit, i * fishWidth, 0, fishWidth, fishHeight)
//            val d: Drawable = BitmapDrawable(context!!.resources, btmap)
//            anim.addFrame(d, FRAME_INTERVAL)
//        }
//        anim.isOneShot = false
//        return anim
//    }
}