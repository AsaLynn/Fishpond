package com.water.fish

import android.animation.*
import android.content.Context
import android.content.res.TypedArray
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.*
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.water.fish.widget.PetView
import pl.droidsonroids.gif.GifDrawable
import java.util.*
import kotlin.collections.ArrayList


/**
 *  海底布局.
 *  采用ObjectAnimator + View位移+多个Path,动画组合进行监听.
 *  AnimatorSet无法操作动画的重复运行.
 *  参考资料:
 *  https://www.cnblogs.com/endian11/p/9604196.html
 *  https://github.com/koral--/android-gif-drawable
 **/
class BlueWaterLayout : ConstraintLayout, View.OnClickListener, ISeaLayout {

    companion object {
        private const val TAG = "BlueWaterLayout"
    }

    private val vgPetView by lazy {
        findViewById<PetView>(R.id.petView).apply {
            setOnPetClickListener(this@BlueWaterLayout)
        }
    }

//    private var mWidth = 0

    private val fishEntityList = mutableListOf<FishEntity>()

    /**
     * 鱼宠区域的高度
     */
//    private var fishPetAreaH = 0

    /**
     * 容器底部区域高度,与宠物无法抵达的区域:即总深度-鱼宠区域的高度
     */
//    private var waterBottomHeight = 0

    /**
     * 鱼儿游动范围左边Padding
     */
    private var paddingPetAreaLeft = 0

    /**
     * 停止动画标识.
     */
    private var isEnd = false


    private var isStarted = false

    private var mOnItemListener: OnClickListener? = null

    val ivStone by lazy {
        findViewById<ImageView>(R.id.ivStone)
    }

    val ivAIShell by lazy {
        findViewById<ImageView>(R.id.ivAIShell).also {
            it.setOnClickListener(this)
        }
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
        onInitAttributeSet(attrs)
        LayoutInflater.from(context).inflate(R.layout.layout_blue_water, this)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.i(TAG, "onSizeChanged: ")
        //计算鱼宠游动范围的最大高度
//        mWidth = width
        /*fishPetAreaH = height - waterBottomHeight
        if (fishPetAreaH <= 0) {
            fishPetAreaH = height
        }*/
        val fishWidth = vgPetView.measuredWidth
        val fishHeight = vgPetView.measuredHeight
        val petRectF = RectF().apply {
            left = (paddingLeft + paddingPetAreaLeft).toFloat()
            top = paddingTop.toFloat()
            right = width - paddingRight.toFloat() - fishWidth
            bottom =
                height - paddingBottom.toFloat() - (vgPetView.layoutParams as LayoutParams).bottomMargin - fishHeight
        }
        vgPetView.initMovement(petRectF)
    }



    override fun onClick(v: View) {
        when (v.id) {

            R.id.ivAIShell -> {
                mOnItemListener?.onClick(v)
            }
        }

    }

    private fun onInitAttributeSet(attrs: AttributeSet?) {
        val typedArray: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.BlueWaterLayout)
        try {
            /*fishPetAreaH =
                typedArray.getDimensionPixelOffset(
                    R.styleable.BlueWaterLayout_fishPetAreaHeight,
                    0
                )*/
            /*waterBottomHeight =
                typedArray.getDimensionPixelOffset(
                    R.styleable.BlueWaterLayout_waterBottomHeight,
                    0
                )
            Log.i(TAG, "constructor: waterBottomHeight:$waterBottomHeight")*/
            paddingPetAreaLeft =
                typedArray.getDimensionPixelOffset(
                    R.styleable.BlueWaterLayout_paddingPetAreaLeft,
                    0
                )
        } finally {
            typedArray.recycle()
        }
    }


    override fun notifyDataSetChanged() {
        //pause()
        fishEntityList.forEachIndexed { _, entity ->
            if (entity is PetFish) {
                vgPetView.onChanged(entity)
            } else if (entity is Shell) {
                if (entity.shellResId != 0) {
                    ivAIShell.setImageResource(entity.shellResId)
                }
                if (entity.stoneResId != 0) {
                    ivStone.setImageResource(entity.stoneResId)
                }
            }
        }
        //resume()
    }

    override fun setFishData(entityList: MutableList<FishEntity>) {
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

    fun restart() {
    }

    fun pause() {
        ivAIShell.drawable?.let {
            (it as GifDrawable).stop()
        }

    }

    fun resume() {
        ivAIShell.drawable?.let {
            (it as GifDrawable).run {
                reset()
            }
        }
    }

    fun end() {
        isStarted = false
        isEnd = true
    }


    fun setOnItemClickListener(l: OnClickListener?) {
        mOnItemListener = l
    }

}


/*override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        Log.i(TAG, "onWindowFocusChanged: ")
        if (hasWindowFocus) {
            if (isStarted && !isRunning) {
                if (petPathList.isNotEmpty()) {
                    petObjectAnimatorList[0].run {
                        if (!isStarted) {
                            start()
                            this@BlueWaterLayout.isRunning = true
                        }
                    }
                }
            }
            //resume()
        } else {
            //pause()
        }
    }*/