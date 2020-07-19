package com.bs.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bs.R
import com.bs.firstFragment
import com.bs.secondFragment
import com.example.guilongyuangaoyue.myapplication.fouthFragment
import com.example.guilongyuangaoyue.myapplication.thirdFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.layout_mine_fragment.*

class mineFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_mine_fragment,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list= mutableListOf<Fragment>(firstFragment(),secondFragment(),thirdFragment(),thirdFragment())
        val tab1= mine_tab.newTab()
        val tab2= mine_tab.newTab()
        val tab3= mine_tab.newTab()
        val tab4= mine_tab.newTab()
        tab1.text=resources.getString(R.string.tab1)
        mine_tab.addTab(tab1)
        tab2.text=resources.getString(R.string.tab2)
        mine_tab.addTab(tab2)
        tab3.text=resources.getString(R.string.tab3)
        mine_tab.addTab(tab3)
        tab4.text=resources.getString(R.string.tab4)
        mine_tab.addTab(tab4)
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.mine_frame,list.get(0))
            ?.commit()
        mine_tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val pos=mine_tab.selectedTabPosition
                if (list != null) {
                    activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.mine_frame,list.get(pos))
                        ?.commit()
                }
            }
        }
        )

    }
}