package com.water.fish.listener

import android.animation.Animator
import com.water.fish.FishStatus
import com.water.fish.widget.IMarineView

/**
 *  Created by zxn on 2021/6/4.
 **/
class ShoalAnimatorListener(view: IMarineView, animationList: List<Animator>) :
    BaseAnimatorListenerAdapter(view, animationList) {

    companion object {
        private const val TAG = "ShoalAnimatorListener"
    }

    private var mFishStatus = FishStatus.MOVE_RIGHT

    override fun onAnimationStart(animation: Animator, position: Int, marineView: IMarineView) {
        mFishStatus = FishStatus.MOVE_RIGHT
        marineView.start(animation)
    }

    override fun onAnimationEnd(animation: Animator, position: Int, marineView: IMarineView) {

    }

    override fun onAnimationRepeat(animation: Animator, position: Int, marineView: IMarineView) {
        when (mFishStatus) {
            FishStatus.MOVE_RIGHT -> marineView.turnLeft(animation)
            else -> marineView.turnRight(animation)
        }
        mFishStatus =
            if (mFishStatus == FishStatus.MOVE_LEFT) FishStatus.MOVE_RIGHT else FishStatus.MOVE_LEFT
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        animationList[currentPathIndex].run {
            if (hasWindowFocus) {
                if (!isStarted && !isRunning) {
                    start()
                } else {
                    if (this.isPaused) {
                        this.resume()
                    }
                }
            } else {
                if (this.isRunning) {
                    this.pause()
                }
            }
        }
    }

}