package com.water.fish.widget

import android.animation.Animator
import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.water.fish.FishEntity
import com.water.fish.R
import com.water.fish.Shell
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView

/**
 *  Created by zxn on 2021/6/4.
 **/
class ShellView : ConstraintLayout, IMarineView {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setWillNotDraw(false)
        LayoutInflater.from(context).inflate(R.layout.layout_shell_constraint, this)
    }

    private val ivStone by lazy {
        findViewById<ImageView>(R.id.ivStone)
    }

    private val ivAIShell by lazy {
        findViewById<GifImageView>(R.id.ivAIShell)
    }

    override fun onInitMovement(rectF: RectF) {

    }



    override fun turnRight(nextAnimator: Animator) {

    }



    override fun turnLeft(nextAnimator: Animator) {

    }


    override fun start(nextAnimator: Animator) {

    }

    override fun onChanged(entity: FishEntity) {
        if (entity is Shell) {
            if (entity.shellResId != 0) {
                ivAIShell.setImageResource(entity.shellResId)
            }
            if (entity.stoneResId != 0) {
                ivStone.setImageResource(entity.stoneResId)
            }
        }
    }

    override var moveSpeed: Long = 0

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        ivAIShell.background?.let {
            (it as GifDrawable).run {
                if (hasWindowFocus) {
                    if (!this.isRunning) {
                        this.start()
                    }
                } else {
                    if (this.isRunning) {
                        this.stop()
                    }
                }
            }
        }
    }
}