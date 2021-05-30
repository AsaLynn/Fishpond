package com.fishpond.demo.activity

import android.content.Context
import android.view.View
import com.fishpond.demo.R
import com.fishpond.demo.base.MyBaseActivity
import com.fishpond.demo.databinding.ActivitySeaBinding
import com.water.fish.PetFish
import com.water.fish.Shell
import com.zxn.mvvm.ext.jumpInTo

class SeaActivity : MyBaseActivity() {

    companion object {
        @JvmStatic
        fun jumpTo(context: Context) {
            jumpInTo<SeaActivity>(context)
        }
    }

    private val mBinding by lazy {
        ActivitySeaBinding.inflate(layoutInflater)
    }

    override val layoutRoot: View by lazy {
        mBinding.root
    }

    private val petFish by lazy {
        PetFish().apply {
            moveLeftResId = R.mipmap.ic_fish_pet_left_normal
            moveRightResId = R.mipmap.ic_fish_pet_right_normal
            turnLeftResId = R.mipmap.ic_fish_pet_left_turn
            turnRightResId = R.mipmap.ic_fish_pet_right_turn
            spurtLeftResId = R.mipmap.ic_fish_pet_left_normal_water
            spurtRightResId = R.mipmap.ic_fish_pet_right_normal_water
        }
    }

    private val mShell by lazy {
        Shell().apply {
            shellResId = R.mipmap.ic_shell
            stoneResId = R.mipmap.bg_stone_platform
        }
    }

    override fun onInitView() {
        mBinding.fishLayout.setFishData(mutableListOf(petFish,mShell))
        mBinding.fishLayout.start()
    }


}