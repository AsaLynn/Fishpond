package com.water.fish

import android.animation.*
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.water.fish.holder.PetDrawableHolder
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

/**
 *  速度加快会出倒游问题,推荐指数不高.
 *  采用ValueAnimator+View位移+Path
 *  https://www.jianshu.com/p/b6a5b20cf3b9
 **/
class SeaLayout : FrameLayout, View.OnClickListener {

    companion object {

        private const val TAG = "SeaLayout"

        private const val SPEED = 40

        private const val SPEED_MOVE_SECOND = 1000L

        private const val TYPE_1 = 1 //从左到右 带旋转角度

        private const val TYPE_2 = 2 //从右到左 带旋转角度

        private const val TYPE_3 = 3 //从左到右  直线

        private const val TYPE_4 = 4 //从右到左  直线

        private const val HANDLER_100 = 100 //从右到左  直线

        private const val HANDLER_101 = 101 //从右到左  直线

    }

    private val petPath = Path()

    private val petPathMeasure = PathMeasure()

    private val petPathLengthArray = FloatArray(13)

    private val petValueAnimator by lazy {
        ValueAnimator.ofFloat(
            petPathLengthArray[0],
            petPathLengthArray[1],
            petPathLengthArray[2],
            petPathLengthArray[3],
            petPathLengthArray[4],
            petPathLengthArray[5],
            petPathLengthArray[6],
            petPathLengthArray[7],
            petPathLengthArray[8],
            petPathLengthArray[9],
            petPathLengthArray[10],
            petPathLengthArray[11],
            petPathLengthArray[12],
        ).apply {
            addListener(petValueAnimatorListener)
            addUpdateListener(petUpdateListener)
            interpolator = LinearInterpolator()
            duration = petFishSpeed * SPEED_MOVE_SECOND
        }
    }

    private val petPosArray = FloatArray(2)

    private val petUpdateListener = ValueAnimator.AnimatorUpdateListener {
        val value = (it.animatedValue as Float)
        petPathMeasure.getPosTan(value,petPosArray,null)
        vgPetView.x = petPosArray[0]
        vgPetView.y = petPosArray[1]
        Log.i(TAG, "addUpdateListener:(value:$value")
        //Log.i(TAG, "addUpdateListener:(x:$x,y:$y) ")
        when(value.toInt()) {
            petPathLengthArray[1].toInt() -> {
                it.pause()
                currentIndex = 1
                postDelayed({
                    it.resume()
                }, restDuration)
            }
            petPathLengthArray[2].toInt() -> {
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
            petPathLengthArray[3].toInt() -> {
                currentIndex = 3
            }
            petPathLengthArray[4].toInt() -> {
                currentIndex = 4
            }
            petPathLengthArray[5].toInt() -> {
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
            petPathLengthArray[6].toInt() -> {
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
            petPathLengthArray[7].toInt() -> {
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
            petPathLengthArray[8].toInt() -> {
                it.pause()
                currentIndex = 8
                postDelayed({
                    it.resume()
                }, restDuration)
            }
            petPathLengthArray[9].toInt() -> {
                currentIndex = 9
            }
            petPathLengthArray[10].toInt() -> {
                currentIndex = 10
            }
            petPathLengthArray[11].toInt() -> {
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

    private val ivPetFish: GifImageView by lazy {
        findViewById<GifImageView>(R.id.ivFish).apply {
            setOnClickListener(this@SeaLayout)
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

//    private val mTipsPop: EasyPopup by lazy {
//        EasyPopup.create()
//            .setContentView(context, R.layout.layout_fish_tips)
//            .setFocusAndOutsideEnable(false)
//            .setAnchorView(ivPetFish)
//            .setNeedReMeasureWH(true)
//            .apply()
//    }

    /**
     * 停止动画标识.
     */
    private var isEnd = false

    private val petValueAnimatorListener = object : Animator.AnimatorListener {

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

    /**
     * 当前移动的关键坐标点索引.
     */
    private var currentIndex: Int = 0

    private var isStarted = false

    private val petPointList = mutableListOf<Point>()

    /**
     * 转身持续时间.turnDuration
     */
    private val turnDuration = 60L

    var restDuration = 2 * 1000L


    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setWillNotDraw(false)
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
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus) {
            if (isStarted) {
                if (!petPath.isEmpty) {
                    if (!petValueAnimator.isStarted) {
                        petValueAnimator.start()
                    }
                }
            }
            resume()
        } else {
            pause()
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
    }

    fun pause() {
        if (!petPath.isEmpty) {
            if (!petValueAnimator.isPaused) {
                petValueAnimator.pause()
            }
        }
        ivAIShell.drawable?.let {
            (it as GifDrawable).stop()
        }

    }

    fun resume() {
        if (!petPath.isEmpty) {
            if (petValueAnimator.isStarted) {
                isEnd = false
                if (petValueAnimator.isRunning) {
                    petValueAnimator.resume()
                }
            }
        }
        ivAIShell.drawable?.let {
            (it as GifDrawable).run {
                reset()
            }
        }
    }

    fun end() {
        isStarted = false
        isEnd = true
        if (!petPath.isEmpty) {
            petValueAnimator.end()
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
        //val topMargin = (ivPetFish.layoutParams as ConstraintLayout.LayoutParams).topMargin
        //val fishHeight = ivPetFish.layoutParams.height + topMargin
        val fishWidth = ivPetFish.layoutParams.width
        val fishHeight = vgPetView.layoutParams.height
        petPath.reset()
        petPointList.clear()
        if (petPath.isEmpty) {

            //P0
            val x0 = mWidth - fishWidth
            val y0 = height - fishHeight
            petPointList.add(Point(x0, y0))
            petPath.moveTo(x0.toFloat(), y0.toFloat())
            petPathMeasure.setPath(petPath, false)
            petPathLengthArray[0] = petPathMeasure.length

            //P1
            val x1 =
                paddingLeft + paddingPetAreaLeft + (mWidth - paddingPetAreaLeft - paddingLeft) / 2 - fishWidth / 2
            val y1 = fishPetAreaH / 2
            petPointList.add(Point(x1, y1))
            petPath.lineTo(x1.toFloat(), y1.toFloat())
            petPathMeasure.setPath(petPath, false)
            petPathLengthArray[1] = petPathMeasure.length

            //P2
            val x2 = paddingLeft + paddingPetAreaLeft
            val y2 = fishPetAreaH / 2
            petPointList.add(Point(x2, y2))
            petPath.lineTo(x2.toFloat(), y2.toFloat())
            petPathMeasure.setPath(petPath, false)
            petPathLengthArray[2] = petPathMeasure.length

            //P3
            val x3 = mWidth - fishWidth
            val y3 = fishHeight / 2
            petPointList.add(Point(x3, y3))
            petPath.lineTo(x3.toFloat(), y3.toFloat())
            petPathMeasure.setPath(petPath, false)
            petPathLengthArray[3] = petPathMeasure.length

            //P4
            val x4 = mWidth - fishWidth
            val y4 = fishPetAreaH / 2
            petPointList.add(Point(x4, y4))
            petPath.lineTo(x4.toFloat(), y4.toFloat())
            petPathMeasure.setPath(petPath, false)
            petPathLengthArray[4] = petPathMeasure.length

            //P5
            petPointList.add(Point(x3, y3))
            petPath.lineTo(x3.toFloat(), y3.toFloat())
            petPathMeasure.setPath(petPath, false)
            petPathLengthArray[5] = petPathMeasure.length

            //P6
            val x6 = paddingLeft + paddingPetAreaLeft
            val y6 = fishHeight / 2
            petPointList.add(Point(x6, y6))
            petPath.lineTo(x6.toFloat(), y6.toFloat())
            petPathMeasure.setPath(petPath, false)
            petPathLengthArray[6] = petPathMeasure.length

            //P7
            petPointList.add(Point(x4, y4))
            petPath.lineTo(x4.toFloat(), y4.toFloat())
            petPathMeasure.setPath(petPath, false)
            petPathLengthArray[7] = petPathMeasure.length

            //P8
            petPointList.add(Point(x1, y1))
            petPath.lineTo(x1.toFloat(), y1.toFloat())
            petPathMeasure.setPath(petPath, false)
            petPathLengthArray[8] = petPathMeasure.length

            //P9
            val x9 = paddingLeft + paddingPetAreaLeft
            //val y9 = fishPetAreaH
            val y9 = height - fishHeight
            petPointList.add(Point(x9, y9))
            petPath.lineTo(x9.toFloat(), y9.toFloat())
            petPathMeasure.setPath(petPath, false)
            petPathLengthArray[9] = petPathMeasure.length

            //P10
            petPointList.add(Point(x2, y2))
            petPath.lineTo(x2.toFloat(), y2.toFloat())
            petPathMeasure.setPath(petPath, false)
            petPathLengthArray[10] = petPathMeasure.length

            //P11
            //t.addPoint(x9, y9)
            petPointList.add(Point(x9, y9))
            petPath.lineTo(x9.toFloat(), y9.toFloat())
            petPathMeasure.setPath(petPath, false)
            petPathLengthArray[11] = petPathMeasure.length

            //P12
            petPointList.add(Point(x0, y0))
            petPath.lineTo(x0.toFloat(), y0.toFloat())
            petPathMeasure.setPath(petPath, false)
            petPathLengthArray[12] = petPathMeasure.length
            for (item in petPathLengthArray) {
                Log.i(TAG, "addRoutePoint: $item")
            }

        }
    }

    private fun isInPointRect(index: Int, x: Float, y: Float): Boolean {

        if (petPointList.isEmpty()) return false

        val point = petPointList[index]
        return abs(point.x - (x + 0.5F).toInt()) < 5
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
            petValueAnimator.duration = field * SPEED_MOVE_SECOND
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



