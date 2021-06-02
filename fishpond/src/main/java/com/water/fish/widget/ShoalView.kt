package com.water.fish.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.water.fish.R
import pl.droidsonroids.gif.GifImageView

/**
 *  (轨迹动画)
 *  https://blog.csdn.net/Jiang_Rong_Tao/article/details/58635019
 *  (贝塞尔曲线实现波浪动画)
 *  https://blog.csdn.net/zz51233273/article/details/107866070
 *  (绘制波浪线)
 *  https://blog.csdn.net/IT_XF/article/details/75014160
 *  Created by zxn on 2021/6/1.
 **/
class ShoalView : GifImageView {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setWillNotDraw(false)
        isClickable = false
        setBackgroundColor(Color.WHITE)
        setImageResource(R.mipmap.bg_fish_tips)
    }
}


/*fun testPathAnimator() {
        val path = Path().apply {
            moveTo(200f, 200f)
            lineTo(200F * 3, 200F)
            lineTo(200F * 3, 200F * 5)
            lineTo(200F * 1, 200F * 5)
            lineTo(200F * 3, 200F * 1)
        }
        val moveAnimator = ObjectAnimator.ofFloat(ivPetFish, "x", "y", path)
        moveAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                //Log.i(TAG, "onAnimationStart: ")
            }

            override fun onAnimationEnd(animation: Animator) {
                //l.removeView(imageView)
            }

            override fun onAnimationCancel(animation: Animator) {
                //l.removeView(imageView)
            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
        moveAnimator.addUpdateListener {
            *//*Log.i(
                TAG,
                "addUpdateListener:(x:${it.getAnimatedValue(it.values[0].propertyName)},y:${
                    it.getAnimatedValue(it.values[1].propertyName)
                }) "
            )*//*
        }
        moveAnimator.interpolator = LinearInterpolator()
        moveAnimator.duration = 5 * 1000
        moveAnimator.start()
    }*/


//petAnimatorSet.duration = petFishSpeed * 1000L
//petAnimatorSet.playSequentially(petObjectAnimatorList)

/*petAnimatorSet.play(petObjectAnimatorList[1])
    .after(restDuration * SPEED_MOVE_SECOND)
    .after(petObjectAnimatorList[0])

petAnimatorSet.play(petObjectAnimatorList[2])
    .after(petObjectAnimatorList[1])

petAnimatorSet.play(petObjectAnimatorList[3]).after(turnDuration)
    .after(petObjectAnimatorList[2])

petAnimatorSet.play(petObjectAnimatorList[4]).after(turnDuration)
    .after(petObjectAnimatorList[3])

petAnimatorSet.play(petObjectAnimatorList[5]).after(turnDuration)
    .after(petObjectAnimatorList[4])

petAnimatorSet.play(petObjectAnimatorList[6])
    .after(restDuration * SPEED_MOVE_SECOND)
    .after(petObjectAnimatorList[5])

petAnimatorSet.play(petObjectAnimatorList[7]).after(turnDuration)
    .after(petObjectAnimatorList[6])*/


//private fun initAnimation(type: Int, height: Int, iv: View) {
//    iv.visibility = FrameLayout.VISIBLE
//    val v = 1f
//    /**
//     * 旋转
//     */
//    var rotateAnimation: RotateAnimation? = null
//    var translateAnimation: TranslateAnimation? = null
//    val random = Random()
//    //val value = random.nextInt(50) + 1
//    //val value = random.nextInt(100) + 1
//    val value = 100
//    when (type) {
//        BlueWaterLayout.TYPE_1 -> {
//            translateAnimation = TranslateAnimation(0f, height.toFloat(), 0f, 0f)
//            rotateAnimation = RotateAnimation(
//                0F,
//                value.toFloat(),
//                RotateAnimation.RELATIVE_TO_SELF,
//                v,
//                RotateAnimation.RELATIVE_TO_SELF,
//                v
//            )
//        }
//        BlueWaterLayout.TYPE_2 -> {
//            translateAnimation = TranslateAnimation(0f, height.toFloat(), 0f, 0f)
//            rotateAnimation = RotateAnimation(
//                0F,
//                (-value).toFloat(),
//                RotateAnimation.RELATIVE_TO_SELF,
//                v,
//                RotateAnimation.RELATIVE_TO_SELF,
//                v
//            )
//        }
//        BlueWaterLayout.TYPE_3 -> {
//            translateAnimation = TranslateAnimation(0f, 1000F, 0f, 0f)
//            /*rotateAnimation = RotateAnimation(
//                0F,
//                (-value).toFloat(),
//                RotateAnimation.RELATIVE_TO_SELF,
//                v,
//                RotateAnimation.RELATIVE_TO_SELF,
//                v
//            )*/
//        }
//        BlueWaterLayout.TYPE_4 -> {
//            translateAnimation = TranslateAnimation(0f, height.toFloat(), 0f, 0f)
//            rotateAnimation = RotateAnimation(
//                0F,
//                value.toFloat(),
//                RotateAnimation.RELATIVE_TO_SELF,
//                v,
//                RotateAnimation.RELATIVE_TO_SELF,
//                v
//            )
//        }
//    }
//    /**
//     * 平移.
//     */
//    val animationSet = AnimationSet(true)
//    animationSet.duration = 15000
//    animationSet.interpolator = LinearInterpolator()
//    if (translateAnimation != null) {
//        animationSet.addAnimation(translateAnimation)
//        translateAnimation.repeatCount = Animation.INFINITE
//    }
//    if (rotateAnimation != null) {
//        animationSet.addAnimation(rotateAnimation)
//    }
//    iv.startAnimation(animationSet)
//}