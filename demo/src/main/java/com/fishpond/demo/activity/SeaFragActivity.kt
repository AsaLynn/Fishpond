package com.fishpond.demo.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fishpond.demo.databinding.ActivityEnterBinding.inflate
import com.fishpond.demo.fragment.BlankAFragment
import com.fishpond.demo.fragment.BlankBFragment

class SeaFragActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        fun jumpTo(context: Context) {
            context.startActivity(Intent(context, SeaFragActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_sea_frag)

        val newInstance1 = BlankAFragment.newInstance()
        val newInstance2 = BlankBFragment.newInstance()
        inflate(layoutInflater).also {
            setContentView(it.root)
            supportFragmentManager.beginTransaction()
                .add(android.R.id.content, newInstance1)
                .hide(newInstance1)
                .commitAllowingStateLoss()

            supportFragmentManager.beginTransaction()
                .add(android.R.id.content, newInstance2)
                .hide(newInstance2)
                .commitAllowingStateLoss()

            supportFragmentManager.beginTransaction().hide(newInstance1).commitAllowingStateLoss()
            supportFragmentManager.beginTransaction().hide(newInstance2).commitAllowingStateLoss()
        }

    }
}