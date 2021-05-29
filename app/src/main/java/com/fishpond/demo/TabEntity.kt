package com.fishpond.demo

import com.zxn.tablayout.listener.CustomTabEntity

/**
 *Created by zxn on 2021/3/25.CommodityFragment
 **/
class TabEntity(
    var title: String,
    var content: String = "",
    var selectedIcon: Int = 0,
    var unselectedIcon: Int = 0
) : CustomTabEntity {
    override fun getTabTitle(): String = title

    override fun getTabContent(): String = content

    override fun getTabSelectedIcon(): Int = selectedIcon

    override fun getTabUnselectedIcon(): Int = unselectedIcon

}