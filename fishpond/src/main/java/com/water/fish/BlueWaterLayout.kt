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
import androidx.constraintlayout.widget.ConstraintLayout
import com.water.fish.widget.PetView
import com.water.fish.widget.ShellView
import com.water.fish.widget.ShoalView
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

    private val shellView by lazy {
        findViewById<ShellView>(R.id.shellView).apply {
            setOnClickListener(this@BlueWaterLayout)
        }
    }

    private val shoalView by lazy {
        findViewById<ShoalView>(R.id.shoalView)
    }

    private val fishEntityList = mutableListOf<FishEntity>()

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

//    private val ivAIShell by lazy {
//        findViewById<ImageView>(R.id.ivAIShell).also {
//            it.setOnClickListener(this)
//        }
//    }

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
        val petRectF = RectF().apply {
            left = (paddingLeft + paddingPetAreaLeft).toFloat()
            top = paddingTop.toFloat()
            right = width - paddingRight.toFloat() - vgPetView.measuredWidth
            bottom =
                height - paddingBottom.toFloat() - (vgPetView.layoutParams as LayoutParams).bottomMargin - vgPetView.measuredHeight
        }
        vgPetView.onInitMovement(petRectF)

        shoalView.onInitMovement(RectF().apply {
            left = paddingLeft.toFloat()
            top =
                (height - paddingBottom - shoalView.measuredHeight - (shoalView.layoutParams as LayoutParams).bottomMargin * 2).toFloat()
            right = (width - paddingRight).toFloat()
            bottom = (height - paddingBottom).toFloat()
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.shellView -> {
                mOnItemListener?.onClick(v)
            }
        }

    }

    private fun onInitAttributeSet(attrs: AttributeSet?) {
        val typedArray: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.BlueWaterLayout)
        try {
            paddingPetAreaLeft =
                typedArray.getDimensionPixelOffset(
                    R.styleable.BlueWaterLayout_paddingPetAreaLeft,
                    0
                )
        } finally {
            typedArray.recycle()
        }
    }

    /**
     * ShellView.
     */
    override fun notifyDataSetChanged(position: Int) {
        when (position) {
            0 -> vgPetView.onChanged(fishEntityList[0])
            1 -> shellView.onChanged(fishEntityList[1])
            2 -> shoalView.onChanged(fishEntityList[2])
        }
    }


    override fun notifyDataSetChanged() {
        //pause()
        fishEntityList.forEachIndexed { _, entity ->

            when (entity) {
                is PetFish -> {
                    vgPetView.onChanged(entity)
                }
                is Shell -> {
                    shellView.onChanged(entity)
                }
                is ShoalFish -> {
                    shoalView.onChanged(entity)
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
        /*ivAIShell.drawable?.let {
            (it as GifDrawable).stop()
        }*/
    }

    fun resume() {
        /*ivAIShell.drawable?.let {
            (it as GifDrawable).run {
                reset()
            }
        }*/
    }

    fun end() {
        isStarted = false
        isEnd = true
    }


    fun setOnItemClickListener(l: OnClickListener?) {
        mOnItemListener = l
    }

}