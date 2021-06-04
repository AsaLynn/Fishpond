package com.water.fish.widget

import android.animation.Animator
import android.graphics.RectF
import com.water.fish.FishEntity

/**
 *  海产
 *  Created by zxn on 2021/6/2.
 **/
interface IMarineView {

    companion object {
        const val SPEED_MOVE_SECOND = 1000L
    }

    /**
     * 初始化活动范围.
     */
    fun onInitMovement(rectF: RectF)

    fun moveLeft()

    fun turnRight()

    fun turnRight(nextAnimator: Animator)

    fun moveRight()

    fun turnLeft()

    fun turnLeft(nextAnimator: Animator)

    /**
     * 在当前动画路线休息逗留.
     * @param position 当前路线索引.
     * @param nextAnimator 下个路线Animator
     */
    fun rest(position: Int, nextAnimator: Animator)

    /**
     *开始游动.
     */
    fun start(nextAnimator: Animator)

    fun  onChanged(entity: FishEntity)

    var moveSpeed: Long

}
