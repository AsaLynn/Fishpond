package com.water.fish.utils

import android.content.Context

/**
 *  Created by zxn on 2021/6/1.
 **/


fun dp2px(dp: Float, context: Context): Int {
    val scale: Float = context.resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}