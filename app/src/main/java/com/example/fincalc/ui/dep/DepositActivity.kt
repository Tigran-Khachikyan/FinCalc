package com.example.fincalc.ui.dep

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.fincalc.R
import com.example.fincalc.models.cur_met.currencyCodeList
import com.example.fincalc.models.cur_met.currencyFlagList
import com.example.fincalc.models.dep.PaymentInterval
import com.example.fincalc.models.dep.QueryDep
import com.example.fincalc.models.dep.ScheduleDep
import com.example.fincalc.models.dep.getScheduleDep
import com.example.fincalc.ui.*
import kotlinx.android.synthetic.main.activity_deposit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class DepositActivity : AppCompatActivity() {

    private lateinit var adapterRec: AdapterRecViewDep
    private lateinit var adapterSpin: AdapterSpinnerRates
    private var scheduleDep: ScheduleDep? = null
    private var period: PaymentInterval = PaymentInterval.MONTHLY
    val dec = DecimalFormat("#,###.##")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deposit)

        imitateRadioGroup(btnMonthly, btnQuarterly, btnEndOfPeriod)

        initSpinner()

        recyclerDep.setHasFixedSize(true)
        recyclerDep.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)



        adapterRec = AdapterRecViewDep(
            getScheduleDep(
                QueryDep(
                    250000, 12, 6.4F, PaymentInterval.MONTHLY,
                    false, 10.0F
                )
            )
        )
        recyclerDep.adapter = adapterRec

        btnClearDep.setOnClickListener {
            etSumDep.text.clear()
            etRateDep.text.clear()
            etTermDep.text.clear()
            layoutDepResult.visibility = View.GONE
        }


        btnCalculateDep.setOnClickListener {

            hideKeyboard(this)
            val currentQuery: QueryDep? = getQueryDep(it)

            if (currentQuery == null) {
                adapterRec.scheduleDep = null
            } else {
                CoroutineScope(Main).launch {
                    val result = getScheduleDep(currentQuery)
                    adapterRec.scheduleDep = result
                    adapterRec.notifyDataSetChanged()
                    val text: String =
                        resources.getString(R.string.ResultShowDep) +
                                " ${dec.format(result.totalPercentAfterTaxing)}"
                    tvResultDepShow.text = text
                }
                if (layoutDepOptionalInput.visibility != View.GONE) {
                    btnExpandDep.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_expand_more_black_24dp,
                        0
                    )
                    btnExpandDep.setText(R.string.AdvancedCalculation)
                    toggle(true, layoutDepOptionalInput, layDepInput)
                }
                layoutDepResult.visibility = View.VISIBLE
            }
        }

        btnMonthly.setOnClickListener {
            imitateRadioGroup(btnMonthly, btnQuarterly, btnEndOfPeriod)
            period = PaymentInterval.MONTHLY
        }
        btnQuarterly.setOnClickListener {
            imitateRadioGroup(btnQuarterly, btnMonthly, btnEndOfPeriod)
            period = PaymentInterval.QUARTERLY
        }
        btnEndOfPeriod.setOnClickListener {
            imitateRadioGroup(btnEndOfPeriod, btnQuarterly, btnMonthly)
            period = PaymentInterval.END_OF_THE_CONTRACT
        }


        btnExpandDep.setOnClickListener {
            if (layoutDepOptionalInput.visibility == View.GONE) {
                //  ivLoanImage.visibility = View.GONE
                btnExpandDep.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_expand_less_black_24dp,
                    0
                )
                btnExpandDep.setText(R.string.SimpleCalculation)
                toggle(false, layoutDepOptionalInput, layDepInput)
                layoutDepResult.visibility = View.GONE
            } else {
                //   ivLoanImage.visibility = View.VISIBLE
                btnExpandDep.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_expand_more_black_24dp,
                    0
                )
                btnExpandDep.setText(R.string.AdvancedCalculation)
                toggle(true, layoutDepOptionalInput, layDepInput)
            }
        }

    }

    private fun imitateRadioGroup(btnClicked: Button, btn2: Button, btn3: Button) {

        btn2.setBackgroundResource(R.drawable.btnperiodsunselected)
        btn3.setBackgroundResource(R.drawable.btnperiodsunselected)
        btnClicked.setBackgroundResource(R.drawable.btnperiodsselected)
        btnClicked.setTextColor(resources.getColor(android.R.color.white))
        btn2.setTextColor(resources.getColor(android.R.color.black))
        btn3.setTextColor(resources.getColor(android.R.color.black))
        btnClicked.isClickable = false
        btn2.isClickable = true
        btn3.isClickable = true
    }


    private fun getPixelFromDp(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }


/*
    private fun animateInputLayout(closed: Boolean, context: Context) {

        val pixelToTranslate = getPixelFromDp(context, 300f)
        if (closed) {
            btnExpand.setText(R.string.SimpleCalculation)

            val animMandExpand = layoutLoanMandatoryInput.animate()
            animMandExpand.duration = 1000
            animMandExpand.interpolator = AccelerateDecelerateInterpolator()
            animMandExpand.translationY(0f).start()


            val animOptionalAppears = layoutLoanInputOptional.animate()
            animOptionalAppears.duration = 1000
            animOptionalAppears.interpolator = AccelerateDecelerateInterpolator()
            animOptionalAppears.translationY(0f).alpha(1f).start()
        } else {
            btnExpand.setText(R.string.AdvancedCalculation)
            val animMandDownAndScaling = layoutLoanMandatoryInput.animate()
            animMandDownAndScaling.duration = 1000
            animMandDownAndScaling.interpolator = AccelerateDecelerateInterpolator()
            animMandDownAndScaling.translationY(pixelToTranslate).start()


            val animOptionalHides = layoutLoanInputOptional.animate()
            animOptionalHides.duration = 1000
            animOptionalHides.interpolator = AccelerateDecelerateInterpolator()
            animOptionalHides.translationY(pixelToTranslate).alpha(0f).start()
        }
    }
*/

    private fun getQueryDep(view: View): QueryDep? {

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

        val check = if (period == PaymentInterval.QUARTERLY) 3 else 1

        return when {
            amount == 0L || term == 0 || rate == 0.0F -> {
                if (amount == 0L)
                    iconTrigger(ivLogoDepSum)
                if (term == 0)
                    iconTrigger(ivLogoDepTerm)
                if (rate == 0.0F)
                    iconTrigger(ivLogoDepRate)

                val invalidInput = resources.getString(R.string.InvalidInput)
                showSnackbar(invalidInput, view)
                null
            }
            term % check != 0 -> {
                iconTrigger(ivLogoDepTerm)
                val invalidInputMis = resources.getString(R.string.InvalidInputPeriod)
                showSnackbar(invalidInputMis, view)
                null
            }
            taxRate >= 100F -> {
                iconTrigger(ivLogoDepTax)
                val invalidInput = resources.getString(R.string.InvalidInputTax)
                showSnackbar(invalidInput, view)
                null
            }
            else -> {
                QueryDep(amount, term, rate, period, capitalized, taxRate)
            }
        }
    }

    private fun initSpinner(){
        adapterSpin = AdapterSpinnerRates(
            this, R.layout.layoutspinner,
            currencyCodeList, currencyFlagList,true
        )
        adapterSpin.setDropDownViewResource(R.layout.layoutspinner)
        spinnerDep.adapter = adapterSpin
        spinnerDep.setSelection(adapterSpin.count-4)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animatoo.animateCard(this)
    }

}
