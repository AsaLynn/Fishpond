package com.water.fish.listener

import android.animation.Animator
import com.water.fish.widget.IMarineView

/**
 *  Created by zxn on 2021/6/2.
 **/
class PetAnimatorListenerAdapter(view: IMarineView, animationList: List<Animator>) :
    BaseAnimatorListenerAdapter(view, animationList) {


    override fun onAnimationStart(animation: Animator, position: Int, marineView: IMarineView) {
        when (position) {
            0, 3, 5 -> marineView.moveLeft()

            2, 4, 7 -> marineView.moveRight()
        }
    }


    override fun onAnimationEnd(animation: Animator, position: Int, marineView: IMarineView) {
        val nextPosition = (position + 1) % animationList.size
        when (position) {
            0, 5 -> {
                animationList.let {
                    marineView.rest(position, it[nextPosition])
                }
            }
            1, 3, 6 -> {
                animationList.let {
                    marineView.turnRight(it[nextPosition])
                }
            }
            2, 4 -> {
                animationList.let {
                    marineView.turnLeft(it[nextPosition])
                }
            }
            7 -> {
                animationList.let {
                    marineView.start(it[nextPosition])
                }
            }
        }
    }

    fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        if (hasWindowFocus) {
            animationList?.let {
                it[currentPathIndex].run {
                    if (!isStarted && !isRunning) {
                        start()
                    }
                }
            }
        }
    }

    fun notifyDataSetChanged(){
        when (currentPathIndex) {
            0, 3, 5 -> marineView.moveLeft()

            2, 4, 7 -> marineView.moveRight()
        }
    }

}