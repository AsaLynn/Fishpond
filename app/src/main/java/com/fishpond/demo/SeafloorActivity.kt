package com.fishpond.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fishpond.demo.databinding.ActivitySeafloorBinding
import com.water.fish.PetFish
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

    private val mBinding by lazy {
        ActivitySeafloorBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        mBinding.fishLayout.setFishData(mutableListOf(petFish, ShoalFish().apply {
            skinResId = R.mipmap.fish_group_1
        }))

        mBinding.fishLayout.start()

        mBinding.tvStart.setOnClickListener {
            Toast.makeText(this, "开始", Toast.LENGTH_SHORT).show()
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
    }

    /*override fun onResume() {
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
    }*/

}