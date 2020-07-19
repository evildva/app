package com.bs.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bs.R
import com.example.guilongyuangaoyue.myapplication.login.login
import com.example.guilongyuangaoyue.myapplication.login.register
import com.example.guilongyuangaoyue.myapplication.login.thirdParty
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.layout_login.*

class logInActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_login)

        val fragmentList : MutableList<Fragment> = mutableListOf(login(),register(),thirdParty())

        class viewPagerAdapter : FragmentStateAdapter(this@logInActivity){
            override fun getItemCount(): Int {
                return fragmentList.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragmentList[position]
            }

        }
        login_vp.adapter=viewPagerAdapter()
        
        val tab_login=login_tab.newTab()
        val tab_regist=login_tab.newTab()
        tab_login.text="login"
        tab_regist.text="register"
        login_tab.addTab(tab_login)
        login_tab.addTab(tab_regist)

        login_tab.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                login_vp.currentItem=login_tab.selectedTabPosition
            }

        })

        login_vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                login_tab.setScrollPosition(position, 0F,true)
                super.onPageSelected(position)
            }
        })

    }

}