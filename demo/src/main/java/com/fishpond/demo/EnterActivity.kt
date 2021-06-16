package com.fishpond.demo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fishpond.demo.activity.BallActivity
import com.fishpond.demo.activity.BlueWaterActivity
import com.fishpond.demo.activity.SeaActivity
import com.fishpond.demo.activity.SeawaterActivity
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
            Toast.makeText(this, "已废弃", Toast.LENGTH_SHORT).show()
        }
        mBinding.btnB.setOnClickListener {
            Toast.makeText(this, "已废弃", Toast.LENGTH_SHORT).show()
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
        mBinding.btnF.setOnClickListener {
            BallActivity.jumpTo(this)
        }
        BlueWaterActivity.jumpTo(this)
    }

}