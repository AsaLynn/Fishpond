package com.water.fish.listener

import android.animation.ValueAnimator
import android.util.Log
import com.water.fish.BlueWaterLayout

/**
 * 0:p0->p1.
 * 1:p1->p2.
 * 2:p2->p3->p4->p5.
 * 3:p5->p6.
 * 4:p6->p7.
 * 5:p7->p8.
 * 6:p8->p9->p10->p11.
 * 7:p11->p12
 * Created by zxn on 2021/5/30.
 */
class PetUpdateListener(val index: Int, val layout: BlueWaterLayout) :
    ValueAnimator.AnimatorUpdateListener {
    companion object {
        private const val TAG = "PetUpdateListener"
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        val x = animation.getAnimatedValue(animation.values[0].propertyName) as Float
        val y = animation.getAnimatedValue(animation.values[1].propertyName) as Float
        Log.i(TAG, "addUpdateListener:(x:$x,y:$y) ")


    }


}