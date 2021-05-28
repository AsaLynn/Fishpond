package com.fishpond.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fishpond.demo.databinding.FragmentSeaFragmetBinding
import com.water.fish.PetFish
import com.water.fish.Shell
import com.water.fish.ShoalFish

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SeaFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var mBinding: FragmentSeaFragmetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         mBinding = FragmentSeaFragmetBinding.inflate(inflater, container, false)
        //return inflater.inflate(R.layout.fragment_sea_fragmet, container, false)
        return mBinding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            SeaFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.fishLayout.setFishData(mutableListOf(petFish, mShell, ShoalFish().apply {
            skinResId = R.mipmap.fish_group_1
        }))
        mBinding.fishLayout.start()

    }

    /*override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.fishLayout.setFishData(mutableListOf(petFish, mShell, ShoalFish().apply {
            skinResId = R.mipmap.fish_group_1
        }))
        mBinding.fishLayout.start()
    }*/
}