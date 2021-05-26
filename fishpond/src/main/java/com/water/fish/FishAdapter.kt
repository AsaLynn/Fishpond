package com.water.fish

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

/**
 *  Created by zxn on 2021/5/21.
 **/
class FishAdapter constructor(data: MutableList<FishEntity>? = null) : BaseAdapter() {

    /**
     * 数据, 只允许 get。
     */
    var data: MutableList<FishEntity> = data ?: arrayListOf()
        internal set

    override fun getCount(): Int = data.size

    override fun getItem(position: Int): FishEntity = data[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? = null

}