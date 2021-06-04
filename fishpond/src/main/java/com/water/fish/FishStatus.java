package com.water.fish;

/**
 * Created by zxn on 2021/4/1.
 */

import androidx.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 鱼儿的游动状态
 */
@IntDef({
        FishStatus.MOVE_BOTTOM_ON_LEFT,
        FishStatus.MOVE_TOP_ON_LEFT,
        FishStatus.MOVE_BOTTOM_LEFT,
        FishStatus.MOVE_BOTTOM_RIGHT,
        FishStatus.MOVE_TOP_ON_RIGHT,
        FishStatus.MOVE_BOTTOM_ON_RIGHT,
        FishStatus.MOVE_TOP_RIGHT,
        FishStatus.MOVE_TOP_LEFT,
        FishStatus.TURN_LEFT,
        FishStatus.TURN_RIGHT,
        FishStatus.MOVE_LEFT,
        FishStatus.MOVE_RIGHT,
        FishStatus.REST,
//        FishStatus.SPRAY_START,
//        FishStatus.SPRAY_STOP,
})
@Retention(RetentionPolicy.SOURCE)
public @interface FishStatus {
    /**
     * 休息.
     */
    int REST = -1;
    /**
     * 向左转
     */
    int TURN_LEFT = 0;
    /**
     * 向右转
     */
    int TURN_RIGHT = 1;
    /**
     * 水平向左正常游动
     */
    int MOVE_LEFT = 2;
    /**
     * 水平向右正常游动
     */
    int MOVE_RIGHT = 3;
    /**
     * 面向右边垂直向下游动
     */
    int MOVE_BOTTOM_ON_RIGHT = 4;
    /**
     * 面向左边垂直向下游动
     */
    int MOVE_BOTTOM_ON_LEFT = 5;
    /**
     * 向左上方游动
     */
    int MOVE_TOP_LEFT = 6;
    /**
     * 向右上方游动
     */
    int MOVE_TOP_RIGHT = 7;
    /**
     * 面向右边垂直向上游动
     */
    int MOVE_TOP_ON_RIGHT = 8;
    /**
     * 向右下方游动
     */
    int MOVE_BOTTOM_RIGHT = 9;
    /**
     * 向左下方游动
     */
    int MOVE_BOTTOM_LEFT = 10;
    /**
     * 面向左边垂直向上游动
     */
    int MOVE_TOP_ON_LEFT = 11;

//    /**
//     * 碰水状态变化
//     */
//    int SPRAY_START = 12;
//    int SPRAY_STOP = 13;
}
