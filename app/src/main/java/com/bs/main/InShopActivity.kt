package com.bs.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bs.R
import kotlinx.android.synthetic.main.layout_inshop.*

class InShopActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.layout_inshop)
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {

        val id=intent.getIntExtra("shopid",0)
        val bundle =Bundle()
        bundle.putInt("shopid",id)

        class viewPagerAdapter : FragmentStateAdapter(this){
            override fun getItemCount(): Int {
                return 1
            }

            override fun createFragment(position: Int): Fragment {
                val store = store()
                store.arguments=bundle
                return store
            }

        }

        in_viewpager.adapter=viewPagerAdapter()

        super.onStart()
    }

}