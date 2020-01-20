package com.example.fincalc.ui.dep

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.fincalc.R
import com.example.fincalc.data.db.dep.Deposit
import com.example.fincalc.models.cur_met.currencyCodeList
import com.example.fincalc.models.cur_met.currencyFlagList
import com.example.fincalc.models.deposit.Frequency
import com.example.fincalc.models.deposit.TableDep
import com.example.fincalc.ui.*
import kotlinx.android.synthetic.main.activity_deposit.*
import kotlinx.android.synthetic.main.dialog_save.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@Suppress("DEPRECATION")
class DepositActivity : AppCompatActivity() {
    private var dep: Deposit? = null
    private lateinit var adapterRec: AdapterRecViewDep
    private lateinit var adapterSpin: AdapterSpinnerRates
    private lateinit var depViewModel: DepositViewModel
    private var period: Frequency = Frequency.MONTHLY
    val dec = DecimalFormat("#,###.##")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deposit)

        depViewModel = ViewModelProvider(this).get(DepositViewModel::class.java)
        imitateRadioGroup(btnMonthly, btnQuarterly, btnEndOfPeriod)

        initSpinner()

        recyclerDep.setHasFixedSize(true)
        recyclerDep.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        adapterRec = AdapterRecViewDep(null)
        recyclerDep.adapter = adapterRec

        btnClearDep.setOnClickListener {
            clear()
        }

        btnCalculateDep.setOnClickListener {
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

        fab_AddDep.setOnClickListener {
            getDialog(this, dep)
        }
    }

    private fun clear() {
        etSumDep.text.clear()
        etRateDep.text.clear()
        etTermDep.text.clear()
        layoutDepResult.visibility = View.GONE
    }

    private fun calculate(view: View) {
        hideKeyboard(this)
        dep = getDep(view)

        if (dep == null) {
            adapterRec.scheduleDep = null
        } else {
            CoroutineScope(Main).launch {
                val scheduleDep = TableDep(dep as Deposit)
                adapterRec.scheduleDep = scheduleDep
                adapterRec.notifyDataSetChanged()
                val text: String = resources.getString(R.string.ResultShowDep) +
                        " ${dec.format(scheduleDep.totalPerAfterTax)}"
                val effRate: String = resources.getString(R.string.EffectiveRate) + ": " +
                        dec.format(scheduleDep.effectiveRate).toString() + "%"
                tvResultDepShow.text = text
                tvEffectRateDepShow.text = effRate
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

    private fun expand() {
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
                Deposit(
                    amount = amount, months = term, rate = rate, capitalize = capitalized,
                    taxRate = taxRate, frequency = period
                )
            }
        }
    }

    private fun initSpinner() {
        adapterSpin = AdapterSpinnerRates(
            this, R.layout.layoutspinner,
            currencyCodeList, currencyFlagList, true
        )
        adapterSpin.setDropDownViewResource(R.layout.layoutspinner)
        spinnerDep.adapter = adapterSpin
        spinnerDep.setSelection(adapterSpin.count - 4)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animatoo.animateCard(this)
    }

    @SuppressLint("InflateParams")
    private fun getDialog(context: Context?, dep: Deposit?) {
        if (context != null) {
            val dialogBuilder = AlertDialog.Builder(context)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_save, null)
            dialogBuilder.setView(dialogView)

            val etBank: EditText = dialogView.findViewById(R.id.etDialBank)

            //spinner Currency
            val spinnerCur: Spinner = dialogView.findViewById(R.id.spinDialCurrency)
            val adapterSpinCur = AdapterSpinnerRates(
                context, R.layout.layoutspinner,
                currencyCodeList, currencyFlagList, true
            )
            adapterSpinCur.setDropDownViewResource(R.layout.layoutspinner)
            spinnerCur.adapter = adapterSpinCur
            spinnerCur.setSelection(adapterSpinCur.count - 4)

            //spinner LoanType
            val spinnerType = dialogView.findViewById<Spinner>(R.id.spinnerDialLoanType)
            spinnerType.visibility = View.GONE
            val tvSpinner = dialogView.findViewById<TextView>(R.id.tvDialLoanType)
            tvSpinner.visibility = View.GONE

            //click SAVE
            dialogBuilder.setPositiveButton(
                getString(R.string.save)
            ) { _, _ ->

                val bank = etBank.text.toString()
                val cur = spinnerCur.selectedItem.toString()

                dep?.bank = bank
                dep?.currency = cur
                dep?.let {
                    depViewModel.addDep(dep)
                }
            }

            //click CANCEL
            dialogBuilder.setNegativeButton(
                getString(R.string.cancel)
            ) { _, _ -> }

            val alertDialog = dialogBuilder.create()
            alertDialog.show()
        }
    }

}
