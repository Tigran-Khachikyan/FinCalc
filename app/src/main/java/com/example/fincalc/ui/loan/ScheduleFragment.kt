package com.example.fincalc.ui.loan

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fincalc.R
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.models.credit.Formula
import com.example.fincalc.models.credit.getLoanTypeFromString
import com.example.fincalc.models.credit.getLoanTypesNames
import com.example.fincalc.models.rates.arrayCurCodes
import com.example.fincalc.ui.*
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * A simple [Fragment] subclass.
 */
class ScheduleFragment() : Fragment(), CoroutineScope {

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Main + job
    private var form: Formula? = null

    constructor(formula: Formula) : this() {
        this.form = formula
    }

    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var adapterRecSchedule: AdapterRecScheduleLoan

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        job = Job()
        scheduleViewModel = ViewModelProvider(this).get(ScheduleViewModel::class.java)
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabSaveLoan.setSvgColor(requireContext(), android.R.color.white)

        recycler.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)

        adapterRecSchedule = AdapterRecScheduleLoan(null)
        recycler.adapter = adapterRecSchedule


        fabSaveLoan.setOnClickListener {
            launch {
                form?.let {
                    val loan = ScheduleViewModel.Container.schedules.value?.get(0)?.loan
                    loan?.let { getDialog(requireContext(), loan, form as Formula) }
                    hideKeyboard(requireActivity())
                }
            }
        }

        scheduleViewModel.getSchedules().observe(viewLifecycleOwner, Observer {

            if (it != null && form != null) {
                constraintLayoutFragment.visibility = View.VISIBLE
                val schedule = it.filter { sch -> sch.formulaLoan == form }[0]

                adapterRecSchedule.item = schedule
                adapterRecSchedule.notifyDataSetChanged()

                val realRate = decimalFormatter2p.format(schedule.realRate).replace(',', '.') + "%"
                tvRealRateLoanAc.text = realRate
                val total =
                    decimalFormatter1p.format((schedule.totalPayment + schedule.oneTimeComAndCosts))
                tvTotalPayLoanAc.text = total

            } else constraintLayoutFragment.visibility = View.GONE
        })
    }

    @SuppressLint("InflateParams")
    private fun getDialog(context: Context?, loan: Loan, formula: Formula) {
        if (context != null) {
            val dialogBuilder = AlertDialog.Builder(context)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_save, null)
            dialogBuilder.setView(dialogView)

            val etBank = dialogView.findViewById<EditText>(R.id.etDialBank)

            //spinner Currency
            val spinnerCur: Spinner = dialogView.findViewById(R.id.spinDialCurrency)
            val adapterSpinCur = AdapterSpinnerRates(
                context, R.layout.spinner_currencies, arrayCurCodes
            )
            adapterSpinCur.setDropDownViewResource(R.layout.spinner_currencies)
            spinnerCur.adapter = adapterSpinCur

            //spinner LoanType
            val spinnerType: Spinner = dialogView.findViewById(R.id.spinnerDialLoanType)
            val adapterSpinType =
                AdapterSpinnerTypes(
                    context, R.layout.spinner_loan_types, getLoanTypesNames(context)
                )
            adapterSpinType.setDropDownViewResource(R.layout.spinner_loan_types)
            spinnerType.adapter = adapterSpinType
            spinnerType.setSelection(adapterSpinType.count - 1)
            dialogBuilder.setTitle(R.string.savingOptions)

            //click SAVE
            dialogBuilder.setPositiveButton(getString(R.string.save)) { _, _ ->

                val typeEnum = getLoanTypeFromString(
                    spinnerType.selectedItem.toString(), requireContext()
                )
                loan.bank = etBank.text.toString()
                loan.currency = spinnerCur.selectedItem.toString()
                typeEnum?.let { loan.type = typeEnum }
                loan.formula = formula
                loan.date = formatterShort.format(Date())
                scheduleViewModel.addLoan(loan)

                showSnackBar(R.string.successSaved, fabSaveLoan)
                hideKeyboard(this.requireActivity())
            }

            dialogBuilder.setNegativeButton(getString(R.string.cancel)) { _, _ -> }

            val alertDialog = dialogBuilder.create()

            alertDialog.show()
            alertDialog.setCustomView()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
    }
}
