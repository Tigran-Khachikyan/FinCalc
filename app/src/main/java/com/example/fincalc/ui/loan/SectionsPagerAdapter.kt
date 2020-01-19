package com.example.fincalc.ui.loan

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.fincalc.models.credit.Formula


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
@Suppress("DEPRECATION")
class SectionsPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    private val annuityFragment = ScheduleFragment(Formula.ANNUITY)
    private val differentialFragment = ScheduleFragment(Formula.DIFFERENTIAL)
    private val overdraftFragment = ScheduleFragment(Formula.OVERDRAFT)

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> differentialFragment
            1 -> annuityFragment
            else -> overdraftFragment
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> Formula.DIFFERENTIAL.name
            1 -> Formula.ANNUITY.name
            else -> Formula.OVERDRAFT.name
        }
    }

    override fun getCount() = 3
}