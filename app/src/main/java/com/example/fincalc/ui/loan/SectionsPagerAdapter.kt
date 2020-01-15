package com.example.fincalc.ui.loan

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.fincalc.models.loan.FormulaLoan


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    private val annuityFragment = ScheduleFragment(FormulaLoan.ANNUITY)
    private val differentialFragment = ScheduleFragment(FormulaLoan.DIFFERENTIAL)
    private val overdraftFragment = ScheduleFragment(FormulaLoan.OVERDRAFT)

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> differentialFragment
            1 -> annuityFragment
            else -> overdraftFragment
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> FormulaLoan.DIFFERENTIAL.name
            1 -> FormulaLoan.ANNUITY.name
            else -> FormulaLoan.OVERDRAFT.name
        }
    }

    override fun getCount() = 3
}