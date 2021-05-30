package com.water.fish

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Path
import android.graphics.Point
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.util.*

/**
 *  鱼儿管理者
 *  Created by zxn on 2021/3/30.
 **/
class FishManager(private var context: Context?, private var width: Int, private var height: Int) {

    private val FRAME_INTERVAL = 150

    private val MAX_COUNT = 17 //最大数量

    private var fishWidth = 0

    private var fishHeight = 0

    private var random: Random = Random()

    private var fishs = mutableListOf<FishView>()

//    /**
//     * 创建一群鱼
//     */
//    fun createFishGroup(): List<FishView?>? {
//        for (i in 0 until MAX_COUNT) {
//            val fishModel = FishView(context!!)
//            fishModel.setAnim(getFishAnim())
//            fishModel.setPath(getRandomPath())
//            fishs.add(fishModel)
//        }
//        return fishs
//    }

//    /**
//     * 创建一只鱼
//     * @param fishBean
//     */
//    fun createFish(fishBean: FishEntity): FishView {
//        return when (fishBean) {
//           is Carp -> {
//                FishView(context!!).also {
//                    it.setAnim(getFishAnim())
//                    it.setPath(getRandomPath())
//                    fishs.add(it)
//                }
//            }
//            is PetFish -> {
//                FishView(context!!).also {
//                    it.setAnim(getFishAnim())
//                    it.setPath(getRandomPath())
//                    fishs.add(it)
//                }
//            }
//            else -> {
//                FishView(context!!).also {
//                    it.setAnim(getFishAnim())
//                    it.setPath(getTestPath())
//                    fishs.add(it)
//                }
//            }
//        }
//    }

    /**
     * 获取随机路线
     * @return
     */
    fun getRandomPath(): Path {
        val path = Path()
        val points: List<Point> = getRandomPoint()
        path.moveTo(points[0].x.toFloat(), points[0].y.toFloat())
        //cubicTo（）方法去描述一条三阶贝塞尔曲线
        path.cubicTo(
            points[1].x.toFloat(),
            points[1].y.toFloat(),
            points[2].x.toFloat(),
            points[2].y.toFloat(),
            points[3].x.toFloat(),
            points[3].y.toFloat()
        )
        return path
    }

//    /**
//     * 获取测试路线
//     * @return  Path 路线
//     */
//    private fun getTestPath(): Path {
//        val path = Path()
//        val points: List<Point> = getRandomPoint()
//        path.moveTo(100.0f, 100.0f)
//        //cubicTo（）方法去描述一条三阶贝塞尔曲线
//        path.cubicTo(
//            200f,
//            200f,
//            500f,
//            500f,
//            200f,
//            1000f
//        )
//        return path
//    }

    //获取贝塞尔4个随机点
    private fun getRandomPoint(): List<Point> {
        val num: Int = getRandomInt(200, false)
        return when (num % 4) {
            0 -> fromeLeft2Right()
            1 -> fromeBottom2Top()
            2 -> fromeRight2Left()
            else -> fromeTop2Bottom()
        }
    }

    /**
     * 获取一个随机数
     * @param region
     * @param isMinus 是否是负数
     * @return
     */
    private fun getRandomInt(region: Int, isMinus: Boolean): Int {
        return if (isMinus) {
            val temp = random.nextInt(100)
            if (temp % 2 == 0) {
                random.nextInt(region)
            } else -random.nextInt(region)
        } else {
            random.nextInt(region)
        }
    }

    //从上到下
    private fun fromeTop2Bottom(): List<Point> {
        var temp: Int
        val points: MutableList<Point> = ArrayList()
        val p1 = Point()
        p1.x = getRandomInt(width, false)
        p1.y = 0
        points.add(p1)
        val p2 = Point()
        temp = getRandomInt(width, true)
        p2.x = p1.x + temp
        p2.y = height / 3
        points.add(p2)
        val p3 = Point()
        temp = getRandomInt(width, true)
        p3.x = p1.x + temp
        p3.y = height / 3 * 2
        points.add(p3)
        val p4 = Point()
        p4.x = getRandomInt(width, false)
        p4.y = height + fishHeight
        points.add(p4)
        return points
    }

    //从下到上
    private fun fromeBottom2Top(): List<Point> {
        var temp: Int
        val points: MutableList<Point> = ArrayList()
        val p1 = Point()
        p1.x = getRandomInt(width, false)
        p1.y = height + fishHeight
        points.add(p1)
        val p2 = Point()
        temp = getRandomInt(width, true)
        p2.x = p1.x + temp
        p2.y = height / 3 * 2
        points.add(p2)
        val p3 = Point()
        temp = getRandomInt(width, true)
        p3.x = p1.x + temp
        p3.y = height / 3
        points.add(p3)
        val p4 = Point()
        p4.x = getRandomInt(width, false)
        p4.y = 0
        points.add(p4)
        return points
    }

    //从左到右
    private fun fromeLeft2Right(): List<Point> {
        var temp: Int
        val points: MutableList<Point> = ArrayList()
        val p1 = Point()
        p1.x = 0 - fishWidth
        p1.y = getRandomInt(height, false)
        points.add(p1)
        val p2 = Point()
        temp = getRandomInt(height, true)
        p2.x = width / 3
        p2.y = p1.y + temp
        points.add(p2)
        val p3 = Point()
        temp = getRandomInt(height, true)
        p3.x = width / 3 * 2
        p3.y = p1.y + temp
        points.add(p3)
        val p4 = Point()
        p4.x = width + fishHeight
        p4.y = getRandomInt(height, false)
        points.add(p4)
        return points
    }

    //从右到左
    private fun fromeRight2Left(): List<Point> {
        var temp: Int
        val points: MutableList<Point> = ArrayList()
        val p1 = Point()
        p1.x = width + fishHeight
        p1.y = getRandomInt(height, false)
        points.add(p1)
        val p2 = Point()
        temp = getRandomInt(height, true)
        p2.x = width / 3 * 2
        p2.y = p1.y + temp
        points.add(p2)
        val p3 = Point()
        temp = getRandomInt(height, true)
        p3.x = width / 3
        p3.y = p1.y + temp
        points.add(p3)
        val p4 = Point()
        p4.x = 0 - fishWidth
        p4.y = getRandomInt(height, false)
        points.add(p4)
        return points
    }



}