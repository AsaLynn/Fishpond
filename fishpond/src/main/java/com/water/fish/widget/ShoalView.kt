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