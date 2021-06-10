package com.water.fish.widget

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.water.fish.FishEntity
import com.water.fish.FishStatus
import com.water.fish.PetFish
import com.water.fish.R
import com.water.fish.listener.PetAnimatorAdapter
import com.water.fish.widget.IMarineView.Companion.SPEED_MOVE_SECOND
import pl.droidsonroids.gif.GifImageView

/**
 *  https://blog.csdn.net/Jiang_Rong_Tao/article/details/58635019
 *  鱼宠View.
 *  Created by zxn on 2021/6/1.
 **/
class PetView : ConstraintLayout, View.OnClickListener, IWhaleView {

    companion object {
        private const val TAG = "PetView"
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setWillNotDraw(false)
        LayoutInflater.from(context).inflate(R.layout.layout_pet_constraint, this)
    }

    private val ivPetFish: GifImageView by lazy {
        findViewById<GifImageView>(R.id.ivFish).apply {
            setOnClickListener(this@PetView)
        }
    }

    private val tvTips by lazy {
        findViewById<TextView>(R.id.tvTips).apply {
            setOnClickListener(this@PetView)
        }
    }

    private var mOnPetClickListener: OnClickListener? = null

    private var mPetFish: PetFish? = null

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
    private val petPathList = mutableListOf<Path>()

    private val petAnimatorList = mutableListOf<Animator>()

    private val mPetAnimatorAdapter =
        PetAnimatorAdapter(this, petAnimatorList)

    /**
     * 休息的秒数
     */
    private var restDuration = 2

    /**
     * 转身持续时间.turnDuration
     */
    private val turnDuration = 60L

    private val mPathMeasure = PathMeasure()

    override var moveSpeed = 10L

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        Log.i(TAG, "onWindowFocusChanged: $hasWindowFocus")
        mPetAnimatorAdapter.onWindowFocusChanged(hasWindowFocus)
    }

    @SuppressLint("Recycle")
    override fun onInitMovement(rectF: RectF) {
        petPathList.clear()
        for (index in 0 until 8) {
            val path = Path()
            rectF.run {
                when (index) {
                    //path0
                    0 -> {
                        path.moveTo(right, bottom)
                        path.lineTo(centerX(), centerY())
                    }
                    //path1
                    1 -> {
                        path.moveTo(centerX(), centerY())
                        path.lineTo(left, centerY())
                    }
                    //path2
                    2 -> {
                        path.moveTo(left, centerY())
                        path.lineTo(right, top)
                        path.lineTo(right, centerY())
                        path.lineTo(right, top)
                    }
                    //path3
                    3 -> {
                        path.moveTo(right, top)
                        path.lineTo(left, top)
                    }
                    //path4
                    4 -> {
                        path.moveTo(left, top)
                        path.lineTo(right, centerY())
                    }
                    //path5
                    5 -> {
                        path.moveTo(right, centerY())
                        path.lineTo(centerX(), centerY())
                    }
                    //path6
                    6 -> {
                        path.moveTo(centerX(), centerY())
                        path.lineTo(left, bottom)
                        path.lineTo(left, centerY())
                        path.lineTo(left, bottom)
                    }
                    //path7
                    7 -> {
                        path.moveTo(left, bottom)
                        path.lineTo(right, bottom)
                    }
                }
            }
            mPathMeasure.setPath(path, false)
            val moveDuration = (mPathMeasure.length.toLong() * SPEED_MOVE_SECOND / moveSpeed)
            val objectAnimator = ObjectAnimator.ofFloat(this, View.X, View.Y, path).apply {
                addListener(mPetAnimatorAdapter)
                interpolator = LinearInterpolator()
                duration = moveDuration
            }
            petAnimatorList.add(objectAnimator)
        }
    }

    override fun moveLeft() {
        mPetFish?.let {
            ivPetFish.setImageResource(it.toLeftImageRes())
        }
    }

    override fun moveRight() {
        mPetFish?.let {
            ivPetFish.setImageResource(it.toRightImageRes())
        }
    }

    override fun pause(animation: Animator) {
        animation.pause()
    }

    override fun resume(animation: Animator) {
        animation.resume()
    }

    override fun turnRight(nextAnimator: Animator) {
        mPetFish?.let {
            ivPetFish.setImageResource(it.turnRightImageRes())
        }
        nextAnimator.startDelay = turnDuration
        nextAnimator.start()
    }

    override fun turnLeft(nextAnimator: Animator) {
        mPetFish?.let {
            ivPetFish.setImageResource(it.turnLeftImageRes())
        }
        nextAnimator.startDelay = turnDuration
        nextAnimator.start()
    }

    override fun travel() {
        mPetAnimatorAdapter.end()
        visibility = View.GONE
    }

    override fun rest(position: Int, nextAnimator: Animator) {
        nextAnimator.startDelay = restDuration * SPEED_MOVE_SECOND
        nextAnimator.start()
    }

    override fun start(nextAnimator: Animator) {
        if (!nextAnimator.isStarted && !nextAnimator.isRunning) {
            nextAnimator.startDelay = turnDuration
            nextAnimator.start()
        }
    }

    override fun onChanged(entity: FishEntity) {
        mPetFish = entity as PetFish
        mPetFish?.let {
            if (it.moveSpeed > 0) {
                moveSpeed = it.moveSpeed
            }
            when (it.fishStatus) {
                FishStatus.EXCITING, FishStatus.FLESH_UP, FishStatus.BEAUTIFY -> {
                    if (visibility != View.VISIBLE) visibility = VISIBLE
                    postDelayed({
                        it.updateFishStatus(it.lastStatus)
                        mPetAnimatorAdapter.notifyDataSetChanged()
                    }, it.recoverDelayMillis)
                    mPetAnimatorAdapter.notifyDataSetChanged()
                }
                FishStatus.TRAVEL -> {
                    travel()
                }
                else -> {
                    if (visibility != View.VISIBLE) visibility = VISIBLE
                    mPetAnimatorAdapter.notifyDataSetChanged()
                    mPetAnimatorAdapter.restart()
                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ivFish -> {
                if (tvTips.visibility != View.VISIBLE) {
                    mPetFish?.tips()?.let {
                        tvTips.text = it
                    }
                    tvTips.visibility = View.VISIBLE
                    postDelayed({
                        tvTips.visibility = View.GONE
                    }, 6 * 1000L)
                }
            }
            else -> mOnPetClickListener?.onClick(v)
        }
    }

    fun setOnPetClickListener(l: OnClickListener?) {
        mOnPetClickListener = l
    }

}