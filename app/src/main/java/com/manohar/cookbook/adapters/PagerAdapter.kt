package com.manohar.cookbook.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.manohar.cookbook.views.fragments.AboutFragment
import com.manohar.cookbook.views.fragments.HomeFragment
import com.manohar.cookbook.views.fragments.ProfileFragment

class PagerAdapter(fm: FragmentManager, tabCount: Int) : FragmentStatePagerAdapter(fm) {

    var tabCount = tabCount

    override fun getCount(): Int {
       return tabCount
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> ProfileFragment()
            2 -> AboutFragment()
            else -> HomeFragment()
        }
    }
}