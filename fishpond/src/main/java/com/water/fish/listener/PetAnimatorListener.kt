package com.water.fish.listener

import android.animation.Animator
import com.water.fish.BlueWaterLayout

/**
 * 0:p0->p1.
 * 1:p1->p2.
 * 2:p2->p3->p4->p5.
 * 3:p5->p6.
 * 4:p6->p7.
 * 5:p7->p8.
 * 6:p8->p9->p10->p11.
 * 7:p11->p12.
 * Created by zxn on 2021/5/30.
 */
class PetAnimatorListener(private val index: Int, val layout: BlueWaterLayout) : Animator.AnimatorListener {

    override fun onAnimationStart(animation: Animator) {
        layout.onPetAnimationStart(index)
    }

    override fun onAnimationEnd(animation: Animator) {
        layout.onPetAnimationEnd(index)
    }

    override fun onAnimationCancel(animation: Animator) {

    }

    override fun onAnimationRepeat(animation: Animator) {

    }
}