package com.example.fincalc.ui.loan

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import com.example.fincalc.models.credit.TableLoan
import com.example.fincalc.models.cur_met.currencyCodeList
import com.example.fincalc.models.cur_met.currencyFlagList
import com.example.fincalc.models.credit.getEnumFromSelection
import com.example.fincalc.models.credit.getLoanTypeListName
import com.example.fincalc.ui.AdapterSpinnerRates
import com.example.fincalc.ui.customizeAlertDialog
import com.example.fincalc.ui.decimalFormatter1p
import com.example.fincalc.ui.showSnackbar
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.text.DecimalFormat

/**
 * A simple [Fragment] subclass.
 */
class ScheduleFragment(private val formula: Formula) : Fragment() {

    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var adapterRecSchedule: AdapterRecScheduleLoan

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        scheduleViewModel = ViewModelProvider(this).get(ScheduleViewModel::class.java)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)

        adapterRecSchedule = AdapterRecScheduleLoan(null)
        recycler.adapter = adapterRecSchedule


        if (formula != Formula.ANNUITY) {
            if (formula == Formula.DIFFERENTIAL) scheduleViewModel.getScheduleDifferential().observe(
                viewLifecycleOwner,
                Observer {

                    refreshSchedule(it)
                    it?.let {
                        val table = it
                        fab_Loan.setOnClickListener {
                            this.context?.let {
                                getDialog(this.context, table.loan, formula)
                            }
                        }
                    }
                })
            else if (formula == Formula.OVERDRAFT) scheduleViewModel.getScheduleOverdraft().observe(
                viewLifecycleOwner,
                Observer {

                    refreshSchedule(it)
                    it?.let {
                        val schedule = it
                        fab_Loan.setOnClickListener {
                            CoroutineScope(Main).launch {
                                getDialog(context, schedule.loan, formula)
                            }
                        }
                    }
                })
        } else
            scheduleViewModel.getScheduleAnnuity().observe(
                viewLifecycleOwner,
                Observer {

                    refreshSchedule(it)
                    it?.let {
                        val schedule = it
                        fab_Loan.setOnClickListener {
                            CoroutineScope(Main).launch {
                                getDialog(context, schedule.loan, formula)
                            }
                        }
                    }
                })
    }

    private fun refreshSchedule(sch: TableLoan?) {
        if (sch != null) {
            constraintLayoutFragment.visibility = View.VISIBLE
            /*   adapterSchedule.items = sch.rowList
               adapterSchedule.notifyDataSetChanged()*/
            adapterRecSchedule.item = sch
            adapterRecSchedule.notifyDataSetChanged()
            val realRate = context?.getString(R.string.RealRate) + ": " +
                    decimalFormatter1p.format(sch.realRate).toString() + "%"
            tvRealRateLoanAc.text = realRate
            val total = context?.getString(R.string.TOTAL_PAYMENT) + ": " +
                    decimalFormatter1p.format((sch.totalPayment + sch.oneTimeComAndCosts)).toString()
            tvTotalPayLoanAc.text = total
        } else {
            constraintLayoutFragment.visibility = View.GONE
        }
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
            val spinnerCur = dialogView.findViewById<Spinner>(R.id.spinDialCurrency)
            val adapterSpinCur = AdapterSpinnerRates(
                context, R.layout.layoutspinner,
                currencyCodeList, currencyFlagList, true
            )
            adapterSpinCur.setDropDownViewResource(R.layout.layoutspinner)
            spinnerCur.adapter = adapterSpinCur
            spinnerCur.setSelection(adapterSpinCur.count - 4)

            //spinner LoanType
            val spinnerType = dialogView.findViewById<Spinner>(R.id.spinnerDialLoanType)
            val adapterSpinType =
                ArrayAdapter<String>(
                    context,
                    android.R.layout.simple_spinner_item,
                    getLoanTypeListName(context)
                )
            spinnerType.adapter = adapterSpinType
            spinnerType.setSelection(adapterSpinType.count - 1)

            dialogBuilder.setTitle(R.string.DialogTitleSave)

            //click SAVE
            dialogBuilder.setPositiveButton(
                getString(R.string.save)
            ) { _, _ ->

                val bank = etBank.text.toString()
                val cur = spinnerCur.selectedItem.toString()
                val typeString = spinnerType.selectedItem.toString()
                val typeEnum =
                    getEnumFromSelection(
                        typeString,
                        this.context
                    )
                loan.bank = bank
                loan.currency = cur
                typeEnum?.let {
                    loan.type = typeEnum
                }
                loan.formula = formula

                scheduleViewModel.addLoan(loan)

                val success = context.getString(R.string.successSaved)
                showSnackbar(success, fab_Loan, true)
            }

            dialogBuilder.setNegativeButton(
                getString(R.string.cancel)
            ) { _, _ -> }

            val alertDialog = dialogBuilder.create()

            alertDialog.show()
            customizeAlertDialog(alertDialog, true)
            alertDialog.window?.setBackgroundDrawableResource(R.color.LoansPrimary)
        }
    }
}
