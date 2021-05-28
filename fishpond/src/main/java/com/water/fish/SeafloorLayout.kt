package com.water.fish

import android.animation.*
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.water.fish.holder.PetDrawableHolder
import com.zxn.popup.EasyPopup
import pl.droidsonroids.gif.GifDrawable
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

        private const val SPEED = 30

        private const val SPEED_MOVE_SECOND = 1000L

        private const val TYPE_1 = 1 //从左到右 带旋转角度

        private const val TYPE_2 = 2 //从右到左 带旋转角度

        private const val TYPE_3 = 3 //从左到右  直线

        private const val TYPE_4 = 4 //从右到左  直线

        private const val HANDLER_100 = 100 //从右到左  直线

        private const val HANDLER_101 = 101 //从右到左  直线

    }

    private val petPath = Path()

    private val ivPetFish: GifImageView by lazy {
        findViewById<GifImageView>(R.id.ivFish).apply {
            setOnClickListener(this@SeafloorLayout)
        }
    }

    private val tvTips by lazy {
        findViewById<TextView>(R.id.tvTips)
    }

    private val vgPetView by lazy {
        findViewById<View>(R.id.vgPetView)
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
            .setAnchorView(ivPetFish)
            .setNeedReMeasureWH(true)
            .apply()
    }

    /**
     * 停止动画标识.
     */
    private var isEnd = false

    private val petFishAnimatorListener = object : Animator.AnimatorListener {

        override fun onAnimationStart(animation: Animator) {
            currentIndex = 0
            petDrawableHolder?.let {
                ivPetFish.setImageDrawable(if (it.isSpraying) it.spurtLeftDrawable else it.moveLeftDrawable)
            }
        }

        override fun onAnimationEnd(animation: Animator) {
            if (!isEnd) {
                currentIndex = 12
                animation.startDelay = 200L
                animation.start()
            }
        }

        override fun onAnimationCancel(animation: Animator) {

        }

        override fun onAnimationRepeat(animation: Animator) {

        }

    }

    private val petFishUpdateListener = ValueAnimator.AnimatorUpdateListener {
        val x = it.getAnimatedValue(it.values[0].propertyName).toString().toFloat()
        val y = it.getAnimatedValue(it.values[1].propertyName).toString().toFloat()
        Log.i(TAG, "addUpdateListener:(x:$x,y:$y) ")
        when {
            isInPointRect(1, x, y) -> {
                it.pause()
                currentIndex = 1
                postDelayed({
                    it.resume()
                }, turnDuration)
            }
            isInPointRect(2, x, y) -> {
                it.pause()
                currentIndex = 2
                petDrawableHolder?.let { holder ->
                    ivPetFish.setImageDrawable(holder.turnRightDrawable)
                    postDelayed({
                        it.resume()
                        ivPetFish.setImageDrawable(if (holder.isSpraying) holder.spurtRightDrawable else holder.moveRightDrawable)
                    }, turnDuration)
                }

            }
            isInPointRect(3, x, y) -> {
                currentIndex = 3
            }
            isInPointRect(4, x, y) -> {
                currentIndex = 4
            }
            isInPointRect(5, x, y) -> {
                it.pause()
                ivPetFish.setImageDrawable(petDrawableHolder?.turnLeftDrawable)
                currentIndex = 5
                postDelayed({
                    it.resume()
                    petDrawableHolder?.run {
                        ivPetFish.setImageDrawable(if (isSpraying) spurtLeftDrawable else moveLeftDrawable)
                    }
                }, turnDuration)
            }
            isInPointRect(6, x, y) -> {
                it.pause()
                ivPetFish.setImageDrawable(petDrawableHolder?.turnRightDrawable)
                currentIndex = 6
                postDelayed({
                    it.resume()
                    petDrawableHolder?.run {
                        ivPetFish.setImageDrawable(if (isSpraying) spurtRightDrawable else moveRightDrawable)
                    }
                }, turnDuration)
            }
            isInPointRect(7, x, y) -> {
                it.pause()
                ivPetFish.setImageDrawable(petDrawableHolder?.turnLeftDrawable)
                currentIndex = 7
                postDelayed({
                    it.resume()
                    petDrawableHolder?.run {
                        ivPetFish.setImageDrawable(if (isSpraying) spurtLeftDrawable else moveLeftDrawable)
                    }
                }, turnDuration)
            }
            isInPointRect(8, x, y) -> {
                it.pause()
                currentIndex = 8
                val duration = 1000L
                postDelayed({
                    it.resume()
                }, duration)
            }
            isInPointRect(9, x, y) -> {
                currentIndex = 9
            }
            isInPointRect(10, x, y) -> {
                currentIndex = 10
            }
            isInPointRect(11, x, y) -> {
                it.pause()
                ivPetFish.setImageDrawable(petDrawableHolder?.turnRightDrawable)
                currentIndex = 11
                postDelayed({
                    it.resume()
                    petDrawableHolder?.run {
                        ivPetFish.setImageDrawable(if (isSpraying) spurtRightDrawable else moveRightDrawable)
                    }
                }, turnDuration)
            }
        }
    }

    /**
     * 当前移动的关键坐标点索引.
     */
    private var currentIndex: Int = 0

    private val petFishAnimator by lazy {
        //vgPetView,ivPetFish
        ObjectAnimator.ofFloat(vgPetView, View.X, View.Y, petPath).apply {
            addListener(petFishAnimatorListener)
            addUpdateListener(petFishUpdateListener)
            //自定义从初始值 - 结束值 进行过渡
            //setEvaluator(PetPointEvaluator(petPointList))
            interpolator = LinearInterpolator()
            duration = petFishSpeed * SPEED_MOVE_SECOND
        }
    }

    private var isStarted = false

    private val petPointList = mutableListOf<Point>()

    /**
     * 转身持续时间.turnDuration
     */
    private val turnDuration = 40L

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
        LayoutInflater.from(context).inflate(R.layout.layout_shells_ai, this)
        LayoutInflater.from(context).inflate(R.layout.layout_pet, this)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //计算鱼宠游动范围的最大高度
        mWidth = width
        fishPetAreaH = height - waterBottomHeight
        if (fishPetAreaH <= 0) {
            fishPetAreaH = height
        }

        addRoutePoint()

        /*if (isStarted) {
            if (!petFishAnimator.isStarted) {
                petFishAnimator.start()
            }
        }*/


    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isStarted) {
            if (!petFishAnimator.isStarted) {
                petFishAnimator.start()
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ivFish -> {
                if (tvTips.visibility != View.VISIBLE) {
                    if (fishEntityList.isNotEmpty() && fishEntityList[0] is PetFish) {
                        (fishEntityList[0] as PetFish).tips()?.let {
                            tvTips.text = it
                        }
                    }
                    tvTips.visibility = View.VISIBLE
                    postDelayed({
                        tvTips.visibility = View.GONE
                    }, 6 * 1000L)
                }
            }
            R.id.ivAIShell -> {
                mOnItemListener?.onClick(v)
            }
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

    private var petDrawableHolder: PetDrawableHolder? = null


    fun notifyDataSetChanged() {
        pause()
        fishEntityList.forEachIndexed { _, entity ->
            if (entity is PetFish) {
                petDrawableHolder = PetDrawableHolder.create(resources, entity)
                //更换皮肤
                setPetDrawable()
            } else if (entity is Shell) {
                if (entity.shellResId != 0) {
                    ivAIShell.setImageResource(entity.shellResId)
                }
                if (entity.stoneResId != 0) {
                    ivStone.setImageResource(entity.stoneResId)
                }
            }
            /*else {
                val fishView =
                    LayoutInflater.from(context).inflate(R.layout.layout_fish_shoal, this, false)
                addView(fishView)
                initAnimation(TYPE_3, height, fishView)
            }*/
        }

        resume()
    }

    //pet
    private fun setPetDrawable() {
        petDrawableHolder?.let {
            when (currentIndex) {
                0, 1, 5, 7, 8, 9, 10, 12 -> {
                    ivPetFish.setImageDrawable(it.toLeftDrawable())
                }
                2, 3, 4, 6, 11 -> {
                    ivPetFish.setImageDrawable(it.toRightDrawable())
                }
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

    }

    fun start() {
        isStarted = true
        isEnd = false
        if (petPointList.isNotEmpty()) {
            invalidate()
            //requestLayout()
        }
    }

    fun pause() {
        if (!petPath.isEmpty) {
            if (!petFishAnimator.isStarted) {
                petFishAnimator.start()
            } else {
                petFishAnimator.pause()
            }
        }
        ivAIShell.drawable?.let {
            (it as GifDrawable).stop()
        }

    }

    fun resume() {
        if (!petPath.isEmpty) {
            if (petFishAnimator.isStarted) {
                petFishAnimator.resume()
            }
        }
        ivAIShell.drawable?.let {
            (it as GifDrawable).run {
                reset()
            }
        }
    }

    fun end() {
        isEnd = true
        if (!petPath.isEmpty) {
            petFishAnimator.end()
        }
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

        val topMargin = (ivPetFish.layoutParams as ConstraintLayout.LayoutParams).topMargin
        val fishWidth = ivPetFish.layoutParams.width
        //val fishHeight = ivPetFish.layoutParams.height + topMargin
        val fishHeight = vgPetView.layoutParams.height
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
            //val y9 = fishPetAreaH
            val y9 = height - fishHeight
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
        abs(point.x - (x + 0.5F).toInt()) < 10
                && abs(point.y - (y + 0.5F).toInt()) < 10


    private fun isInPointRect(index: Int, x: Float, y: Float): Boolean {
        /*val point = petPointList[index]
        if (index == 5 && currentIndex > 3) {
            return abs(point.y - (y + 0.5F).toInt()) < 10
        } else {
            return abs(point.x - (x + 0.5F).toInt()) < 10
                    && abs(point.y - (y + 0.5F).toInt()) < 10
                    && (index - currentIndex) == 1
        }*/

        if (petPointList.isEmpty()) return false

        val point = petPointList[index]
        return abs(point.x - (x + 0.5F).toInt()) < 10
                && abs(point.y - (y + 0.5F).toInt()) < 10
                && (index - currentIndex) == 1
    }


    fun testPathAnimator() {
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

    val ivStone by lazy {
        findViewById<ImageView>(R.id.ivStone)
    }

    val ivAIShell by lazy {
        findViewById<ImageView>(R.id.ivAIShell).also {
            it.setOnClickListener(this)
        }
    }

    fun setOnItemClickListener(l: OnClickListener?) {
        mOnItemListener = l
    }

    private var mOnItemListener: OnClickListener? = null

    /**
     * 设置1000的petFishSpeed速度.
     */
    var petFishSpeed = SPEED
        set(value) {
            field = value
            pause()
            petFishAnimator.duration = field * SPEED_MOVE_SECOND
            resume()
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



