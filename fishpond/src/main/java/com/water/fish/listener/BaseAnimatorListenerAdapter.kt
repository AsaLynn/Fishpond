package com.water.fish.listener

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import com.water.fish.widget.IMarineView

/**
 *  Created by zxn on 2021/6/2.
 **/
abstract class BaseAnimatorListenerAdapter<T : IMarineView>(
    val marineView: T,
    var animationList: List<Animator>
) : AnimatorListenerAdapter() {

    /**
     * 当前移动的路线索引.
     */
    var currentPathIndex: Int = 0
        protected set

    override fun onAnimationStart(animation: Animator) {
        onAnimationStart(animation, currentPathIndex, marineView)
    }

    override fun onAnimationEnd(animation: Animator) {
        onAnimationEnd(animation, currentPathIndex, marineView)
        currentPathIndex++
        currentPathIndex %= animationList.size
    }

    override fun onAnimationRepeat(animation: Animator) {
        onAnimationRepeat(animation, currentPathIndex, marineView)
    }

    abstract fun onAnimationStart(animation: Animator, position: Int, marineView: T)

    abstract fun onAnimationEnd(animation: Animator, position: Int, marineView: T)

    abstract fun onAnimationRepeat(animation: Animator, position: Int, marineView: T)

    abstract fun onWindowFocusChanged(hasWindowFocus: Boolean)

    abstract fun start()

    abstract fun end()
}