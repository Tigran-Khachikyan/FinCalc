package com.example.fincalc.ui.dep

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.fincalc.R
import com.example.fincalc.data.db.dep.Deposit
import com.example.fincalc.models.deposit.Frequency
import com.example.fincalc.ui.*
import kotlinx.android.synthetic.main.activity_deposit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


const val KEY_AMOUNT = "Amount"
const val KEY_TERM = "Term"
const val KEY_RATE = "Rate"
const val KEY_CAPITALIZATION = "Capitalization"
const val KEY_TAX_RATE = "TaxRate"
const val KEY_FREQUENCY = "Frequency"


@Suppress("DEPRECATION")
class DepositActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Main + job
    private var period: Frequency = Frequency.MONTHLY


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deposit)
        job = Job()

        val taxInfo = baseContext.getString(R.string.Tax) + " (%)"
        tvTaxIntro.text = taxInfo

        tvStatusDep.setFont(FONT_PATH)
        tvHeaderDepAct.setFont(FONT_PATH)

        imitateRadioGroup(btnMonthly, btnQuarterly, btnEndOfPeriod)

        btnClearDep.setOnClickListener {
            clear()
        }

        btnCalculateDep.setOnClickListener {
            hideKeyboard(this)
            calculate(it)
        }

        btnMonthly.setOnClickListener {
            imitateRadioGroup(btnMonthly, btnQuarterly, btnEndOfPeriod)
            period = Frequency.MONTHLY
        }

        btnQuarterly.setOnClickListener {
            imitateRadioGroup(btnQuarterly, btnMonthly, btnEndOfPeriod)
            period = Frequency.QUARTERLY
        }

        btnEndOfPeriod.setOnClickListener {
            imitateRadioGroup(btnEndOfPeriod, btnQuarterly, btnMonthly)
            period = Frequency.AT_THE_END
        }

        btnExpandDep.setOnClickListener {
            expand()
        }
    }

    private fun clear() {
        etSumDep.text.clear()
        etRateDep.text.clear()
        etTermDep.text.clear()
    }

    private fun calculate(view: View) {
        hideKeyboard(this)
        launch {
            val dep = getDep(view)

            dep?.let {
                progressBarDepAct.visibility = View.VISIBLE
                delay(1000)
                val bundle = Bundle()
                bundle.putLong(KEY_AMOUNT, dep.amount)
                bundle.putInt(KEY_TERM, dep.months)
                bundle.putFloat(KEY_RATE, dep.rate)
                bundle.putBoolean(KEY_CAPITALIZATION, dep.capitalize)
                bundle.putFloat(KEY_TAX_RATE, dep.taxRate)
                bundle.putString(KEY_FREQUENCY, dep.frequency.name)
                val fragmentReport = DepScheduleFragment()
                fragmentReport.arguments = bundle

                supportFragmentManager.beginTransaction()
                    .add(R.id.layContainerDep, fragmentReport)
                    .addToBackStack(" ").commit()
                progressBarDepAct.visibility = View.GONE
            }
        }
    }

    private fun expand() {
        if (layDepOptionalInput.visibility == View.GONE) {
            layDepImage.visibility = View.GONE
            btnExpandDep.setCustomSizeVector(
                baseContext,
                resRight = R.drawable.ic_expand_less_black_24dp,
                sizeRightdp = 24
            )
            btnExpandDep.setText(R.string.SimpleCalculation)
            toggle(false, layDepOptionalInput, layDepInput)
        } else {
            layDepImage.visibility = View.VISIBLE
            btnExpandDep.setCustomSizeVector(
                baseContext,
                resRight = R.drawable.ic_expand_more_black_24dp,
                sizeRightdp = 24
            )
            btnExpandDep.setText(R.string.AdvancedCalculation)
            toggle(true, layDepOptionalInput, layDepInput)
        }
    }

    private fun imitateRadioGroup(btnClicked: Button, btn2: Button, btn3: Button) {

        btn2.setBackgroundResource(R.drawable.btn_expand)
        btn3.setBackgroundResource(R.drawable.btn_expand)
        btnClicked.setBackgroundResource(R.drawable.final_btn_period_select)
        btnClicked.setTextColor(resources.getColor(android.R.color.white))
        btn2.setTextColor(resources.getColor(android.R.color.black))
        btn3.setTextColor(resources.getColor(android.R.color.black))
        btnClicked.isClickable = false
        btn2.isClickable = true
        btn3.isClickable = true
    }

    private fun getDep(view: View): Deposit? {

        val amount =
            if (etSumDep.text.toString() == "") 0
            else etSumDep.text.toString().toLong()
        val term =
            if (etTermDep.text.toString() == "") 0
            else etTermDep.text.toString().toInt()
        val rate =
            if (etRateDep.text.toString() == "") 0F
            else etRateDep.text.toString().toFloat()
        val capitalized = checkBoxCapitalized.isChecked
        val taxRate =
            if (etTaxDep.text.toString() == "") 10F
            else etTaxDep.text.toString().toFloat()

        val check = if (period == Frequency.QUARTERLY) 3 else 1

        return when {
            amount == 0L || term == 0 || rate == 0.0F -> {
                if (amount == 0L) ivLogoDepSum.trigger()
                if (term == 0) ivLogoDepTerm.trigger()
                if (rate == 0.0F) ivLogoDepRate.trigger()

                showSnackBar(R.string.InvalidInput, view)
                null
            }
            term % check != 0 -> {
                ivLogoDepTerm.trigger()
                showSnackBar(R.string.InvalidInputPeriod, view)
                null
            }
            taxRate >= 100F -> {
                ivLogoDepTax.trigger()
                showSnackBar(R.string.InvalidInputTax, view)
                null
            }
            else -> {
                Deposit(
                    amount = amount, months = term, rate = rate, capitalize = capitalized,
                    taxRate = taxRate, frequency = period
                )
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animatoo.animateSlideUp(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}
