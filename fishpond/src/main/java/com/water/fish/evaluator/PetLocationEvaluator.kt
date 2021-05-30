package com.water.fish.evaluator

import android.animation.FloatEvaluator
import android.animation.TypeEvaluator
import kotlin.math.abs

/**
 * Created by zxn on 2021/5/30.
 */
class PetLocationEvaluator(var array: FloatArray) : TypeEvaluator<PetLocation> {
    companion object{
        private const val LENGTH_AMOUNT_ERROR = 2
    }
    private val lengthEvaluator = FloatEvaluator()

    /**
     * 此函数返回从开始一直到结束的值
     * @param fraction  表示占据开始值和结束值之间的比例
     * @param startValue 动画起始值
     * @param endValue  动画结束值
     * @return 通过fraction计算出从开始到结束期间的线性变化值
     */
    override fun evaluate(
        fraction: Float,
        startValue: PetLocation,
        endValue: PetLocation
    ): PetLocation {
        val length = evaluateLength(fraction, startValue, endValue)
        val position = evaluatePosition(length)
        return PetLocation(length, position)
    }

    private fun evaluateLength(
        fraction: Float,
        startValue: PetLocation,
        endValue: PetLocation
    ): Float = lengthEvaluator.evaluate(fraction, startValue.length, endValue.length)

    /**
     * @return [-1到12,-1表示游行中,0-12表示关键位置索引.]
     */
    private fun evaluatePosition(length: Float): Int = when {

        abs(length - array[0]) <= LENGTH_AMOUNT_ERROR -> 0

        abs(length - array[1]) <= LENGTH_AMOUNT_ERROR -> 1

        abs(length - array[2]) <= LENGTH_AMOUNT_ERROR -> 2

        abs(length - array[3]) <= LENGTH_AMOUNT_ERROR -> 3

        abs(length - array[4]) <= LENGTH_AMOUNT_ERROR -> 4

        abs(length - array[5]) <= LENGTH_AMOUNT_ERROR -> 5

        abs(length - array[6]) <= LENGTH_AMOUNT_ERROR -> 6

        abs(length - array[7]) <= LENGTH_AMOUNT_ERROR -> 7

        abs(length - array[8]) <= LENGTH_AMOUNT_ERROR -> 8

        abs(length - array[9]) <= LENGTH_AMOUNT_ERROR -> 9

        abs(length - array[10]) <= LENGTH_AMOUNT_ERROR -> 10

        abs(length - array[11]) <= LENGTH_AMOUNT_ERROR -> 11

        abs(length - array[12]) <= LENGTH_AMOUNT_ERROR -> 12

        else -> -1
    }

}

/**
 * 自定义估值器的值类型
 * @param length 当前Path处的长度
 * @param position  关键坐标点位置[0-12]
 */
class PetLocation(var length: Float, var position: Int)