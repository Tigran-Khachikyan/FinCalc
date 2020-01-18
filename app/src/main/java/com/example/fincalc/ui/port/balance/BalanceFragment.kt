package com.example.fincalc.ui.port.balance

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.fincalc.R
import com.example.fincalc.data.db.LoanType
import com.example.fincalc.models.loan.getEnumFromSelection
import com.example.fincalc.ui.initialize
import com.example.fincalc.ui.loan.LoanActivity
import com.example.fincalc.ui.port.NavSwitcher
import com.example.fincalc.ui.port.NavViewModel
import com.example.fincalc.ui.port.OnViewHolderClick
import com.nightonke.boommenu.BoomButtons.BoomButton
import com.nightonke.boommenu.OnBoomListenerAdapter
import kotlinx.android.synthetic.main.fragment_balance.*

private const val BUTTON_DIALOG_SIZE_PRESSED = 20F
private const val BUTTON_DIALOG_SIZE_UNPRESSED = 18F

class BalanceFragment : Fragment() {

    private lateinit var balanceViewModel: BalanceViewModel
    private lateinit var adapterRecLoan: AdapterRecLoanBalance
    private lateinit var tvFilterType: TextView
    private lateinit var tvFilterCur: TextView
    private lateinit var tvFilterSort: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        balanceViewModel = ViewModelProvider(this).get(BalanceViewModel::class.java)
        return inflater.inflate(R.layout.fragment_balance, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvFilterType = view.findViewById(R.id.tvLoanTypeFilterBalFr)
        tvFilterCur = view.findViewById(R.id.tvLoanCurFilterBalFr)
        tvFilterSort = view.findViewById(R.id.tvLoanSortFilterBalFr)

        fabRecBalanceAddLoan.setOnClickListener {
            val intent = Intent(context, LoanActivity::class.java)
            startActivity(intent)
            Animatoo.animateSpin(context)
        }

        bmbLoansMenuBalFr.initialize()

//holder click
        val onViewHolderClick = object : OnViewHolderClick {
            override fun openLoan(position: Int) {
            }
        }

        adapterRecLoan = AdapterRecLoanBalance(arrayListOf(), balanceViewModel, onViewHolderClick)
        recyclerLoanBalanceFr.setHasFixedSize(true)
        recyclerLoanBalanceFr.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
        recyclerLoanBalanceFr.adapter = adapterRecLoan
    }

    override fun onResume() {
        super.onResume()
        balanceViewModel.getLoanList().observe(viewLifecycleOwner, Observer { loansFil ->

            val loans = loansFil.loanList
            val types = loansFil.filtTypeList
            val cur = loansFil.filtCur

            val isAcc = loansFil.sortByAcc

            val textLoanHeader = when {
                loans == null || loans.isEmpty() -> "${context?.getString(R.string.noLoanFound)}"
                loans.size == 1 -> "1 ${context?.getString(R.string.loan)}"
                else -> loans.size.toString() + " ${context?.getString(R.string.Loans)}"
            }

            layLoansMenuBalFr.text = textLoanHeader


            loans?.let {

                tvFilterType.setOnClickListener {
                    tvFilterType.visibility = View.GONE
                    balanceViewModel.setLTypeSelec(null)
                }

                tvFilterCur.setOnClickListener {
                    tvFilterCur.visibility = View.GONE
                    balanceViewModel.setCurrencySelec(null)
                }

                tvFilterSort.setOnClickListener {
                    tvFilterSort.visibility = View.GONE
                    balanceViewModel.setSortedByRateAcc(null)
                }

                adapterRecLoan.loanList = it
                adapterRecLoan.notifyDataSetChanged()

                adapterRecLoan.onViewHolderClick = object : OnViewHolderClick {
                    override fun openLoan(position: Int) {

                        NavViewModel.Container.setNav(NavSwitcher.LOANS, position)
                    }
                }

                bmbLoansMenuBalFr.onBoomListener = object : OnBoomListenerAdapter() {
                    override fun onClicked(index: Int, boomButton: BoomButton) {
                        super.onClicked(index, boomButton)
                        when (index) {
                            0 -> getDialFilterByType(context, types)
                            1 -> cur?.let {
                                getDialFilterByCur(context, cur)
                            }
                            2 -> setSortByAcc(isAcc)
                            3 -> balanceViewModel.deleteAllLoans()
                        }
                    }
                }
            }
        })
    }

    @SuppressLint("InflateParams")
    private fun getDialFilterByType(context: Context?, types: List<LoanType>?) {
        if (context != null) {
            val dialogBuilder = AlertDialog.Builder(context)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_loan_filter_type, null)
            dialogBuilder.setView(dialogView)

            //Buttons
            val checkedTypes = arrayListOf<LoanType>()

            fun btnCheck(btn: Button) {
                btn.background = context.getDrawable(R.drawable.btncalculate)
                btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bag, 0, 0, 0)
                btn.textSize = BUTTON_DIALOG_SIZE_PRESSED
                val curEnum = getEnumFromSelection(btn.text.toString(), context)
                checkedTypes.add(curEnum)
            }

            fun btnUncheck(btn: Button) {
                btn.background = context.getDrawable(R.drawable.btnexpand)
                btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                btn.textSize = BUTTON_DIALOG_SIZE_UNPRESSED
                val curEnum = getEnumFromSelection(btn.text.toString(), context)
                if (checkedTypes.contains(curEnum))
                    checkedTypes.remove(curEnum)

            }

            fun isBtnChecked(btn: Button): Boolean = btn.textSize == BUTTON_DIALOG_SIZE_PRESSED * 2


            val btnDialClickList = View.OnClickListener {
                val curBut = it as Button

                if (isBtnChecked(curBut))
                    btnUncheck(curBut)
                else
                    btnCheck(curBut)
            }

            val btnMort: Button = dialogView.findViewById(R.id.btnDialLoanTypeMort)
            btnMort.setOnClickListener(btnDialClickList)

            val btnCar: Button = dialogView.findViewById(R.id.btnDialLoanTypeCar)
            btnCar.setOnClickListener(btnDialClickList)

            val btnBus: Button = dialogView.findViewById(R.id.btnDialLoanTypeBus)
            btnBus.setOnClickListener(btnDialClickList)

            val btnCons: Button = dialogView.findViewById(R.id.btnDialLoanTypeCons)
            btnCons.setOnClickListener(btnDialClickList)

            val btnCrLines: Button = dialogView.findViewById(R.id.btnDialLoanTypeCrLines)
            btnCrLines.setOnClickListener(btnDialClickList)

            val btnDepSec: Button = dialogView.findViewById(R.id.btnDialLoanTypeDepSec)
            btnDepSec.setOnClickListener(btnDialClickList)

            val btnGold: Button = dialogView.findViewById(R.id.btnDialLoanTypeGold)
            btnGold.setOnClickListener(btnDialClickList)

            val btnStud: Button = dialogView.findViewById(R.id.btnDialLoanTypeStud)
            btnStud.setOnClickListener(btnDialClickList)

            val btnUnsec: Button = dialogView.findViewById(R.id.btnDialLoanTypeUnsecured)
            btnUnsec.setOnClickListener(btnDialClickList)

            val btnSelectOrClear: Button = dialogView.findViewById(R.id.btnDialLoanTypeSelectAll)

            fun selectAll() {
                btnCheck(btnMort)
                btnCheck(btnCons)
                btnCheck(btnCar)
                btnCheck(btnStud)
                btnCheck(btnCrLines)
                btnCheck(btnUnsec)
                btnCheck(btnDepSec)
                btnCheck(btnGold)
                btnCheck(btnBus)
                btnCheck(btnSelectOrClear)
            }

            fun clear() {
                btnUncheck(btnMort)
                btnUncheck(btnCons)
                btnUncheck(btnCar)
                btnUncheck(btnStud)
                btnUncheck(btnCrLines)
                btnUncheck(btnUnsec)
                btnUncheck(btnDepSec)
                btnUncheck(btnGold)
                btnUncheck(btnBus)
                btnUncheck(btnSelectOrClear)
            }

            btnSelectOrClear.setOnClickListener {
                val curButton = it as Button
                if (!isBtnChecked(curButton)) {
                    selectAll()
                    curButton.text = context.getString(R.string.CLEAR)
                    curButton.background = context.getDrawable(R.drawable.btnclear)
                } else {
                    clear()
                    curButton.text = context.getString(R.string.SELECT_ALL)
                    curButton.background = context.getDrawable(R.drawable.btnclear)
                }
            }

            //initialize
            when {
                types == null -> btnSelectOrClear.performClick()
                types.isEmpty() -> clear()
                else -> {
                    if (types.contains(LoanType.MORTGAGE)) btnCheck(btnMort)
                    else btnUncheck(btnMort)
                    if (types.contains(LoanType.CONSUMER_LOAN)) btnCheck(btnCons)
                    else btnUncheck(btnCons)
                    if (types.contains(LoanType.CAR_LOAN)) btnCheck(btnCar)
                    else btnUncheck(btnCar)
                    if (types.contains(LoanType.STUDENT_LOAN)) btnCheck(btnStud)
                    else btnUncheck(btnStud)
                    if (types.contains(LoanType.CREDIT_LINES)) btnCheck(btnCrLines)
                    else btnUncheck(btnCrLines)
                    if (types.contains(LoanType.UNSECURED)) btnCheck(btnUnsec)
                    else btnUncheck(btnUnsec)
                    if (types.contains(LoanType.DEPOSIT_SECURED)) btnCheck(btnDepSec)
                    else btnUncheck(btnDepSec)
                    if (types.contains(LoanType.GOLD_PLEDGE_SECURED)) btnCheck(btnGold)
                    else btnUncheck(btnGold)
                    if (types.contains(LoanType.BUSINESS)) btnCheck(btnBus)
                    else btnUncheck(btnBus)
                    if (types.size < 9) btnUncheck(btnSelectOrClear)
                    else btnCheck(btnSelectOrClear)
                }
            }

            //click SAVE
            dialogBuilder.setPositiveButton(
                getString(R.string.save)
            ) { _, _ ->

                if (checkedTypes.size < 9)
                    tvFilterType.visibility = View.VISIBLE
                else
                    tvFilterType.visibility = View.GONE

                balanceViewModel.setLTypeSelec(checkedTypes)
            }

            //click CANCEL
            dialogBuilder.setNegativeButton(
                getString(R.string.cancel)
            ) { _, _ -> }

            val alertDialog = dialogBuilder.create()
            alertDialog.show()
        }
    }

    @SuppressLint("InflateParams")
    private fun getDialFilterByCur(context: Context?, curs: List<String>) {
        if (context != null) {
            val dialogBuilder = AlertDialog.Builder(context)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_filter_currency, null)
            dialogBuilder.setView(dialogView)

            val spinner: Spinner = dialogView.findViewById(R.id.spinDialFilCurr)
            spinner.adapter =
                ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, curs)
            spinner.setSelection(0)

            //click SAVE
            dialogBuilder.setPositiveButton(
                getString(R.string.save)
            ) { _, _ ->

                val selection = spinner.selectedItem.toString()
                val list = arrayListOf<String>()
                list.add(selection)
                balanceViewModel.setCurrencySelec(list)
                tvFilterCur.visibility = View.VISIBLE
            }

            //click CANCEL
            dialogBuilder.setNegativeButton(
                getString(R.string.cancel)
            ) { _, _ -> }

            val alertDialog = dialogBuilder.create()
            alertDialog.show()
        }
    }

    fun setSortByAcc(acc: Boolean?) {

        if (acc == null || acc == false)
            balanceViewModel.setSortedByRateAcc(true)
        else
            balanceViewModel.setSortedByRateAcc(false)
        tvFilterSort.visibility = View.VISIBLE

    }

    override fun onPause() {
        super.onPause()
        balanceViewModel.removeSources()
    }

}