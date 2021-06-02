package com.water.fish

import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.Point
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlin.math.abs

/**
 *  鱼的基类,框定类型.
 *  Created by zxn on 2021/3/30.
 **/
sealed class FishEntity {

    companion object {
        private const val TAG = "FishEntity"
    }

    var moveSpeed = 10L

    var moveLeftResId: Int = 0

    var moveRightResId: Int = 0

    var turnLeftResId: Int = 0

    var turnRightResId: Int = 0

    /**
     * 标记鱼是否游动中,true:游动中
     */
    var isSwimming = true

    /**
     * 鱼的坐标轨迹点.
     */
    protected var pointList =
        mutableListOf<Point>()


    private val pathMeasure by lazy {
        //强制闭合路径.
        PathMeasure(swimPath, false)
    }

    private val pos = FloatArray(2)

    private val tan = FloatArray(2)

    val swimPath by lazy {

        createPath().also {
            if (it.isEmpty) {
                val points = mutableListOf(
                    Point(1000, 1000),
                    Point(100, 1000),
                    Point(100, 200),
                    Point(1000, 200),
                    Point(1000, 1000)
                )
                for ((index, point) in points.withIndex()) {
                    if (index == 0) {
                        it.moveTo(point.x.toFloat(), point.y.toFloat())
                    } else {
                        it.lineTo(point.x.toFloat(), point.y.toFloat())
                    }
                }
            }
        }
    }

    /**
     * 用于摆放的坐标点
     */
    val layoutPoint by lazy {
        Point()
    }

    /**
     * 获取下一步游动的轨迹坐标点
     */
    fun swimmingPoint(progress: Float, fishView: ImageView): Point {
        val distance = (pathMeasure.length * progress)
        Log.i(TAG, "swimmingPoint:distance: $distance")

        //得到distance，位置的点的坐标，和在个点与path进行切线的正切，分别放在pos[] 和tan[]中。
        pathMeasure.getPosTan(
            distance,
            pos,
            tan
        )

        //根据不同的坐标点位置,回调游动方法并给予游动状态.
        val x = pos[0]
        val y = pos[1]
        Log.i(TAG, "swimmingPoint: x:$x,y:$y")

        onSwimming(x, y, distance, fishView)

        return layoutPoint.also {
            it.set(x.toInt(), y.toInt())
        }
    }


    /**
     * 获取鱼的游戏路径
     */
    open fun createPath(): Path = Path()

    /**
     * 添加轨迹坐标点.
     */
    fun addPoint(x: Int, y: Int) {
        pointList.add(Point(x, y))
    }

    /**
     * 是否已经添加过了路径的坐标点
     * true:装载过了,false:未装载.
     */
    fun isAdded(): Boolean = pointList.isNotEmpty()

    /**
     * 鱼的游动状态,默认向左上方游动.
     */
    var fishStatus = FishStatus.MOVE_TOP_LEFT
        protected set

    /**
     * 鱼的上个游动状态
     */
    var lastFishStatus = FishStatus.MOVE_TOP_LEFT
        protected set

    /**
     * 当前鱼的展示状态,初始值向左
     */
    var skinResId = moveLeftResId


    /**
     * 判断是否在指定坐标点内
     * 如果某点的x坐标和y坐标和指定的点x坐标和y坐标,分别相差10以内则认为在该点坐标上.
     * @param point 指定坐标点
     * @param x 某点的x坐标
     * @param y 某点的y坐标
     * @return true:在指定坐标点内.
     */
    protected fun isInPointRect(point: Point, x: Float, y: Float): Boolean =
        abs(point.x - (x + 0.5F).toInt()) < 15 && abs(point.y - (y + 0.5F).toInt()) < 40
    //abs(point.x - (x + 0.5F).toInt()) < width / 10 && abs(point.y - (y + 0.5F).toInt()) < height / 10

    /**
     * 根据移动的坐标点和轨迹坐标点来修改小鱼的游动状态.
     * @param x
     * @param y
     * @param distance 距离起点的直线距离.
     */
    open fun onSwimming(x: Float, y: Float, distance: Float, fishView: ImageView) {

    }

}

/**
 * 鱼宠
 */
class PetFish : FishEntity() {

    companion object {
        private const val TAG = "FishEntity"
    }

    override fun createPath(): Path =
        Path().apply {
            for ((index, point) in this@PetFish.pointList.withIndex()) {
                if (index == 0) {
                    moveTo(point.x.toFloat(), point.y.toFloat())
                } else {
                    lineTo(point.x.toFloat(), point.y.toFloat())
                }
            }
        }

    var tipsList = mutableListOf<String>()

    fun tips(): String? {
        if (tipsList.isEmpty()) return null
        tipsList.shuffled().take(1)[0].also {
            //val mStringBuilder = StringBuilder()
            mStringBuilder.clear()
            return when (it.length) {
                in 0..7 -> it
                in 8..14 -> mStringBuilder.append(it.substring(0, 7))
                    .append("\n")
                    .append(it.substring(7)).toString()
                else -> mStringBuilder.append(it.substring(0, 7))
                    .append("\n")
                    .append(it.substring(7, 13))
                    .append("...")
                    .toString()
            }
        }
    }

    private val mStringBuilder by lazy {
        StringBuilder()
    }

    /**
     * 开始或者停止喷水
     * @param isEnabled true:开启喷水,false停止喷水.
     */
    fun spurt(isEnabled: Boolean) {
        if (isSpraying != isEnabled) {
            isSpraying = isEnabled
            isChange = true
        }
    }

    /**
     * 喷水左移
     */
    var spurtLeftResId: Int = 0

    /**
     * 喷水右移
     */
    var spurtRightResId: Int = 0

    /**
     * 开启喷水:true,停止喷水:false.
     */
    var isSpraying = false

    private var onSprayChangeListener: ((value: Int) -> Unit)? = null

    fun setSprayChangeListener(block: (value: Int) -> Unit) {
        onSprayChangeListener = block
    }

    /**
     * 根据移动的坐标点和轨迹坐标点来修改小鱼的游动状态.
     * @param x
     * @param y
     * @param distance 距离起点的直线距离.
     */
    override fun onSwimming(x: Float, y: Float, distance: Float, fishView: ImageView) {

        if (pointList.isEmpty()) {
            return
        }

        if (distance == 0F /*|| progress == 0F*/) {
            //P0,点处开始向左上游动.
            changeSwimming(FishStatus.TURN_LEFT, fishView)
            changeSwimming(FishStatus.MOVE_TOP_LEFT, fishView)
        }

        if (isInPointRect(pointList[1], x, y)) {
            //P1,P8处停留
            changeSwimming(FishStatus.REST, fishView)
        } else {
            when (lastFishStatus) {
                FishStatus.MOVE_TOP_LEFT -> {
                    if (fishStatus == FishStatus.REST) {
                        changeSwimming(FishStatus.MOVE_LEFT, fishView)
                    }
                }

                FishStatus.MOVE_LEFT -> {
                    if (fishStatus == FishStatus.REST) {
                        changeSwimming(FishStatus.MOVE_BOTTOM_LEFT, fishView)
                    }
                }

                else -> {
                    when {
                        //P2,P10
                        //P2点处向右转身,开始向右游动.
                        isInPointRect(pointList[2], x, y) -> {

                            //判定该点位于此P2
                            if (fishStatus == FishStatus.MOVE_LEFT) {
                                if (fishStatus != FishStatus.MOVE_TOP_RIGHT) {
                                    changeSwimming(FishStatus.TURN_RIGHT, fishView)
                                    changeSwimming(FishStatus.MOVE_TOP_RIGHT, fishView)
                                }
                            }
                            //P10->p11,下游
                            if (fishStatus == FishStatus.MOVE_TOP_ON_LEFT) {
                                changeSwimming(FishStatus.MOVE_BOTTOM_ON_LEFT, fishView)
                            }
                        }

                        //P3,P5,
                        isInPointRect(pointList[3], x, y) -> {
                            //P3,下游
                            if (fishStatus == FishStatus.MOVE_TOP_RIGHT) {
                                changeSwimming(FishStatus.MOVE_BOTTOM_ON_RIGHT, fishView)
                            }
                            //P5点处向左转身,开始向左游动.
                            if (fishStatus == FishStatus.MOVE_TOP_ON_RIGHT) {
                                changeSwimming(FishStatus.TURN_LEFT, fishView)
                                changeSwimming(FishStatus.MOVE_LEFT, fishView)
                            }
                        }

                        //P4,P7,
                        isInPointRect(pointList[4], x, y) -> {
                            //P4,上游
                            if (fishStatus == FishStatus.MOVE_BOTTOM_ON_RIGHT) {
                                changeSwimming(FishStatus.MOVE_TOP_ON_RIGHT, fishView)
                            }
                            //P7点处向左转身,开始向左游动.
                            if (fishStatus == FishStatus.MOVE_BOTTOM_RIGHT) {
                                changeSwimming(FishStatus.TURN_LEFT, fishView)
                                changeSwimming(FishStatus.MOVE_LEFT, fishView)
                            }
                        }

                        //P6,
                        isInPointRect(pointList[6], x, y) -> {
                            //P6点处向右转身,开始向右游动.
                            if (fishStatus == FishStatus.MOVE_LEFT) {
                                changeSwimming(FishStatus.TURN_RIGHT, fishView)
                                changeSwimming(FishStatus.MOVE_BOTTOM_RIGHT, fishView)
                                layoutPoint.set(pointList[6].x, pointList[6].y)
                            }
                        }

                        //P9,P11,
                        isInPointRect(pointList[9], x, y) -> {
                            //P9,垂直向上游
                            if (fishStatus == FishStatus.MOVE_BOTTOM_LEFT) {
                                changeSwimming(FishStatus.MOVE_TOP_ON_LEFT, fishView)
                            }
                            //P11,向右转身,向右移动
                            if (fishStatus == FishStatus.MOVE_BOTTOM_ON_LEFT) {
                                changeSwimming(FishStatus.TURN_RIGHT, fishView)
                                changeSwimming(FishStatus.MOVE_RIGHT, fishView)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *  改变小鱼的游动状态.
     * @param status 向左转,向右转,向左正常游动,向右正常游动.
     */
    private fun changeSwimming(@FishStatus status: Int, fishView: ImageView) {
        if (!isChange) {
            if (status == fishStatus) {
                return
            }
        } else {
            isChange = false
        }

        lastFishStatus = fishStatus
        fishStatus = status
        when (fishStatus) {
            FishStatus.TURN_LEFT -> {
                Log.i(TAG, "changeSwimming: 向左转身:$status")
                skinResId = turnLeftResId
            }
            FishStatus.TURN_RIGHT -> {
                Log.i(TAG, "changeSwimming: 向右转身:$status")
                skinResId = turnRightResId
            }
            FishStatus.MOVE_LEFT -> {
                Log.i(TAG, "changeSwimming: 水平向左:$status")
                skinResId = if (isSpraying) spurtLeftResId else moveLeftResId
            }
            FishStatus.MOVE_RIGHT -> {
                Log.i(TAG, "changeSwimming: 水平向右:$status")
                skinResId = if (isSpraying) spurtRightResId else moveRightResId
            }
            FishStatus.REST -> {
                Log.i(TAG, "changeSwimming: 休息:$status")
                isSwimming = false
                skinResId = if (isSpraying) spurtLeftResId else moveLeftResId
            }
            FishStatus.MOVE_TOP_RIGHT -> {
                Log.i(TAG, "changeSwimming: 向右上方:$status")
                skinResId = if (isSpraying) spurtRightResId else moveRightResId
            }
            FishStatus.MOVE_TOP_LEFT -> {
                Log.i(TAG, "changeSwimming: 向左上方:$status")
                skinResId = if (isSpraying) spurtLeftResId else moveLeftResId
            }
            FishStatus.MOVE_BOTTOM_ON_LEFT -> {
                Log.i(TAG, "changeSwimming: 面向左,垂直向下:$status")
                skinResId = if (isSpraying) spurtLeftResId else moveLeftResId
            }
            FishStatus.MOVE_TOP_ON_LEFT -> {
                Log.i(TAG, "changeSwimming: 面向左,垂直向上:$status")
                skinResId = if (isSpraying) spurtLeftResId else moveLeftResId
            }
            FishStatus.MOVE_BOTTOM_ON_RIGHT -> {
                Log.i(TAG, "changeSwimming: 面向右,垂直向下:$status")
                skinResId = if (isSpraying) spurtRightResId else moveRightResId
            }
            FishStatus.MOVE_TOP_ON_RIGHT -> {
                Log.i(TAG, "changeSwimming: 面向右,垂直向上:$status")
                skinResId = if (isSpraying) spurtRightResId else moveRightResId
            }
            FishStatus.MOVE_BOTTOM_RIGHT -> {
                Log.i(TAG, "changeSwimming: 向右下方:$status")
                skinResId = if (isSpraying) spurtRightResId else moveRightResId
            }
            FishStatus.MOVE_BOTTOM_LEFT -> {
                Log.i(TAG, "changeSwimming: 向左下方:$status")
                skinResId = if (isSpraying) spurtLeftResId else moveLeftResId
            }
        }
        //开始或者停止喷水.//.override(w, h)
        Glide.with(fishView).load(skinResId).into(fishView)
    }

    var isChange = false

    fun toLeftImageRes(): Int = if (isSpraying) spurtLeftResId else moveLeftResId

    fun toRightImageRes(): Int = if (isSpraying) spurtRightResId else moveRightResId
}

/**
 * 鱼群,ShoalFish
 */
class ShoalFish : FishEntity()

/**
 * 贝壳
 */
class Shell : FishEntity() {
    /**
     * 石头ID
     */
    var stoneResId: Int = 0

    /**
     * 贝壳
     */
    var shellResId: Int = 0


}


///**
// * 测试鱼Test,
// */
//class TestFish : FishEntity() {
//    override fun createPath(): Path =
//        Path().apply {
//            moveTo(1000f, 1000f)
//            lineTo(100f, 1000f)
//            lineTo(100f, 200f)
//            lineTo(1000f, 200f)
//            lineTo(1000f, 1000f)
//        }
//}



