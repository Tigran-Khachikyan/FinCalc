package com.my_1st.fincalc.ui.loan

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.my_1st.fincalc.R
import com.my_1st.fincalc.models.credit.Formula


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
@Suppress("DEPRECATION")
class SectionsPagerAdapter(fm: FragmentManager, val context: Context) :
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
            0 -> context.getString(R.string.DIFFERENTIAL)
            1 -> context.getString(R.string.ANNUITY)
            else -> context.getString(R.string.OVERDRAFT)
        }
    }

    override fun getCount() = 3
}