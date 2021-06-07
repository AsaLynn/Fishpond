package com.water.fish

/**
 *  鱼的基类,框定类型.
 *  Created by zxn on 2021/3/30.
 **/
sealed class FishEntity {

    var moveSpeed = 10L

    /**
     * 当前鱼的展示状态,初始值向左
     */
    open var skinResId = 0

}

/**
 * 鱼宠
 */
class PetFish : FishEntity() {

    /**
     * 鱼的状态
     */
    private var fishStatus = FishStatus.NORMAL

    var tipsList = mutableListOf<String>()

    private val mStringBuilder by lazy {
        StringBuilder()
    }

    var moveLeftResId: Int = 0

    /**
     * 喷水左移
     */
    var spurtLeftResId: Int = 0

    var thirstyLeftResId: Int = 0

    var moveRightResId: Int = 0

    /**
     * 喷水右移
     */
    var spurtRightResId: Int = 0

    var thirstyRightResId: Int = 0

    var turnLeftResId: Int = 0

    var thirstyTurnLeftResId: Int = 0

    var turnRightResId: Int = 0

    var thirstyTurnRightResId: Int = 0

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

    fun updateFishStatus(@FishStatus status: Int) {
        this.fishStatus = status
    }

    fun toLeftImageRes(): Int = when (fishStatus) {
        FishStatus.THIRSTY -> thirstyLeftResId
        FishStatus.SPRAY -> spurtLeftResId
        FishStatus.NORMAL -> moveLeftResId
        else -> moveLeftResId
    }

    fun toRightImageRes(): Int = when (fishStatus) {
        FishStatus.THIRSTY -> thirstyRightResId
        FishStatus.SPRAY -> spurtRightResId
        FishStatus.NORMAL -> moveRightResId
        else -> moveRightResId
    }

    fun turnLeftImageRes(): Int = when (fishStatus) {
        FishStatus.THIRSTY -> thirstyTurnLeftResId
        FishStatus.SPRAY -> turnLeftResId
        FishStatus.NORMAL -> turnLeftResId
        else -> turnLeftResId
    }

    fun turnRightImageRes(): Int = when (fishStatus) {
        FishStatus.THIRSTY -> thirstyTurnRightResId
        FishStatus.SPRAY -> turnRightResId
        FishStatus.NORMAL -> turnRightResId
        else -> turnRightResId
    }

}

/**
 * 鱼群,ShoalFish
 */
class ShoalFish : FishEntity() {

    var skinResList = mutableListOf<Int>()

    fun shuffledSkinResId() = if (skinResList.isEmpty()) 0 else skinResList.shuffled().take(1)[0]

}

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



