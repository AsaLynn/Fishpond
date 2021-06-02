package com.water.fish

/**
 * Created by zxn on 2021/6/2.
 */
interface ISeaLayout {

    /**
     * 数据更新
     */
    fun notifyDataSetChanged()

    fun setFishData(entityList: MutableList<FishEntity>)

}