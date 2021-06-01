package com.fishpond.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fishpond.demo.activity.*
import com.fishpond.demo.databinding.ActivityEnterBinding

/**
 * 首次进入页面.
 */
class EnterActivity : AppCompatActivity() {

    private val mBinding by lazy {
        ActivityEnterBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mBinding.btnA.setOnClickListener {
            FishpondActivity.jumpTo(this)
        }
        mBinding.btnB.setOnClickListener {
            SeafloorActivity.jumpTo(this)
        }
        mBinding.btnC.setOnClickListener {
            SeaActivity.jumpTo(this)
        }
        mBinding.btnD.setOnClickListener {
            SeawaterActivity.jumpTo(this)
        }
        mBinding.btnE.setOnClickListener {
            BlueWaterActivity.jumpTo(this)
        }
        SeafloorActivity.jumpTo(this)
    }


}