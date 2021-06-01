package com.water.fish.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.water.fish.PetFish
import com.water.fish.R
import pl.droidsonroids.gif.GifImageView

/**
 *  鱼宠View.
 *  Created by zxn on 2021/6/1.
 **/
class PetView : ConstraintLayout, View.OnClickListener {

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

    private var mOnPetClickListener: OnClickListener? = null

    private var mPetFish: PetFish? = null

    fun onMoveLeft() {
        mPetFish?.let {
            ivPetFish.setImageResource(it.toLeftImageRes())
        }
    }

    fun onTurnRight() {
        mPetFish?.let {
            ivPetFish.setImageResource(it.turnRightResId)
        }
    }

    fun onMoveRight() {
        mPetFish?.let {
            ivPetFish.setImageResource(it.toRightImageRes())
        }
    }

    fun onTurnLeft() {
        mPetFish?.let {
            ivPetFish.setImageResource(it.turnLeftResId)
        }
    }

    fun onChanged(pointIndex: Int, petFish: PetFish) {
        mPetFish= petFish
        mPetFish?.let {
            when (pointIndex) {
                0, 1, 5, 7, 8, 9, 10, 12 -> {
                    ivPetFish.setImageResource(it.toLeftImageRes())
                }
                2, 3, 4, 6, 11 -> {
                    ivPetFish.setImageResource(it.toRightImageRes())
                }
            }
        }
    }

    override fun onClick(v: View) {
        mOnPetClickListener?.onClick(v)
    }

    fun setOnPetClickListener(l: OnClickListener?) {
        mOnPetClickListener = l
    }

}