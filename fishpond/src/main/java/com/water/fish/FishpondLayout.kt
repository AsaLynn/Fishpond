package com.water.fish

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.zxn.popup.EasyPopup
import com.zxn.popup.XGravity
import com.zxn.popup.YGravity
import java.util.*
import kotlin.collections.ArrayList

/**
 *  鱼塘布局.
 *  Created by zxn on 2021/3/29.
 **/
class FishpondLayout : ViewGroup, View.OnClickListener {

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
    }

    companion object {
        private const val TAG = "FishpondLayout"
        private const val SPEED = 80L

        private const val TYPE_1 = 1 //从左到右 带旋转角度

        private const val TYPE_2 = 2 //从右到左 带旋转角度

        private const val TYPE_3 = 3 //从左到右  直线

        private const val TYPE_4 = 4 //从右到左  直线

        private const val HANDLER_100 = 100 //从右到左  直线

        private const val HANDLER_101 = 101 //从右到左  直线

    }

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

    /**
     *  游行进度0到1,每到1表示一个完整轨迹结束.
     */
    private var progress = 0.0F
        set(value) {
            field = value
            requestLayout()
        }

    private val mTipsPop: EasyPopup by lazy {
        EasyPopup.create()
            .setContentView(context, R.layout.layout_fish_tips)
            .setFocusAndOutsideEnable(false)
            .apply()
    }

    /**
     * 计算所有ChildView的宽度和高度 然后根据ChildView的计算结果，设置自己的宽和高
     * @param widthMeasureSpec  父类传递过来给当前ViewGroup的宽度建议值
     * @param heightMeasureSpec 父类传递过来给当前ViewGroup的高度建议值
     */
    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.i(TAG, "onMeasure: ")
        val sizeWidth = MeasureSpec.getSize(widthMeasureSpec)
        val sizeHeight = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(
            sizeWidth,
            sizeHeight
        )
        for (i in 0 until childCount) {
            getChildAt(i).run {
                if (this.visibility != GONE) {
                    measureChild(this, widthMeasureSpec, heightMeasureSpec)
                }
            }
        }
    }

    /**
     * 对子View的位置进行排列
     * 为每个子View分配大小和位置，从布局调用。
     * @param changed true:大小或位置发生变化,
     * @param l 左侧位置，相对于父级位置
     * @param t 上侧位置，相对于父级位置
     * @param r 右侧位置，相对于父级位置
     * @param b 下侧位置，相对于父级位置
     */
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        //计算鱼宠游动范围的最大高度
        fishPetAreaH = height - waterBottomHeight
        if (fishPetAreaH <= 0) {
            fishPetAreaH = height
        }

        addRoutePoint()

        for (i in 0 until childCount) {
            fishEntityList[i].run {
                layoutFish(this, getChildAt(i))
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
            context.obtainStyledAttributes(attrs, R.styleable.FishpondLayout)
        try {
            fishPetAreaH =
                typedArray.getDimensionPixelOffset(R.styleable.FishpondLayout_fishPetAreaHeight, 0)
            waterBottomHeight =
                typedArray.getDimensionPixelOffset(R.styleable.FishpondLayout_waterBottomHeight, 0)
            Log.i(TAG, "constructor: waterBottomHeight:$waterBottomHeight")
            paddingPetAreaLeft =
                typedArray.getDimensionPixelOffset(R.styleable.FishpondLayout_paddingPetAreaLeft, 0)
        } finally {
            typedArray.recycle()
        }
    }

    /**
     * 布局容器中的鱼儿.
     */
    private fun layoutFish(fish: FishEntity, view: View) {
        if (fish is PetFish) {
            if (fish.isSwimming) {
                view.run {
                    val fishWidth = measuredWidth
                    val fishHeight = measuredHeight
                    val swimmingPoint = fish.swimmingPoint(progress, findViewById(R.id.ivFish))
                    val childL = swimmingPoint.x - fishWidth / 2
                    val childT = swimmingPoint.y - fishHeight / 2
                    val childR = swimmingPoint.x + fishWidth / 2
                    val childB = swimmingPoint.y + fishHeight / 2
                    this.layout(childL, childT, childR, childB)
                }
            } else {
                objectAnimator.pause()
                postDelayed({
                    fish.isSwimming = true
                    objectAnimator.resume()
                }, 1000L * 2)
            }
        } else {
            view.also {
                if (it is ImageView) {
                    Glide.with(it).load(fish.skinResId).into(it)
                }
                it.layout(0, height - measuredHeight / 2, measuredWidth, height)
            }
        }
    }

    /**
     * 装载游泳路线坐标点
     */
    private fun addRoutePoint() {
        //计算路线的坐标点
        fishEntityList.forEachIndexed { index, t ->
            //坐标点已经装载,则不再进行装载.
            val fishWidth = getChildAt(index).measuredWidth
            val fishHeight = getChildAt(index).measuredHeight
            if (!t.isAdded()) {
                //P0
                val x0 = width - fishWidth / 2
                val y0 = fishPetAreaH - fishHeight / 2
                Log.i(TAG, "addRoutePoint: x0:$x0,y0:$y0")
                t.addPoint(x0, y0)

                //P1
                val x1 =
                    paddingLeft + paddingPetAreaLeft + (width - paddingPetAreaLeft - paddingLeft) / 2
                val y1 = fishPetAreaH / 2
                t.addPoint(x1, y1)

                //P2
                val x2 = paddingLeft + paddingPetAreaLeft + fishWidth / 2
                val y2 = fishPetAreaH / 2
                t.addPoint(x2, y2)

                //P3
                val x3 = width - fishWidth / 2
                val y3 = fishHeight / 2
                t.addPoint(x3, y3)

                //P4
                val x4 = width - fishWidth / 2
                val y4 = fishPetAreaH / 2
                t.addPoint(x4, y4)

                //P5
                t.addPoint(x3, y3)

                //P6
                val x6 = paddingLeft + paddingPetAreaLeft + fishWidth / 2
                val y6 = fishHeight / 2
                t.addPoint(x6, y6)

                //P7
                t.addPoint(x4, y4)

                //P8
                t.addPoint(x1, y1)

                //P9
                val x9 = paddingLeft + paddingPetAreaLeft + fishWidth / 2
                val y9 = fishPetAreaH - fishHeight / 2
                t.addPoint(x9, y9)

                //P10
                t.addPoint(x2, y2)

                //P11
                t.addPoint(x9, y9)

                //P12
                t.addPoint(x0, y0)
            }
        }

    }

    private fun notifyDataSetChanged() {
        removeAllViews()
        fishEntityList.forEachIndexed { _, entity ->
            if (entity is PetFish) {
                val fishView =
                    LayoutInflater.from(context).inflate(R.layout.layout_fish_pet, this, false)
                addView(fishView)
                fishView.setOnClickListener(this)
                entity.setSprayChangeListener {
                    Glide.with(context).load(it).into(fishView.findViewById(R.id.ivFish))
                }
            } else {
                // if (entity is ShoalFish)  layout_fish_shoal.
                val fishView =
                    LayoutInflater.from(context).inflate(R.layout.layout_fish_shoal, this, false)
                addView(fishView)
                initAnimation(TYPE_3, height, fishView)
            }
        }
    }

    private val objectAnimator by lazy {
        ObjectAnimator.ofFloat(this, "Progress", 0.0F, 1.0F).apply {
            duration = SPEED * 1000
            interpolator = LinearInterpolator()
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    start()
                }
            })
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

        if (!objectAnimator.isPaused && !objectAnimator.isStarted) {
            objectAnimator.start()
        }
    }

    fun pause() {
        if (!objectAnimator.isStarted) {
            objectAnimator.start()
        } else {
            objectAnimator.pause()
        }
    }

    fun resume() {
        objectAnimator.resume()
    }

    fun end() {
        objectAnimator.resume()
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

//    private var mAdapter: FishAdapter? = null

    /*override fun getAdapter(): FishAdapter? = mAdapter

    override fun setAdapter(adapter: FishAdapter) {
        //mAdapter?.unregisterDataSetObserver(mDataSetObserver)
        //resetList();
        mAdapter?.let {

        }
    }

    override fun getSelectedView(): View ? = null

    override fun setSelection(position: Int) {

    }*/

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

