package com.water.fish.holder

import android.content.res.Resources
import com.water.fish.FishEntity
import pl.droidsonroids.gif.AnimationListener
import pl.droidsonroids.gif.GifDrawable

/**
 * Created by zxn on 2021/5/28.moveLeftDrawable
 */
data class DrawableHolder(
    var moveLeftDrawable: GifDrawable,
    var moveRightDrawable: GifDrawable,
    var turnRightDrawable: GifDrawable,
    var turnLeftDrawable: GifDrawable,
) {
    companion object {
        fun create(
            res: Resources,
            entity: FishEntity,
            turnRightListener: AnimationListener? = null
        ) =
            DrawableHolder(
                GifDrawable(res, entity.moveLeftResId),
                GifDrawable(res, entity.moveRightResId),
                GifDrawable(res, entity.turnRightResId).apply {
                    loopCount = 1
                    turnRightListener?.let {
                        //addAnimationListener(it)
                    }
                },
                GifDrawable(res, entity.turnLeftResId),
            )
    }
}