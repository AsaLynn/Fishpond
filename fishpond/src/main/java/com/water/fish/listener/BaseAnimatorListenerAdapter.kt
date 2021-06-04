package com.water.fish.listener

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.util.Log
import com.water.fish.widget.IMarineView

/**
 *  Created by zxn on 2021/6/2.
 **/
abstract class BaseAnimatorListenerAdapter(
    val marineView: IMarineView,
    var animationList: List<Animator>
) : AnimatorListenerAdapter() {

    /**
     * 当前移动的路线索引.
     */
    var currentPathIndex: Int = 0
        private set

    override fun onAnimationStart(animation: Animator) {
        onAnimationStart(animation, currentPathIndex, marineView)
    }

    override fun onAnimationEnd(animation: Animator) {
        onAnimationEnd(animation, currentPathIndex, marineView)
        currentPathIndex++
        currentPathIndex %= animationList.size
    }

    override fun onAnimationRepeat(animation: Animator) {
        onAnimationRepeat(animation,currentPathIndex,marineView)
    }

    override fun onAnimationPause(animation: Animator) {

    }

    override fun onAnimationResume(animation: Animator) {

    }

    abstract fun onAnimationStart(animation: Animator, position: Int, marineView: IMarineView)

    abstract fun onAnimationEnd(animation: Animator, position: Int, marineView: IMarineView)

    abstract fun onAnimationRepeat(animation: Animator, position: Int, marineView: IMarineView)

    abstract fun onWindowFocusChanged(hasWindowFocus: Boolean)
}