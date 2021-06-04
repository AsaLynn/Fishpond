package com.fishpond.demo.activity

import android.content.Context
import android.view.View
import android.widget.Toast
import com.fishpond.demo.R
import com.fishpond.demo.base.MyBaseActivity
import com.fishpond.demo.databinding.ActivityBlueWaterBinding
import com.water.fish.PetFish
import com.water.fish.Shell
import com.water.fish.ShoalFish
import com.zxn.mvvm.ext.jumpInTo

class BlueWaterActivity : MyBaseActivity() {

    companion object {
        @JvmStatic
        fun jumpTo(context: Context) {
            jumpInTo<BlueWaterActivity>(context)
        }
    }

    private val mBinding by lazy {
        ActivityBlueWaterBinding.inflate(layoutInflater)
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

    private val mShoalFish by lazy {
        ShoalFish().apply {
            //skinResId = R.mipmap.bg_orange_fish_shoal
            skinResList.add(R.mipmap.bg_orange_fish_shoal)
        }
    }

    override fun onInitView() {
        mBinding.fishLayout.setFishData(mutableListOf(petFish, mShell, mShoalFish))
        mBinding.fishLayout.start()

        mBinding.tvStart.setOnClickListener {
            mBinding.fishLayout.restart()
        }
        mBinding.tvPause.setOnClickListener {
            mBinding.fishLayout.pause()
        }
        mBinding.tvResume.setOnClickListener {
            mBinding.fishLayout.resume()
        }
        mBinding.tvEnd.setOnClickListener {
            mBinding.fishLayout.end()
        }

        mBinding.btnOpen.setOnClickListener {
            DemoActivity.jumpTo(this)
        }

        mBinding.btnFishAdd.setOnClickListener {
            mShoalFish.skinResList.add(R.mipmap.bg_blue_fish_shoal)
            mShoalFish.skinResList.add(R.mipmap.bg_clown_fish_shoal)
        }

        mBinding.btnChange.setOnClickListener {
            petFish.moveLeftResId = R.mipmap.ic_fish_pet_left_normal_fen
            mBinding.fishLayout.notifyDataSetChanged()
        }
        mBinding.btnSpray.setOnClickListener {
            petFish.isSpraying = !petFish.isSpraying
            mBinding.fishLayout.notifyDataSetChanged()
        }
        mBinding.fishLayout.setOnItemClickListener {
            if (it.id == R.id.shellView) {
                Toast.makeText(this, "click:贝壳!", Toast.LENGTH_SHORT).show()
            }
        }

    }

}