package com.fishpond.demo.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fishpond.demo.R
import com.fishpond.demo.databinding.ActivitySeafloorBinding
import com.water.fish.PetFish
import com.water.fish.Shell
import com.water.fish.ShoalFish

/**
 * 海底页面底部.
 */
class SeafloorActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        fun jumpTo(context: Context) {
            context.startActivity(Intent(context, SeafloorActivity::class.java))
        }
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

    private val mBinding by lazy {
        ActivitySeafloorBinding.inflate(layoutInflater)
    }

    private var mIsChange = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        mBinding.fishLayout.setFishData(mutableListOf(petFish,mShell, ShoalFish().apply {
            skinResId = R.mipmap.fish_group_1
        }))

        mBinding.fishLayout.start()

        mBinding.tvStart.setOnClickListener {
            //Toast.makeText(this, "开始", Toast.LENGTH_SHORT).show()
            mBinding.fishLayout.start()
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
        mBinding.btnChange.setOnClickListener {
            mIsChange = !mIsChange
            if (mIsChange){
                petFish.moveLeftResId = R.mipmap.ic_fish_pet_left_normal_fen
                petFish.moveRightResId = R.mipmap.ic_fish_pet_right_normal_fen
            }else{
                petFish.moveLeftResId = R.mipmap.ic_fish_pet_left_normal
                petFish.moveRightResId = R.mipmap.ic_fish_pet_right_normal
            }

            mBinding.fishLayout.notifyDataSetChanged()
        }
        mBinding.btnSpray.setOnClickListener {
            petFish.moveLeftResId = R.mipmap.ic_fish_pet_left_normal_fen
            petFish.isSpraying = !petFish.isSpraying
            mBinding.fishLayout.notifyDataSetChanged()
        }
        mBinding.fishLayout.setOnItemClickListener {
            if (it.id == R.id.ivAIShell){
                Toast.makeText(this@SeafloorActivity,"click",Toast.LENGTH_SHORT).show()
            }
        }
        mBinding.btnFastA.setOnClickListener {
            mBinding.fishLayout.petFishSpeed = 10
        }
        mBinding.btnFastB.setOnClickListener {
            mBinding.fishLayout.petFishSpeed = 50
        }
    }

    override fun onResume() {
        super.onResume()
        mBinding.fishLayout.resume()
    }

    override fun onPause() {
        super.onPause()
        mBinding.fishLayout.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.fishLayout.end()
    }

}