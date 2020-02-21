package com.example.fincalc.ui.loan

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.fincalc.R
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.models.credit.Formula
import com.example.fincalc.models.credit.TableLoan
import com.example.fincalc.ui.*
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_loan.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LoanActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan)

        job = Job()
        ScheduleViewModel.Container.clear()

        tvStatusLoan.setFont(FONT_PATH)
        tvHeaderLoanAct.setFont(FONT_PATH)

        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager, baseContext)
        view_pager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(view_pager)

        btnClear.setOnClickListener { clear() }

        btnCalculate.setOnClickListener { calculate(it) }

        btnExpand.setOnClickListener { expand() }
    }

    private fun clear() {
        scrollAppBarLayoutInit(0, 0, 0)
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
        ScheduleViewModel.Container.clear()
    }

    private fun calculate(view: View) {
        hideKeyboard(this)
        val loan: Loan? = getLoan(view)

        if (loan != null) {
            progressBarLoanAc.visibility = View.VISIBLE
            launch {
                val schedules = arrayOf(
                    getSchedule(Formula.ANNUITY, loan),
                    getSchedule(Formula.DIFFERENTIAL, loan),
                    getSchedule(Formula.OVERDRAFT, loan)
                )
                ScheduleViewModel.Container.setSchedule(schedules)
            }
            scrollAppBarLayoutInit(-resources.displayMetrics.heightPixels, 1200, 1000)
        }
    }

    private fun expand() {
        val heightPx = 140 * resources.displayMetrics.density
        if (layoutLoanInputOptional.visibility == View.GONE) {
            layLoanImage.visibility = View.GONE
            btnExpand.setCustomSizeVector(
                baseContext,
                resRight = R.drawable.ic_expand_less_black_24dp,
                sizeRightdp = 24
            )
            btnExpand.setText(R.string.SimpleCalculation)
            toggle(false, layoutLoanInputOptional, layoutLoanInput)
            scrollAppBarLayoutInit(-heightPx.toInt(), 400, 0)
        } else {
            layLoanImage.visibility = View.VISIBLE
            btnExpand.setCustomSizeVector(
                baseContext,
                resRight = R.drawable.ic_expand_more_black_24dp,
                sizeRightdp = 24
            )
            btnExpand.setText(R.string.AdvancedCalculation)
            toggle(true, layoutLoanInputOptional, layoutLoanInput)
            scrollAppBarLayoutInit(heightPx.toInt(), 400, 0)
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
                if (sum == 0L) ivLogoLoanSum.trigger()
                if (months == 0) ivLogoLoanTerm.trigger()
                if (rate == -1.0F) ivLogoLoanRate.trigger()
                showSnackBar(R.string.InvalidInput, view)
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

    private fun scrollAppBarLayoutInit(scrollY: Int, duration: Long, delay: Long) {

        launch {
            delay(delay)
            progressBarLoanAc.visibility = View.GONE

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
                valueAnimatorExtend.duration = duration
                valueAnimatorExtend.start()
            }
        }
    }

    private fun getSchedule(formula: Formula, loan: Loan): TableLoan {
        loan.formula = formula
        return TableLoan(loan)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animatoo.animateSwipeLeft(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
