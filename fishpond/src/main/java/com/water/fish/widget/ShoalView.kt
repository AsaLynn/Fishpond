package com.water.fish.widget

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import com.water.fish.FishEntity
import com.water.fish.ShoalFish
import com.water.fish.listener.ShoalAnimatorListener
import com.water.fish.widget.IMarineView.Companion.SPEED_MOVE_SECOND
import pl.droidsonroids.gif.GifImageView
import kotlin.math.roundToInt

/**
 *  (轨迹动画)
 *  https://blog.csdn.net/Jiang_Rong_Tao/article/details/58635019
 *  (贝塞尔曲线实现波浪动画)
 *  https://blog.csdn.net/zz51233273/article/details/107866070
 *  (绘制波浪线)
 *  https://blog.csdn.net/IT_XF/article/details/75014160
 *  鱼群
 *  Created by zxn on 2021/6/1.
 **/
class ShoalView : GifImageView, IMarineView {

    companion object {
        private const val TAG = "ShoalView"
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setWillNotDraw(false)
        isClickable = false
        scaleType = ScaleType.FIT_XY
    }

    private var mShoalFish: ShoalFish? = null

    private val pointList = mutableListOf<PointF>()

    //波峰高度，view的1/2
    var mCrestHeight: Float = 80f

    //波峰长度
    private var mCrestWidth: Int = 200

    //波起始点
    private var mStartPoint: Float = 0f

    //隐藏一个波长
    private var mLeftHide: Float = 0f

    private val mPathMeasure = PathMeasure()

    private val petObjectAnimatorList = mutableListOf<Animator>()

    private val mAnimatorListener =
        ShoalAnimatorListener(this, petObjectAnimatorList)

    override var moveSpeed: Long = 5L

    override fun onInitMovement(rectF: RectF) {
        pointList.clear()
        rectF.run {
            val shoalWidth = measuredWidth
            //底部开始
            mStartPoint = rectF.centerY() - measuredHeight / 2
            //波峰高度
            mCrestHeight = rectF.height() / 2F
            //波峰长度
            //mCrestWidth = rectF.width().toInt() / 2
            mCrestWidth = rectF.width().toInt() / 2
            //隐藏一个波长
            mLeftHide = -mCrestWidth.toFloat()
            //几个波峰
            val n = (rectF.width() / mCrestWidth + 0.5).roundToInt()
            //val size = n * 4 + 4
            //val size = n * 4
            val size = n * 2
            for (i in 0..size) {
                //val x = (i * mCrestWidth / 4).toFloat() - mCrestWidth
                //val x = (i * mCrestWidth / 4).toFloat() - mCrestWidth / 4
                //val x = if (size - 1 == i) (i * mCrestWidth / 4).toFloat() - mCrestWidth else (i * mCrestWidth / 4).toFloat() - mCrestWidth / 2
                //val x = (i * mCrestWidth / 4).toFloat() - mCrestWidth / 2
                val x = (i * mCrestWidth / 2).toFloat() - mCrestWidth / 2
                var y = 0f
                when (i % 4) {
                    0, 2 -> y = mStartPoint
                    1 -> y = mStartPoint + mCrestHeight
                    3 -> y = mStartPoint - mCrestHeight
                }
                pointList.add(PointF(x, y))
            }
        }
        val path = Path()
        path.reset()
        path.moveTo(pointList[0].x, pointList[0].y)
        for (i in 0..(pointList.size - 3) step 2) {
            path.quadTo(
                pointList[i + 1].x, pointList[i + 1].y,
                pointList[i + 2].x, pointList[i + 2].y
            )
        }
        mPathMeasure.setPath(path, false)
        val moveDuration = (mPathMeasure.length.toLong() * SPEED_MOVE_SECOND / moveSpeed)
        val objectAnimator = ObjectAnimator.ofFloat(this, View.X, View.Y, path).apply {
            addListener(mAnimatorListener)
            interpolator = LinearInterpolator()
            duration = moveDuration
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
        }
        petObjectAnimatorList.add(objectAnimator)
    }

    override fun turnLeft(nextAnimator: Animator) {
        Log.i(TAG, "turnLeft: ")
        //rotation = 360F
        rotationY = 180F
        mShoalFish?.let {
            setImageResource(it.shuffledSkinResId())
        }
    }

    override fun turnRight(nextAnimator: Animator) {
        Log.i(TAG, "turnRight: ")
        //rotation = 180F
        rotationY = 360F
        mShoalFish?.let {
            setImageResource(it.shuffledSkinResId())
        }
    }

    override fun start(nextAnimator: Animator) {
        mShoalFish?.let {
            setImageResource(it.shuffledSkinResId())
        }
    }

    override fun onChanged(entity: FishEntity) {
        if (entity is ShoalFish) {
            mShoalFish = entity
            moveSpeed = entity.moveSpeed
        }
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        mAnimatorListener.onWindowFocusChanged(hasWindowFocus)
    }

//    override fun moveLeft() {
//
//    }
//
//    override fun moveRight() {
//
//    }

}


/*fun testPathAnimator() {
        val path = Path().apply {
            moveTo(200f, 200f)
            lineTo(200F * 3, 200F)
            lineTo(200F * 3, 200F * 5)
            lineTo(200F * 1, 200F * 5)
            lineTo(200F * 3, 200F * 1)
        }
        val moveAnimator = ObjectAnimator.ofFloat(ivPetFish, "x", "y", path)
        moveAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                //Log.i(TAG, "onAnimationStart: ")
            }

            override fun onAnimationEnd(animation: Animator) {
                //l.removeView(imageView)
            }

            override fun onAnimationCancel(animation: Animator) {
                //l.removeView(imageView)
            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
        moveAnimator.addUpdateListener {
            *//*Log.i(
                TAG,
                "addUpdateListener:(x:${it.getAnimatedValue(it.values[0].propertyName)},y:${
                    it.getAnimatedValue(it.values[1].propertyName)
                }) "
            )*//*
        }
        moveAnimator.interpolator = LinearInterpolator()
        moveAnimator.duration = 5 * 1000
        moveAnimator.start()
    }*/


//petAnimatorSet.duration = petFishSpeed * 1000L
//petAnimatorSet.playSequentially(petObjectAnimatorList)

/*petAnimatorSet.play(petObjectAnimatorList[1])
    .after(restDuration * SPEED_MOVE_SECOND)
    .after(petObjectAnimatorList[0])

petAnimatorSet.play(petObjectAnimatorList[2])
    .after(petObjectAnimatorList[1])

petAnimatorSet.play(petObjectAnimatorList[3]).after(turnDuration)
    .after(petObjectAnimatorList[2])

petAnimatorSet.play(petObjectAnimatorList[4]).after(turnDuration)
    .after(petObjectAnimatorList[3])

petAnimatorSet.play(petObjectAnimatorList[5]).after(turnDuration)
    .after(petObjectAnimatorList[4])

petAnimatorSet.play(petObjectAnimatorList[6])
    .after(restDuration * SPEED_MOVE_SECOND)
    .after(petObjectAnimatorList[5])

petAnimatorSet.play(petObjectAnimatorList[7]).after(turnDuration)
    .after(petObjectAnimatorList[6])*/


//private fun initAnimation(type: Int, height: Int, iv: View) {
//    iv.visibility = FrameLayout.VISIBLE
//    val v = 1f
//    /**
//     * 旋转
//     */
//    var rotateAnimation: RotateAnimation? = null
//    var translateAnimation: TranslateAnimation? = null
//    val random = Random()
//    //val value = random.nextInt(50) + 1
//    //val value = random.nextInt(100) + 1
//    val value = 100
//    when (type) {
//        BlueWaterLayout.TYPE_1 -> {
//            translateAnimation = TranslateAnimation(0f, height.toFloat(), 0f, 0f)
//            rotateAnimation = RotateAnimation(
//                0F,
//                value.toFloat(),
//                RotateAnimation.RELATIVE_TO_SELF,
//                v,
//                RotateAnimation.RELATIVE_TO_SELF,
//                v
//            )
//        }
//        BlueWaterLayout.TYPE_2 -> {
//            translateAnimation = TranslateAnimation(0f, height.toFloat(), 0f, 0f)
//            rotateAnimation = RotateAnimation(
//                0F,
//                (-value).toFloat(),
//                RotateAnimation.RELATIVE_TO_SELF,
//                v,
//                RotateAnimation.RELATIVE_TO_SELF,
//                v
//            )
//        }
//        BlueWaterLayout.TYPE_3 -> {
//            translateAnimation = TranslateAnimation(0f, 1000F, 0f, 0f)
//            /*rotateAnimation = RotateAnimation(
//                0F,
//                (-value).toFloat(),
//                RotateAnimation.RELATIVE_TO_SELF,
//                v,
//                RotateAnimation.RELATIVE_TO_SELF,
//                v
//            )*/
//        }
//        BlueWaterLayout.TYPE_4 -> {
//            translateAnimation = TranslateAnimation(0f, height.toFloat(), 0f, 0f)
//            rotateAnimation = RotateAnimation(
//                0F,
//                value.toFloat(),
//                RotateAnimation.RELATIVE_TO_SELF,
//                v,
//                RotateAnimation.RELATIVE_TO_SELF,
//                v
//            )
//        }
//    }
//    /**
//     * 平移.
//     */
//    val animationSet = AnimationSet(true)
//    animationSet.duration = 15000
//    animationSet.interpolator = LinearInterpolator()
//    if (translateAnimation != null) {
//        animationSet.addAnimation(translateAnimation)
//        translateAnimation.repeatCount = Animation.INFINITE
//    }
//    if (rotateAnimation != null) {
//        animationSet.addAnimation(rotateAnimation)
//    }
//    iv.startAnimation(animationSet)
//}

//    /**
//     * 获取测试路线
//     * @return  Path 路线
//     */
//    private fun getTestPath(): Path {
//        val path = Path()
//        val points: List<Point> = getRandomPoint()
//        path.moveTo(100.0f, 100.0f)
//        //cubicTo（）方法去描述一条三阶贝塞尔曲线
//        path.cubicTo(
//            200f,
//            200f,
//            500f,
//            500f,
//            200f,
//            1000f
//        )
//        return path
//    }


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

//    fun getNextMovePoi(): Point {
//        progress =
//            if (progress < 1) progress + SPEED else 1.0
//
//        pMeasure!!.getPosTan(
//            ((pMeasure!!.length * progress).toInt()).toFloat(),
//            pos,
//            tan
//        )
//
//        val degrees = (atan2(
//            tan[1].toDouble(),
//            tan[0].toDouble()
//        ) * 180.0 / Math.PI).toFloat()
//
//        rotation = degrees
//
//        return Point(pos[0].toInt(), pos[1].toInt())
//    }