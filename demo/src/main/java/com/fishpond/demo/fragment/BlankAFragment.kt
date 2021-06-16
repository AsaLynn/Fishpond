package com.fishpond.demo.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import com.fishpond.demo.R
import com.fishpond.demo.base.MyBaseFragment
import com.fishpond.demo.databinding.FragmentBlankABinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BlankAFragment : MyBaseFragment() {

    private var param1: String? = null
    private var param2: String? = null

    private val mBinding by lazy {
        FragmentBlankABinding.inflate(layoutInflater)
    }

    override fun createObserver() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateRootView(): View = mBinding.root

    override fun onInitView() {
        Log.i(TAG, "onInitView: ")
        mBinding.run {

            waterBallView.decodeBallResource(R.mipmap.ic_bottle_ping_big)

            btnBegin.setOnClickListener {
                waterBallView.visibility = View.VISIBLE
            }

            btnStop.setOnClickListener {
                waterBallView.visibility = View.GONE
            }

            btnChange.setOnClickListener {
                mBallCount++
                if (mBallCount % 2 == 0) {
                    waterBallView.decodeBallResource(R.mipmap.ic_bottle_ping_big)
                } else {
                    waterBallView.decodeBallResource(R.drawable.ball)
                }
            }
        }
    }

    private var mBallCount = 0

    companion object {

        private const val TAG = "BlankAFragment"

        @JvmStatic
        fun newInstance() = BlankAFragment()
    }
}