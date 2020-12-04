package com.example.mp_team_prj.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.mp_team_prj.Fragment.*

class MainFragmentStatePagerAdapter(fm : FragmentManager, val fragmentCount : Int) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        when(position){
            2 -> return SNSFragment()
            3 -> return MyPageFragment()
            else -> return MyPageFragment()
        }
    }

    override fun getCount(): Int = fragmentCount // 자바에서는 { return fragmentCount }

}