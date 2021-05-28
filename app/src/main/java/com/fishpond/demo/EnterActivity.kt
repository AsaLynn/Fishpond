package com.fishpond.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
            MainActivity.jumpTo(this)
        }
        mBinding.btnB.setOnClickListener {
            SeafloorActivity.jumpTo(this)
        }
        SeafloorActivity.jumpTo(this)
    }


}