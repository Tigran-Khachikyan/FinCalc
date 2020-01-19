package com.example.fincalc.ui.loan

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewpager.widget.ViewPager
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.fincalc.R
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.models.credit.TableLoan
import com.example.fincalc.models.cur_met.currencyCodeList
import com.example.fincalc.models.cur_met.currencyFlagList
import com.example.fincalc.models.credit.Formula
import com.example.fincalc.ui.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_loan.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoanActivity : AppCompatActivity() {

    private lateinit var adapterSpin: AdapterSpinnerRates

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan)

        initSpinner()

        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        btnClear.setOnClickListener { clear() }

        btnCalculate.setOnClickListener { calculate(it) }

        btnExpand.setOnClickListener { expand() }
    }

    private fun clear() {
        scrollAppBarLayoutInit(0)
        etSum.text.clear()
        etTerm.text.clear()
        etRate.text.clear()
        etOneTimeCommSum.text.clear()
        etOneTimeCommRate.text.clear()
        etMonthlyCommRate.text.clear()
        etMonthlyCommSum.text.clear()
        etAnnualCommRate.text.clear()
        etAnnualCommSum.text.clear()
        etOtherCosts.text.clear()
        checkboxOneTime.isChecked = false
        checkboxMonthly.isChecked = false
        checkboxAnnual.isChecked = false
        ScheduleViewModel.RepoSchedule.clear()
    }

    private fun calculate(view: View) {
        hideKeyboard(this)
        val loan: Loan? = getLoan(view)

        if (loan != null) {
            CoroutineScope(Dispatchers.Main).launch {
                val schAnn = getSchedule(Formula.ANNUITY, loan)
                val schDiff = getSchedule(Formula.DIFFERENTIAL, loan)
                val schOver = getSchedule(Formula.OVERDRAFT, loan)
                ScheduleViewModel.RepoSchedule.setScheduleAnn(schAnn)
                ScheduleViewModel.RepoSchedule.setScheduleDiff(schDiff)
                ScheduleViewModel.RepoSchedule.setScheduleOver(schOver)
            }

            val handler = Handler()
            val runnable = Runnable {
                scrollAppBarLayoutInit(-resources.displayMetrics.heightPixels)
            }
            handler.postDelayed(runnable, 1000)
        }
    }

    private fun expand() {
        if (layoutLoanInputOptional.visibility == View.GONE) {
            ivLoanImage.visibility = View.GONE
            btnExpand.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_expand_less_black_24dp,
                0
            )
            btnExpand.setText(R.string.SimpleCalculation)
            toggle(false, layoutLoanInputOptional, layoutLoanInput)
        } else {
            ivLoanImage.visibility = View.VISIBLE
            btnExpand.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_expand_more_black_24dp,
                0
            )
            btnExpand.setText(R.string.AdvancedCalculation)
            toggle(true, layoutLoanInputOptional, layoutLoanInput)
        }
    }

    private fun getLoan(view: View): Loan? {

        val minOneTimeComSumOrRate = checkboxOneTime.isChecked
        val minMonthlyComSumOrRate = checkboxMonthly.isChecked
        val minAnnualComSumOrRate = checkboxAnnual.isChecked
        val sum: Long = when {
            etSum.text.toString() == "" -> 0L
            else -> etSum.text.toString().toLong()
        }
        val months = when {
            etTerm.text.toString() == "" -> 0
            else -> etTerm.text.toString().toInt()
        }
        val rate = when {
            etRate.text.toString() == "" -> -1.0F
            else -> etRate.text.toString().toFloat()
        }
        val oneTimeComSum = when {
            etOneTimeCommSum.text.toString() == "" -> 0
            else -> etOneTimeCommSum.text.toString().toInt()
        }
        val otherCosts = when {
            etOtherCosts.text.toString() == "" -> 0
            else -> etOtherCosts.text.toString().toInt()
        }
        val oneTimeRate = when {
            etOneTimeCommRate.text.toString() == "" -> 0.0F
            else -> etOneTimeCommRate.text.toString().toFloat()
        }

        val annualComSum = when {
            etAnnualCommSum.text.toString() == "" -> 0
            else -> etAnnualCommSum.text.toString().toInt()
        }
        val annualComRate = when {
            etAnnualCommRate.text.toString() == "" -> 0.0F
            else -> etAnnualCommRate.text.toString().toFloat()
        }

        val monthlyComSum = when {
            etMonthlyCommSum.text.toString() == "" -> 0
            else -> etMonthlyCommSum.text.toString().toInt()
        }
        val monthlyComRate = when {
            etMonthlyCommRate.text.toString() == "" -> 0.0F
            else -> etMonthlyCommRate.text.toString().toFloat()
        }

        return when {
            sum == 0L || months == 0 || rate == -1.0F -> {
                if (sum == 0L)
                    iconTrigger(ivLogoLoanSum)
                if (months == 0)
                    iconTrigger(ivLogoLoanTerm)
                if (rate == 0.0F)
                    iconTrigger(ivLogoLoanRate)

                val invalidInput = resources.getString(R.string.InvalidInput)
                showSnackbar(invalidInput, view)
                null
            }
            else -> {
                Loan(
                    amount = sum,
                    months = months,
                    rate = rate,
                    oneTimeComRate = oneTimeRate,
                    oneTimeComSum = oneTimeComSum,
                    annComRate = annualComRate,
                    annComSum = annualComSum,
                    monthComRate = monthlyComRate,
                    monthComSum = monthlyComSum,
                    otherCosts = otherCosts,
                    minAnnComSumOrRate = minAnnualComSumOrRate,
                    minMonthComSumOrRate = minMonthlyComSumOrRate,
                    minOneTimeComSumOrRate = minOneTimeComSumOrRate
                )
            }
        }
    }

    private fun scrollAppBarLayoutInit(scrollY: Int) {

        val params = appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior as AppBarLayout.Behavior?
        val valueAnimatorExtend = ValueAnimator.ofInt()
        if (behavior != null) {
            valueAnimatorExtend.interpolator = AccelerateDecelerateInterpolator()
            valueAnimatorExtend.addUpdateListener { animation ->
                behavior.topAndBottomOffset = (animation.animatedValue as Int)
                appBarLayout.requestLayout()
            }
            valueAnimatorExtend.setIntValues(0, scrollY)
            valueAnimatorExtend.duration = 1200
            valueAnimatorExtend.start()
        }
    }

    private fun initSpinner() {
        adapterSpin = AdapterSpinnerRates(
            this, R.layout.layoutspinner,
            currencyCodeList, currencyFlagList, true
        )
        adapterSpin.setDropDownViewResource(R.layout.layoutspinner)
        spinnerLoan.adapter = adapterSpin
        spinnerLoan.setSelection(adapterSpin.count - 4)
    }

    private fun getSchedule(formula: Formula, loan: Loan): TableLoan {
        loan.formula = formula
        return TableLoan(loan)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animatoo.animateSwipeLeft(this)
    }
}
