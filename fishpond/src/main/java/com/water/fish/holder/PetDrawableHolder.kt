package com.water.fish.holder

import android.content.res.Resources
import com.water.fish.PetFish
import pl.droidsonroids.gif.GifDrawable

/**
 * Created by zxn on 2021/5/28.
 */
data class PetDrawableHolder(
    var moveLeftDrawable: GifDrawable,
    var moveRightDrawable: GifDrawable,
    var turnRightDrawable: GifDrawable,
    var turnLeftDrawable: GifDrawable,
    var spurtLeftDrawable: GifDrawable,
    var spurtRightDrawable: GifDrawable,
    var isSpraying: Boolean
) {
    companion object {
        fun create(
            res: Resources,
            entity: PetFish
        ) =
            PetDrawableHolder(
                GifDrawable(res, entity.moveLeftResId),
                GifDrawable(res, entity.moveRightResId),
                GifDrawable(res, entity.turnRightResId)
                    .apply {
                        loopCount = 1
                    },
                GifDrawable(res, entity.turnLeftResId).apply {
                    loopCount = 1
                },
                GifDrawable(res, entity.spurtLeftResId),
                GifDrawable(res, entity.spurtRightResId),
                entity.isSpraying
            )
    }

    fun toLeftDrawable(): GifDrawable = if (isSpraying) spurtLeftDrawable else moveLeftDrawable

    fun toRightDrawable(): GifDrawable = if (isSpraying) spurtLeftDrawable else moveLeftDrawable
}