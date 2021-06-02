package com.water.fish.utils

import android.content.Context

/**
 *  Created by zxn on 2021/6/1.
 **/
fun dp2px(dp: Float, context: Context): Int {
    val scale: Float = context.resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}


//    protected fun dp2px(dp: Float): Int {
//        val scale: Float = mContext.getResources().getDisplayMetrics().density
//        return (dp * scale + 0.5f).toInt()
//    }
//
//    protected fun sp2px(sp: Float): Int {
//        val scale: Float = this.mContext.getResources().getDisplayMetrics().scaledDensity
//        return (sp * scale + 0.5f).toInt()
//    }