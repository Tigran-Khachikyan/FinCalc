package com.example.fincalc.ui.loan

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.example.fincalc.data.db.Loan
import com.example.fincalc.models.cur_met.currencyCodeList
import com.example.fincalc.models.cur_met.currencyFlagList
import com.example.fincalc.models.loan.*
import com.example.fincalc.ui.AdapterSpinnerRates
import com.example.fincalc.ui.port.balance.BalanceViewModel
import kotlinx.android.synthetic.main.fragment_schedule.*

/**
 * A simple [Fragment] subclass.
 */
class ScheduleFragment(private val formula: FormulaLoan) : Fragment() {

    private lateinit var recyclerViewModel: ScheduleViewModel
    private lateinit var balanceViewModel: BalanceViewModel
    private lateinit var adapterRecSchedule: AdapterRecScheduleLoan

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        recyclerViewModel = ViewModelProvider(this).get(ScheduleViewModel::class.java)
        balanceViewModel = ViewModelProvider(this).get(BalanceViewModel::class.java)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)

        adapterRecSchedule = AdapterRecScheduleLoan(null)
        recycler.adapter = adapterRecSchedule


        if (formula != FormulaLoan.ANNUITY) {
            if (formula == FormulaLoan.DIFFERENTIAL) recyclerViewModel.getDiff().observe(
                viewLifecycleOwner,
                Observer {

                    it?.let {
                        val schedule = it
                        refreshSchedule(schedule)
                        fab_Loan.setOnClickListener {
                            this.context?.let {
                                getDialog(this.context, schedule.queryLoan, formula)
                            }
                        }
                    }
                })
            else if (formula == FormulaLoan.OVERDRAFT) recyclerViewModel.getOver().observe(
                viewLifecycleOwner,
                Observer {

                    it?.let {
                        val schedule = it
                        refreshSchedule(schedule)
                        fab_Loan.setOnClickListener {
                            this.context?.let {
                                getDialog(this.context, schedule.queryLoan, formula)
                            }
                        }
                    }
                })
        } else
            recyclerViewModel.getAnnuity().observe(
                viewLifecycleOwner,
                Observer {

                    it?.let {
                        Log.d("sww","set  _mLDAnnuity.value: ${it}")
                        val schedule = it
                        refreshSchedule(schedule)
                        fab_Loan.setOnClickListener {
                            this.context?.let {
                                getDialog(this.context, schedule.queryLoan, formula)
                            }
                        }
                    }
                })
    }


    private fun refreshSchedule(sch: ScheduleLoan?) {
        if (sch != null) {
            constraintLayoutFragment.visibility = View.VISIBLE
            /*   adapterSchedule.items = sch.rowList
               adapterSchedule.notifyDataSetChanged()*/
            adapterRecSchedule.item = sch
            adapterRecSchedule.notifyDataSetChanged()
        } else {
            constraintLayoutFragment.visibility = View.GONE
        }
    }

    @SuppressLint("InflateParams")
    private fun getDialog(context: Context?, queryLoan: QueryLoan, formula: FormulaLoan) {
        if (context != null) {
            val dialogBuilder = AlertDialog.Builder(context)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_loan, null)
            dialogBuilder.setView(dialogView)

            val etBank = dialogView.findViewById<EditText>(R.id.etDialLoanBank)

            //spinner Currency
            val spinnerCur = dialogView.findViewById<Spinner>(R.id.spinnerDialLoanCurrency)
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
            spinnerType.setSelection(adapterSpinType.count-1)


            //click SAVE
            dialogBuilder.setPositiveButton(
                getString(R.string.save)
            ) { _, _ ->

                val bank = etBank.text.toString()
                val cur = spinnerCur.selectedItem.toString()
                val typeString = spinnerType.selectedItem.toString()
                val typeEnum = getEnumFromSelection(typeString,this.context)

                val newLoan = Loan(bank,typeEnum,formula,cur,queryLoan)

                Log.d("jjj","bank: $bank, cur: $cur, type: $typeString, \n " +
                        "newLoan.formula: ${newLoan.repayment_program} , " +
                        "newLoan.query.Amount: ${newLoan.queryLoan.sum}")

                balanceViewModel.addLoan(newLoan)

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
