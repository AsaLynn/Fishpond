package com.water.fish

import android.animation.*
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Path
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.*
import android.widget.FrameLayout
import android.widget.TextView
import com.water.fish.holder.DrawableHolder
import com.zxn.popup.EasyPopup
import com.zxn.popup.XGravity
import com.zxn.popup.YGravity
import pl.droidsonroids.gif.AnimationListener
import pl.droidsonroids.gif.GifImageView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs


/**
 *  海底布局.
 *  参考资料:
 *  https://www.jianshu.com/p/036aecb64093
 *  https://blog.csdn.net/Jiang_Rong_Tao/article/details/58635019?utm_medium=distribute.pc_relevant.none-task-blog-baidujs_baidulandingword-0&spm=1001.2101.3001.4242
 *  Created by zxn on 2021/5/26.
 *  后续增加估值器的用法.
 *  https://blog.csdn.net/carson_ho/article/details/99284218
 *  https://github.com/koral--/android-gif-drawable
 *  https://www.javadoc.io/doc/pl.droidsonroids.gif/android-gif-drawable
 **/
class SeafloorLayout : FrameLayout, View.OnClickListener {

    companion object {

        private const val TAG = "SeafloorLayout"

        private const val SPEED = 40L

        private const val TYPE_1 = 1 //从左到右 带旋转角度

        private const val TYPE_2 = 2 //从右到左 带旋转角度

        private const val TYPE_3 = 3 //从左到右  直线

        private const val TYPE_4 = 4 //从右到左  直线

        private const val HANDLER_100 = 100 //从右到左  直线

        private const val HANDLER_101 = 101 //从右到左  直线

    }

    private val petPath = Path().apply {
        moveTo(200f, 200f)
        lineTo(200F * 3, 200F)
        lineTo(200F * 3, 200F * 5)
        lineTo(200F * 1, 200F * 5)
        lineTo(200F * 1, 200F * 2)
    }

    private val ivFish: GifImageView by lazy {
        findViewById<GifImageView>(R.id.ivFish).apply {
            setOnClickListener(this@SeafloorLayout)
        }
    }

    private var mWidth = 0

    private val fishEntityList = mutableListOf<FishEntity>()

    /**
     * 鱼宠区域的高度
     */
    private var fishPetAreaH = 0

    /**
     * 容器底部区域高度,与宠物无法抵达的区域:即总深度-鱼宠区域的高度
     */
    private var waterBottomHeight = 0

    /**
     * 鱼儿游动范围左边Padding
     */
    private var paddingPetAreaLeft = 0

    private val mTipsPop: EasyPopup by lazy {
        EasyPopup.create()
            .setContentView(context, R.layout.layout_fish_tips)
            .setFocusAndOutsideEnable(false)
            .apply()
    }

    private val petFishAnimatorListener = object : Animator.AnimatorListener {

        override fun onAnimationStart(animation: Animator) {
            //Glide.with(context).load(fishEntityList[0].moveLeftResId).into(ivFish)
            //val gifFromResource = GifDrawable(resources, R.drawable.anim)
            //ivFish.setImageResource(fishEntityList[0].moveLeftResId)
            ivFish.setImageDrawable(drawableHolder?.moveLeftDrawable)
        }

        override fun onAnimationEnd(animation: Animator) {

        }

        override fun onAnimationCancel(animation: Animator) {

        }

        override fun onAnimationRepeat(animation: Animator) {

        }

    }

    private val petFishUpdateListener = ValueAnimator.AnimatorUpdateListener {
        //val x = it.getAnimatedValue(it.values[0].propertyName).toString().toFloat().toInt()
        //val y = it.getAnimatedValue(it.values[1].propertyName).toString().toFloat().toInt()
//        if (x == petPointList[1].x && y == petPointList[1].y) {
//            //Glide.with(context).load(fishEntityList[0].moveLeftResId).into(ivFish)
//            //it.pause()
//            isInPointRect(petPointList[2],)
//        } else if ((x - petPointList[2].x) < 10 && (y - petPointList[2].y) < 10) {
//            Glide.with(context).load(fishEntityList[0].turnRightResId).into(ivFish)
//            //it.pause()
//        }
        val x = it.getAnimatedValue(it.values[0].propertyName).toString().toFloat()
        val y = it.getAnimatedValue(it.values[1].propertyName).toString().toFloat()
        Log.i(TAG, "addUpdateListener:(x:$x,y:$y) ")
        if (isInPointRect(petPointList[1], x, y)) {
            //Glide.with(context).load(fishEntityList[0].moveLeftResId).into(ivFish)
            //it.pause()
        } else if (isInPointRect(petPointList[2], x, y)) {
            it.pause()
            //Glide.with(context).load(fishEntityList[0].turnRightResId).into(ivFish)
            //ivFish.setImageResource(fishEntityList[0].turnRightResId)
            drawableHolder?.let { holder ->
                /*holder.turnRightDrawable.addAnimationListener { num ->
                    Log.i(TAG, "num: $num")
                    //it.resume()
                    ivFish.setImageDrawable(drawableHolder?.moveRightDrawable)
                }*/
                ivFish.setImageDrawable(holder.turnRightDrawable)
                //val duration = holder.turnRightDrawable.duration
                val duration = 40L
                postDelayed({
                    it.resume()
                    ivFish.setImageDrawable(drawableHolder?.moveRightDrawable)
                }, duration.toLong())
            }
        } else if (isInPointRect(petPointList[3], x, y)) {
            it.pause()
        }
    }

    private val turnRightListener = AnimationListener {
        Log.i(TAG, ": count:$it")
        //ivFish.setImageDrawable(drawableHolder?.moveRightDrawable)
        petFishAnimator.resume()
    }

    private val petFishAnimator by lazy {
        ObjectAnimator.ofFloat(ivFish, View.X, View.Y, petPath).apply {
            addListener(petFishAnimatorListener)
            addUpdateListener(petFishUpdateListener)
            //以浮点型的形式从初始值 - 结束值 进行过渡
            //setEvaluator(FloatEvaluator())
            //以整型的形式从初始值 - 结束值 进行过渡
            //setEvaluator(IntEvaluator())
            //自定义从初始值 - 结束值 进行过渡
            //setEvaluator(PetPointEvaluator(petPointList))
            interpolator = LinearInterpolator()
            duration = SPEED * 1000
        }
    }

    private var isStarted = false

    private val petPointList = mutableListOf<Point>()

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        //setWillNotDraw(false)
        isClickable = false
        onInitAttributeSet(attrs)
        LayoutInflater.from(context).inflate(R.layout.layout_pet, this)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        //计算鱼宠游动范围的最大高度
        mWidth = width
        fishPetAreaH = height - waterBottomHeight
        if (fishPetAreaH <= 0) {
            fishPetAreaH = height
        }

        addRoutePoint()

        if (isStarted) {
            if (!petFishAnimator.isStarted) {
                petFishAnimator.start()
            }
        }
    }


    override fun onClick(v: View) {
        if (!mTipsPop.isShowing) {
            if (fishEntityList.isNotEmpty() && fishEntityList[0] is PetFish) {
                (fishEntityList[0] as PetFish).tips()?.let {
                    mTipsPop.findViewById<TextView>(R.id.tvTips)?.text = it
                }
            }
            mTipsPop.showAtAnchorView(v, YGravity.ABOVE, XGravity.CENTER)
            postDelayed({
                mTipsPop.dismiss()
            }, 5 * 1000L)
        }
    }

    private fun onInitAttributeSet(attrs: AttributeSet?) {
        val typedArray: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.SeafloorLayout)
        try {
            fishPetAreaH =
                typedArray.getDimensionPixelOffset(R.styleable.SeafloorLayout_fishPetAreaHeight, 0)
            waterBottomHeight =
                typedArray.getDimensionPixelOffset(R.styleable.SeafloorLayout_waterBottomHeight, 0)
            Log.i(TAG, "constructor: waterBottomHeight:$waterBottomHeight")
            paddingPetAreaLeft =
                typedArray.getDimensionPixelOffset(R.styleable.SeafloorLayout_paddingPetAreaLeft, 0)
        } finally {
            typedArray.recycle()
        }
    }

    private var drawableHolder: DrawableHolder? = null


    private fun notifyDataSetChanged() {
        fishEntityList.forEachIndexed { _, entity ->
            if (entity is PetFish) {
                drawableHolder = DrawableHolder.create(resources, entity, turnRightListener)
            } else {
                /*val fishView =
                    LayoutInflater.from(context).inflate(R.layout.layout_fish_shoal, this, false)
                addView(fishView)
                initAnimation(TYPE_3, height, fishView)*/
            }
        }
    }


    fun setFishData(entityList: MutableList<FishEntity>) {
        if (entityList.isNullOrEmpty()) throw IllegalStateException("entityList can not be null or empty !")

        end()

        if (entityList !== fishEntityList) {
            fishEntityList.clear()
            fishEntityList.addAll(entityList)
        } else {
            val newList = ArrayList(entityList)
            fishEntityList.clear()
            fishEntityList.addAll(newList)
        }

        notifyDataSetChanged()

        /*if (!objectAnimator.isPaused && !objectAnimator.isStarted) {
            objectAnimator.start()
        }*/

    }

    fun start() {
        isStarted = true
        requestLayout()
    }

    fun pause() {
        /*if (!objectAnimator.isStarted) {
            objectAnimator.start()
        } else {
            objectAnimator.pause()
        }*/
    }

    fun resume() {
        //objectAnimator.resume()
    }

    fun end() {
        //objectAnimator.resume()
    }

    private fun initAnimation(type: Int, height: Int, iv: View) {
        iv.visibility = VISIBLE
        val v = 1f
        //        int degress = 40;
        /**
         * 旋转
         */
        var rotateAnimation: RotateAnimation? = null
        var translateAnimation: TranslateAnimation? = null
        val random = Random()
        //val value = random.nextInt(50) + 1
        //val value = random.nextInt(100) + 1
        val value = 100
        when (type) {
            TYPE_1 -> {
                translateAnimation = TranslateAnimation(0f, height.toFloat(), 0f, 0f)
                rotateAnimation = RotateAnimation(
                    0F,
                    value.toFloat(),
                    RotateAnimation.RELATIVE_TO_SELF,
                    v,
                    RotateAnimation.RELATIVE_TO_SELF,
                    v
                )
            }
            TYPE_2 -> {
                translateAnimation = TranslateAnimation(0f, height.toFloat(), 0f, 0f)
                rotateAnimation = RotateAnimation(
                    0F,
                    (-value).toFloat(),
                    RotateAnimation.RELATIVE_TO_SELF,
                    v,
                    RotateAnimation.RELATIVE_TO_SELF,
                    v
                )
            }
            TYPE_3 -> {
                translateAnimation = TranslateAnimation(0f, 1000F, 0f, 0f)
                /*rotateAnimation = RotateAnimation(
                    0F,
                    (-value).toFloat(),
                    RotateAnimation.RELATIVE_TO_SELF,
                    v,
                    RotateAnimation.RELATIVE_TO_SELF,
                    v
                )*/
            }
            TYPE_4 -> {
                translateAnimation = TranslateAnimation(0f, height.toFloat(), 0f, 0f)
                rotateAnimation = RotateAnimation(
                    0F,
                    value.toFloat(),
                    RotateAnimation.RELATIVE_TO_SELF,
                    v,
                    RotateAnimation.RELATIVE_TO_SELF,
                    v
                )
            }
        }
        /**
         * 平移.
         */
        val animationSet = AnimationSet(true)
        animationSet.duration = 15000
        animationSet.interpolator = LinearInterpolator()
        if (translateAnimation != null) {
            animationSet.addAnimation(translateAnimation)
            translateAnimation.repeatCount = Animation.INFINITE
        }
        if (rotateAnimation != null) {
            animationSet.addAnimation(rotateAnimation)
        }
        iv.startAnimation(animationSet)
    }

    /**
     * 装载游泳路线坐标点
     */
    private fun addRoutePoint() {
        //计算路线的坐标点
        //坐标点已经装载,则不再进行装载.
        val fishWidth = ivFish.layoutParams.width
        val fishHeight = ivFish.layoutParams.height
        petPath.reset()
        petPointList.clear()
        if (petPath.isEmpty) {
            //P0
            val x0 = mWidth - fishWidth
            val y0 = height - fishHeight
            Log.i(TAG, "addRoutePoint: x0:$x0,y0:$y0")
            petPointList.add(Point(x0, y0))
            petPath.moveTo(x0.toFloat(), y0.toFloat())

            //P1
            val x1 =
                paddingLeft + paddingPetAreaLeft + (mWidth - paddingPetAreaLeft - paddingLeft) / 2 - fishWidth / 2
            val y1 = fishPetAreaH / 2
            petPointList.add(Point(x1, y1))
            petPath.lineTo(x1.toFloat(), y1.toFloat())

            //P2
            val x2 = paddingLeft + paddingPetAreaLeft
            val y2 = fishPetAreaH / 2
            petPointList.add(Point(x2, y2))
            petPath.lineTo(x2.toFloat(), y2.toFloat())

            //P3
            val x3 = mWidth - fishWidth
            val y3 = fishHeight / 2
            petPointList.add(Point(x3, y3))
            petPath.lineTo(x3.toFloat(), y3.toFloat())

            //P4
            val x4 = mWidth - fishWidth
            val y4 = fishPetAreaH / 2
            petPointList.add(Point(x4, y4))
            petPath.lineTo(x4.toFloat(), y4.toFloat())

            //P5
            petPointList.add(Point(x3, y3))
            petPath.lineTo(x3.toFloat(), y3.toFloat())

            //P6
            val x6 = paddingLeft + paddingPetAreaLeft
            val y6 = fishHeight / 2
            petPointList.add(Point(x6, y6))
            petPath.lineTo(x6.toFloat(), y6.toFloat())

            //P7
            petPointList.add(Point(x4, y4))
            petPath.lineTo(x4.toFloat(), y4.toFloat())

            //P8
            petPointList.add(Point(x1, y1))
            petPath.lineTo(x1.toFloat(), y1.toFloat())

            //P9
            val x9 = paddingLeft + paddingPetAreaLeft
            val y9 = fishPetAreaH
            petPointList.add(Point(x9, y9))
            petPath.lineTo(x9.toFloat(), y9.toFloat())

            //P10
            petPointList.add(Point(x2, y2))
            petPath.lineTo(x2.toFloat(), y2.toFloat())

            //P11
            //t.addPoint(x9, y9)
            petPointList.add(Point(x9, y9))
            petPath.lineTo(x9.toFloat(), y9.toFloat())

            //P12
            petPointList.add(Point(x0, y0))
            petPath.lineTo(x0.toFloat(), y0.toFloat())
        }
    }

    private fun isInPointRect(point: Point, x: Float, y: Float): Boolean =
        abs(point.x - (x + 0.5F).toInt()) < 8 && abs(point.y - (y + 0.5F).toInt()) < 5

    fun testPathAnimator() {
        val path = Path().apply {
            moveTo(200f, 200f)
            lineTo(200F * 3, 200F)
            lineTo(200F * 3, 200F * 5)
            lineTo(200F * 1, 200F * 5)
            lineTo(200F * 3, 200F * 1)
        }
        val moveAnimator = ObjectAnimator.ofFloat(ivFish, "x", "y", path)
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
            /*Log.i(
                TAG,
                "addUpdateListener:(x:${it.getAnimatedValue(it.values[0].propertyName)},y:${
                    it.getAnimatedValue(it.values[1].propertyName)
                }) "
            )*/
        }
        moveAnimator.interpolator = LinearInterpolator()
        moveAnimator.duration = 5 * 1000
        moveAnimator.start()
    }

}


//    protected fun dp2px(dp: Float): Int {
//        val scale: Float = mContext.getResources().getDisplayMetrics().density
//        return (dp * scale + 0.5f).toInt()
//    }
//
//    protected fun sp2px(sp: Float): Int {
//        val scale: Float = this.mContext.getResources().getDisplayMetrics().scaledDensity
//        return (sp * scale + 0.5f).toInt()
//    }



