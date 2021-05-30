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
import android.widget.ImageView
import android.widget.TextView
import com.water.fish.holder.PetDrawableHolder
import com.water.fish.listener.PetAnimatorListener
import com.water.fish.listener.PetUpdateListener
import com.zxn.popup.EasyPopup
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs


/**
 *  海底布局.
 *  采用ObjectAnimator+AnimatorSet + View位移+多个Path,动画组合进行监听.
 *  参考资料:
 *  https://www.cnblogs.com/endian11/p/9604196.html
 *  https://github.com/koral--/android-gif-drawable
 **/
class BlueWaterLayout : FrameLayout, View.OnClickListener {

    companion object {

        private const val TAG = "BlueWaterLayout"

        private const val SPEED = 5

        private const val SPEED_MOVE_SECOND = 1000L

        private const val TYPE_1 = 1 //从左到右 带旋转角度

        private const val TYPE_2 = 2 //从右到左 带旋转角度

        private const val TYPE_3 = 3 //从左到右  直线

        private const val TYPE_4 = 4 //从右到左  直线

        private const val HANDLER_100 = 100 //从右到左  直线

        private const val HANDLER_101 = 101 //从右到左  直线

    }

    /**
     * 组合动路径.
     * 0:p0->p1
     * 1:p1->p2
     * 2:p2->p3->p4->p5
     * 3:p5->p6
     * 4:p6->p7
     * 5:p7->p8
     * 6:p8->p9->p10->p11
     * 7:p11->p12
     */
    private val petPathList = mutableListOf<Path>().apply {
        for (i in 0 until 8) {
            add(Path())
        }
    }

    private val ivPetFish: GifImageView by lazy {
        findViewById<GifImageView>(R.id.ivFish).apply {
            setOnClickListener(this@BlueWaterLayout)
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



    /**
     * 当前移动的关键坐标点索引.
     */
    private var currentIndex: Int = 0


    private val petObjectAnimatorList = mutableListOf<Animator>()


//    private val petAnimatorSet = AnimatorSet()

    private var isStarted = false

//    private val petPointList = mutableListOf<Point>()

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
        Log.i(TAG, "onSizeChanged: ")
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
        Log.i(TAG, "onWindowFocusChanged: ")
        if (hasWindowFocus) {
            if (isStarted) {
                if (petPathList.isNotEmpty()) {
                    /*if (!petAnimatorSet.isStarted) {
                        //petAnimatorSet.playSequentially(petObjectAnimatorList)
                        petAnimatorSet.play(petObjectAnimatorList[0])
                        petAnimatorSet.start()
                    }*/
                    petObjectAnimatorList[0].start()
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
                typedArray.getDimensionPixelOffset(
                    R.styleable.SeafloorLayout_fishPetAreaHeight,
                    0
                )
            waterBottomHeight =
                typedArray.getDimensionPixelOffset(
                    R.styleable.SeafloorLayout_waterBottomHeight,
                    0
                )
            Log.i(TAG, "constructor: waterBottomHeight:$waterBottomHeight")
            paddingPetAreaLeft =
                typedArray.getDimensionPixelOffset(
                    R.styleable.SeafloorLayout_paddingPetAreaLeft,
                    0
                )
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
    }

    fun pause() {
        /*if (!petAnimatorSet.isPaused) {
            petAnimatorSet.pause()
        }*/
        ivAIShell.drawable?.let {
            (it as GifDrawable).stop()
        }

    }

    fun resume() {
        /*if (petAnimatorSet.isStarted) {
            isEnd = false
            if (petAnimatorSet.isRunning) {
                petAnimatorSet.resume()
            }
        }*/
        ivAIShell.drawable?.let {
            (it as GifDrawable).run {
                reset()
            }
        }
    }

    fun end() {
        isStarted = false
        isEnd = true
//        petFishAnimator.end()
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
        Log.i(TAG, "addRoutePoint: ")
        //计算路线的坐标点
        //坐标点已经装载,则不再进行装载.

        //val topMargin = (ivPetFish.layoutParams as ConstraintLayout.LayoutParams).topMargin
        //val fishHeight = ivPetFish.layoutParams.height + topMargin
        val fishWidth = ivPetFish.layoutParams.width
        val fishHeight = vgPetView.layoutParams.height
        petPathList.forEach {
            it.reset()
        }
        //P0
        val x0 = mWidth - fishWidth
        val y0 = height - fishHeight
        petPathList[0].moveTo(x0.toFloat(), y0.toFloat())

        //P1
        val x1 =
            paddingLeft + paddingPetAreaLeft + (mWidth - paddingPetAreaLeft - paddingLeft) / 2 - fishWidth / 2
        val y1 = fishPetAreaH / 2
        petPathList[0].lineTo(x1.toFloat(), y1.toFloat())
        petPathList[1].moveTo(x1.toFloat(), y1.toFloat())

        //P2
        val x2 = paddingLeft + paddingPetAreaLeft
        val y2 = fishPetAreaH / 2
        petPathList[1].lineTo(x2.toFloat(), y2.toFloat())
        petPathList[2].reset()
        petPathList[2].moveTo(x2.toFloat(), x2.toFloat())

        //P3
        val x3 = mWidth - fishWidth
        val y3 = fishHeight / 2
        petPathList[2].lineTo(x3.toFloat(), y3.toFloat())

        //P4
        val x4 = mWidth - fishWidth
        val y4 = fishPetAreaH / 2
        petPathList[2].lineTo(x4.toFloat(), y4.toFloat())

        //P5
        petPathList[2].lineTo(x3.toFloat(), y3.toFloat())
        petPathList[3].moveTo(x3.toFloat(), y3.toFloat())

        //P6
        val x6 = paddingLeft + paddingPetAreaLeft
        val y6 = fishHeight / 2
        petPathList[3].lineTo(x6.toFloat(), y6.toFloat())
        petPathList[4].moveTo(x6.toFloat(), y6.toFloat())

        //P7
        petPathList[4].lineTo(x4.toFloat(), y4.toFloat())
        petPathList[5].moveTo(x4.toFloat(), y4.toFloat())

        //P8
        petPathList[5].lineTo(x1.toFloat(), y1.toFloat())
        petPathList[6].moveTo(x1.toFloat(), y1.toFloat())

        //P9
        val x9 = paddingLeft + paddingPetAreaLeft
        //val y9 = fishPetAreaH
        val y9 = height - fishHeight
        petPathList[6].lineTo(x9.toFloat(), y9.toFloat())

        //P10
        petPathList[6].lineTo(x2.toFloat(), y2.toFloat())

        //P11
        //t.addPoint(x9, y9)
        petPathList[6].lineTo(x9.toFloat(), y9.toFloat())
        petPathList[7].moveTo(x9.toFloat(), y9.toFloat())

        //P12
        petPathList[7].lineTo(x0.toFloat(), y0.toFloat())


        petPathList.forEachIndexed { index, path ->
            if (!path.isEmpty) {
                petObjectAnimatorList.add(
                    ObjectAnimator.ofFloat(vgPetView, View.X, View.Y, path).apply {
                        addListener(PetAnimatorListener(index, this@BlueWaterLayout))
                        addUpdateListener(PetUpdateListener(index, this@BlueWaterLayout))
                        interpolator = LinearInterpolator()
                        duration = when (index) {
                            0 -> petFishSpeed * SPEED_MOVE_SECOND
                            1 -> 4 * SPEED_MOVE_SECOND
                            2 -> 15 * SPEED_MOVE_SECOND
                            else -> petFishSpeed * SPEED_MOVE_SECOND
                        }
                    })
            }
        }
    }

    private fun isInPointRect(point: Point, x: Float, y: Float): Boolean =
        abs(point.x - (x + 0.5F).toInt()) < 10
                && abs(point.y - (y + 0.5F).toInt()) < 10


    private fun isInPointRect(index: Int, x: Float, y: Float): Boolean = false


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

    fun onPetAnimationStart(index: Int, animation: Animator) {
        when (index) {
            0 -> {
                currentIndex = 0
                petDrawableHolder?.let {
                    ivPetFish.setImageDrawable(if (it.isSpraying) it.spurtLeftDrawable else it.moveLeftDrawable)
                }
            }
            1 -> {

            }
            2 -> {
                petDrawableHolder?.let { holder ->
                    ivPetFish.setImageDrawable(if (holder.isSpraying) holder.spurtRightDrawable else holder.moveRightDrawable)
                }
            }
        }
    }

    fun onPetAnimationEnd(index: Int, animation: Animator) {
        when (index) {
            0 -> {
                currentIndex = 1
                petObjectAnimatorList[1].startDelay = restDuration
                petObjectAnimatorList[1].start()
            }
            1 -> {
                currentIndex = 2
                petDrawableHolder?.let { holder ->
                    ivPetFish.setImageDrawable(holder.turnRightDrawable)
                }
                petObjectAnimatorList[2].startDelay = turnDuration
                petObjectAnimatorList[2].start()
            }
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
                }, restDuration)
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
                postDelayed({
                    it.resume()
                }, restDuration)
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


    private var mOnItemListener: OnClickListener? = null

    /**
     * 设置1000的petFishSpeed速度.
     */
    var petFishSpeed = SPEED
        set(value) {
            field = value
            pause()
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



