package com.example.fincalc.ui.dep


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.fincalc.R
import com.example.fincalc.data.db.dep.DepFrequencyConverter
import com.example.fincalc.data.db.dep.Deposit
import com.example.fincalc.models.deposit.TableDep
import com.example.fincalc.models.rates.arrayCurCodes
import com.example.fincalc.ui.*
import kotlinx.android.synthetic.main.fragment_dep_schedule.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class DepScheduleFragment : Fragment() {

    private lateinit var adapterRec: AdapterRecViewDep
    private lateinit var depViewModel: DepositViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        depViewModel = ViewModelProvider(this).get(DepositViewModel::class.java)
        return inflater.inflate(R.layout.fragment_dep_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = this.arguments
        bundle?.let {

            val amount = bundle.getLong(KEY_AMOUNT, 0)
            val months = bundle.getInt(KEY_TERM, 0)
            val rate = bundle.getFloat(KEY_RATE, 0.0F)
            val capitalize = bundle.getBoolean(KEY_CAPITALIZATION, false)
            val taxRate = bundle.getFloat(KEY_TAX_RATE, 0F)
            val freqString = bundle.getString(KEY_FREQUENCY, "")
            val frequency = DepFrequencyConverter().fromStringToEnum(freqString)

            val dep = Deposit(amount, months, rate, capitalize, taxRate, frequency)
            val scheduleDep = TableDep(dep)

            adapterRec = AdapterRecViewDep(scheduleDep)
            recyclerDepReport.setHasFixedSize(true)
            recyclerDepReport.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            recyclerDepReport.adapter = adapterRec

            val effectiveRate = decimalFormatter2p.format(scheduleDep.effectiveRate) + "%"
            val totalIncome = decimalFormatter1p.format(scheduleDep.totalPerAfterTax)
            tvTotalIncomeResDep.text = totalIncome
            tvEffRateResDep.text = effectiveRate

            fab_AddDep.setOnClickListener {
                showDialogSaveDeposit(requireContext(), it, dep, depViewModel)
            }
        }
    }

    //Deposit save
    @SuppressLint("InflateParams")
    private fun showDialogSaveDeposit(
        context: Context, view: View, dep: Deposit, depViewModel: DepositViewModel
    ) {
        val dialogBuilder = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_save, null)
        dialogBuilder.setView(dialogView)

        dialogBuilder.setTitle(R.string.savingOptions)
        val etBank: EditText = dialogView.findViewById(R.id.etDialBank)

        //spinner Currency
        val spinnerCur: Spinner = dialogView.findViewById(R.id.spinDialCurrency)
        val adapterSpinCur = AdapterSpinnerRates(
            context, R.layout.spinner_currencies, arrayCurCodes
        )
        adapterSpinCur.setDropDownViewResource(R.layout.spinner_currencies)
        spinnerCur.adapter = adapterSpinCur

        //spinner LoanType
        val spinnerType: Spinner = dialogView.findViewById(R.id.spinnerDialLoanType)
        spinnerType.visibility = View.GONE
        val tvSpinner: TextView = dialogView.findViewById(R.id.tvDialLoanType)
        tvSpinner.visibility = View.GONE

        //click SAVE
        dialogBuilder.setPositiveButton(
            context.getString(R.string.save)
        ) { _, _ ->

            dep.bank = etBank.text.toString()
            dep.currency = spinnerCur.selectedItem.toString()
            dep.date = formatterShort.format(Date())
            depViewModel.addDep(dep)
            showSnackBar(R.string.successSaved, view)
            hideKeyboard(requireActivity())
        }

        //click CANCEL
        dialogBuilder.setNeutralButton(
            getString(R.string.cancel)
        ) { _, _ -> }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        alertDialog.setCustomView()
        alertDialog.window?.setBackgroundDrawableResource(R.color.DepPrimary)
    }

}
