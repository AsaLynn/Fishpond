package com.fishpond.demo.activity

import android.content.Context
import android.view.View
import android.widget.Toast
import com.fishpond.demo.R
import com.fishpond.demo.base.MyBaseActivity
import com.fishpond.demo.databinding.ActivityBlueWaterBinding
import com.water.fish.FishStatus
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

    override fun onCreateRootView(): View = mBinding.root

    private val petFish by lazy {
        PetFish().apply {

            moveSpeed = 300

            moveLeftResId = R.mipmap.ic_fish_pet_left_normal
            moveRightResId = R.mipmap.ic_fish_pet_right_normal
            turnLeftResId = R.mipmap.ic_fish_pet_left_turn
            turnRightResId = R.mipmap.ic_fish_pet_right_turn
            spurtLeftResId = R.mipmap.ic_fish_pet_left_normal_water
            spurtRightResId = R.mipmap.ic_fish_pet_right_normal_water

            thirstyLeftResId = R.mipmap.ic_fish_pet_left_thirsty
            thirstyRightResId = R.mipmap.ic_fish_pet_right_thirsty

            turnLeftResId = R.mipmap.ic_fish_pet_left_turn_thirsty
            turnRightResId = R.mipmap.ic_fish_pet_right_turn_thirsty

            gladLeftResId = R.mipmap.ic_fish_pet_left_normal_glad
            gladRightResId = R.mipmap.ic_fish_pet_right_normal_glad

            fatLeftResId = R.mipmap.ic_fish_pet_left_normal_fat
            fatRightResId = R.mipmap.ic_fish_pet_right_normal_fat

            prettyLeftResId = R.mipmap.ic_fish_pet_left_normal_pretty
            prettyRightResId = R.mipmap.ic_fish_pet_right_normal_pretty
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
            //skinResList.add(R.mipmap.bg_orange_fish_shoal)
            skinResList.add(R.mipmap.bg_blue_fish_shoal)
            moveSpeed = 150
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
            petFish.updateFishStatus(FishStatus.THIRSTY)
            mBinding.fishLayout.notifyDataSetChanged(0)
        }
        mBinding.btnShort.setOnClickListener {
            status++
            when (status % 3) {
                0 -> petFish.updateFishStatus(FishStatus.EXCITING)
                1 -> petFish.updateFishStatus(FishStatus.FLESH_UP)
                2 -> petFish.updateFishStatus(FishStatus.BEAUTIFY)
            }
            mBinding.fishLayout.notifyDataSetChanged(0)
        }
        mBinding.fishLayout.setOnItemClickListener {
            if (it.id == R.id.shellView) {
                petFish.updateFishStatus(FishStatus.NORMAL)
                mBinding.fishLayout.notifyDataSetChanged(0)
                Toast.makeText(this, "click:贝壳!", Toast.LENGTH_SHORT).show()
            }
        }

        mBinding.btnTravel.setOnClickListener {
            status++
            when (status % 2) {
                0 -> {
                    //旅行回来.
                    petFish.updateFishStatus(FishStatus.NORMAL)
                    mBinding.fishLayout.notifyDataSetChanged(0)
                }
                1 -> {
                    petFish.updateFishStatus(FishStatus.TRAVEL)
                    mBinding.fishLayout.notifyDataSetChanged(0)
                }
            }
        }

    }

    private var status: Int = 0
}