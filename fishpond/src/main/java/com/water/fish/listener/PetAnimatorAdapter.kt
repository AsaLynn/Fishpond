package com.water.fish.listener

import android.animation.Animator
import com.water.fish.widget.IWhaleView

/**
 *  Created by zxn on 2021/6/2.
 **/
class PetAnimatorAdapter(view: IWhaleView, animationList: List<Animator>) :
    BaseAnimatorListenerAdapter<IWhaleView>(view, animationList) {


    override fun onAnimationStart(animation: Animator, position: Int, marineView: IWhaleView) {
        if (!mStarted || !mRunning || mPaused || mAnimationEndRequested) return
        when (position) {
            0, 1, 3, 5, 6 -> marineView.moveLeft()

            2, 4, 7 -> marineView.moveRight()
        }
    }

    override fun onAnimationEnd(animation: Animator, position: Int, marineView: IWhaleView) {

        if (!mStarted || !mRunning || mPaused || mAnimationEndRequested) return

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

    override fun onAnimationRepeat(animation: Animator, position: Int, marineView: IWhaleView) {}

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        mEnabled = true
        if (hasWindowFocus && !mAnimationEndRequested && !mStarted && !mRunning && !mPaused) {
            start()
        }

        if (hasWindowFocus) {
            resume()
        } else {
            //mPaused = true
            pause()
        }
    }

    fun notifyDataSetChanged() {
        when (currentPathIndex) {
            0, 3, 5 -> marineView.moveLeft()

            2, 4, 7 -> marineView.moveRight()
        }
    }

    private var mStarted = false

    private var mRunning = false

    private var mAnimationEndRequested = false

    private var mPaused = false

    private var mResumed = false

    override fun end() {
        mStarted = false
        mRunning = true
        mAnimationEndRequested = true
        endAnimation()
    }

    override fun start() {
        mStarted = true
        mRunning = false
        mAnimationEndRequested = false
        startAnimation()

    }

    private fun startAnimation() {
        mAnimationEndRequested = false
        mRunning = true
        marineView.start(animationList[currentPathIndex])
    }

    private fun endAnimation() {
        mRunning = false
        animationList[currentPathIndex].end()
        //animationList[currentPathIndex].pause()
    }

    fun restart() {
        if (!mStarted
            && !mRunning
            && mEnabled
            && !mPaused
            && mAnimationEndRequested
        ) {
            mStarted = true
            mRunning = false
            mAnimationEndRequested = false
            //currentPathIndex = 0
            startAnimation()
        }
    }

    fun pause() {
        if (mStarted && mRunning && mEnabled && !mAnimationEndRequested && !mPaused) {
            mPaused = true
            mResumed = false
            marineView.pause(animationList[currentPathIndex])
        }
    }

    private fun resume() {
        if (mStarted
            && mRunning
            && mEnabled
            && !mAnimationEndRequested
            && mPaused
            && !mResumed
        ) {
            mPaused = false
            mResumed = true
            marineView.resume(animationList[currentPathIndex])
        }
    }

    private var mEnabled = false

}