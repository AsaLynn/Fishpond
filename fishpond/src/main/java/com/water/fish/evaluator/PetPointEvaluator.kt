package com.water.fish.evaluator

import android.animation.TypeEvaluator
import android.graphics.Point
import android.util.Log

/**
 *  Created by zxn on 2021/5/27.
 **/
class PetPointEvaluator(var list: MutableList<Point>) : TypeEvaluator<Point> {

    companion object {
        private const val TAG = "PetPointEvaluator"
    }

//    constructor(list: MutableList<Point>)

    /**
     * When null, a new PointF is returned on every evaluate call. When non-null,
     * mPoint will be modified and returned on every evaluate.
     */
    private val mPoint: Point = Point()

    /**
     * This function returns the result of linearly interpolating the start and
     * end PointF values, with <code>fraction</code> representing the proportion
     * between the start and end values. The calculation is a simple parametric
     * calculation on each of the separate components in the PointF objects
     * (x, y).
     *
     * <p>If {@link #PointFEvaluator(android.graphics.PointF)} was used to construct
     * this PointFEvaluator, the object returned will be the <code>reuse</code>
     * passed into the constructor.</p>
     *
     * @param fraction   The fraction from the starting to the ending values
     * @param startValue The start PointF
     * @param endValue   The end PointF
     * @return A linear interpolation between the start and end values, given the
     *         <code>fraction</code> parameter.
     */
    override fun evaluate(fraction: Float, startValue: Point, endValue: Point): Point {
        //表示动画完成度（根据它来计算当前动画的值）
        Log.i(TAG, "evaluate: fraction:$fraction")
        //val x = startValue.x + fraction * (endValue.x - startValue.x)
        //val x = startValue.x + fraction * (endValue.x - startValue.x)
        //val y = startValue.y + fraction * (endValue.y - startValue.y)
        //mPoint.set(x.toInt(), y.toInt())

        return when (fraction) {
            0F -> {
                mPoint.set(list[0].x, list[0].y)
                mPoint
            }
            0.1F -> {
                mPoint.set(list[1].x, list[1].y)
                mPoint
            }
            0.2F -> {
                mPoint.set(list[2].x, list[2].y)
                mPoint
            }
            else -> {
                val x = startValue.x + fraction * (endValue.x - startValue.x)
                val y = startValue.y + fraction * (endValue.y - startValue.y)
                mPoint.set(x.toInt(), y.toInt())
                mPoint
            }
        }
    }

}