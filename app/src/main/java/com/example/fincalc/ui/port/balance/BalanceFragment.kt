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
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.models.credit.LoanType
import com.example.fincalc.models.credit.getEnumFromSelection
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

@Suppress("UNCHECKED_CAST")
class BalanceFragment : Fragment() {

    private lateinit var balanceViewModel: BalanceViewModel
    private lateinit var adapterRecLoan: AdapterRecBalance
    private lateinit var adapterRecDep: AdapterRecBalance

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

        fabRecBalanceAddLoan.setOnClickListener {
            val intent = Intent(context, LoanActivity::class.java)
            startActivity(intent)
            Animatoo.animateSpin(context)
        }

        bmbLoansMenuBalFr.initialize()
        bmbDepMenuBalFr.initialize()


        adapterRecLoan = AdapterRecBalance(arrayListOf(), balanceViewModel, null)
        recyclerLoanBalanceFr.setHasFixedSize(true)
        recyclerLoanBalanceFr.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
        recyclerLoanBalanceFr.adapter = adapterRecLoan
    }

    override fun onResume() {
        super.onResume()
        //Loans
        balanceViewModel.getLoanList().observe(viewLifecycleOwner, Observer { loansFil ->

            val loans = loansFil.prodList as List<Loan>?
            val types = loansFil.filTypeList
            val cur = loansFil.curList
            val isAcc = loansFil.sortByAcc

            val textLoanHeader = when {
                loans == null || loans.isEmpty() -> "${context?.getString(R.string.noLoanFound)}"
                loans.size == 1 -> "1 ${context?.getString(R.string.loan)}"
                else -> loans.size.toString() + " ${context?.getString(R.string.Loans)}"
            }

            layLoansMenuBalFr.text = textLoanHeader


            loans?.let {

                tvLoanTypeFilterBalFr.setOnClickListener {
                    tvLoanTypeFilterBalFr.visibility = View.GONE
                    balanceViewModel.setSelLoanTypeList(null)
                }

                tvLoanCurFilterBalFr.setOnClickListener {
                    tvLoanCurFilterBalFr.visibility = View.GONE
                    balanceViewModel.setSelLoanCurList(null)
                }

                tvLoanSortFilterBalFr.setOnClickListener {
                    tvLoanSortFilterBalFr.visibility = View.GONE
                    balanceViewModel.setSortByLoanRate(null)
                }

                adapterRecLoan.list = it
                adapterRecLoan.notifyDataSetChanged()

                adapterRecLoan.onViewHolderClick = object : OnViewHolderClick {
                    override fun openBankProdById(position: Int) {

                        NavViewModel.Container.setNav(NavSwitcher.LOANS, position)
                    }
                }

                bmbLoansMenuBalFr.onBoomListener = object : OnBoomListenerAdapter() {
                    override fun onClicked(index: Int, boomButton: BoomButton) {
                        super.onClicked(index, boomButton)
                        when (index) {
                            0 -> getDialFilByLoanType(context, types)
                            1 -> cur?.let {
                                getDialFilByLoanCur(context, cur)
                            }
                            2 -> setSortLoanByAcc(isAcc)
                            3 -> balanceViewModel.deleteAllLoans()
                        }
                    }
                }
            }
        })


        /*//Deposits
        balanceViewModel.getDepList().observe(viewLifecycleOwner, Observer { depFil ->

            val deps = depFil.prodList as List<Deposit>?
            val freq = depFil.freqList
            val cur = depFil.curList
            val isAcc = depFil.sortByAcc

            val textDepHeader = when {
                deps == null || deps.isEmpty() -> "${context?.getString(R.string.noDepositFound)}"
                deps.size == 1 -> "1 ${context?.getString(R.string.deposit)}"
                else -> deps.size.toString() + " ${context?.getString(R.string.deposits)}"
            }

            layDepMenuBalFr.text = textDepHeader
            deps?.let {

                tvDepTypeFilterBalFr.setOnClickListener {
                    tvDepTypeFilterBalFr.visibility = View.GONE
                    balanceViewModel.setSelDepFreqList(null)
                }

                tvDepCurFilterBalFr.setOnClickListener {
                    tvDepCurFilterBalFr.visibility = View.GONE
                    balanceViewModel.setSelDepCurList(null)
                }

                tvDepSortFilterBalFr.setOnClickListener {
                    tvDepSortFilterBalFr.visibility = View.GONE
                    balanceViewModel.setSortByDepRate(null)
                }

                adapterRecDep.list = it
                adapterRecDep.notifyDataSetChanged()

                adapterRecDep.onViewHolderClick = object : OnViewHolderClick {
                    override fun openBankProdById(position: Int) {

                        NavViewModel.Container.setNav(NavSwitcher.DEPOSITS, position)
                    }
                }

                bmbDepMenuBalFr.onBoomListener = object : OnBoomListenerAdapter() {
                    override fun onClicked(index: Int, boomButton: BoomButton) {
                        super.onClicked(index, boomButton)
                        when (index) {
                            0 -> getDialFilByLoanType(context, types)
                            1 -> cur?.let {
                                getDialFilByLoanCur(context, cur)
                            }
                            2 -> setSortByAcc(isAcc, false)
                            3 -> balanceViewModel.deleteAllLoans()
                        }
                    }
                }
            }
        })*/
    }

    @SuppressLint("InflateParams")
    private fun getDialFilByLoanType(context: Context?, types: List<LoanType>?) {
        if (context != null) {
            val dialogBuilder = AlertDialog.Builder(context)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_filter_type, null)
            dialogBuilder.setView(dialogView)

            //Buttons
            val checkedTypes = arrayListOf<LoanType>()

            fun btnCheck(btn: Button) {
                btn.background = context.getDrawable(R.drawable.btncalculate)
                btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bag, 0, 0, 0)
                btn.textSize = BUTTON_DIALOG_SIZE_PRESSED
                val curEnum =
                    getEnumFromSelection(
                        btn.text.toString(),
                        context
                    )
                checkedTypes.add(curEnum)
            }

            fun btnUncheck(btn: Button) {
                btn.background = context.getDrawable(R.drawable.btnexpand)
                btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                btn.textSize = BUTTON_DIALOG_SIZE_UNPRESSED
                val curEnum =
                    getEnumFromSelection(
                        btn.text.toString(),
                        context
                    )
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
            val btnCar: Button = dialogView.findViewById(R.id.btnDialLoanTypeCar)
            val btnBus: Button = dialogView.findViewById(R.id.btnDialLoanTypeBus)
            val btnCons: Button = dialogView.findViewById(R.id.btnDialLoanTypeCons)
            val btnCrLines: Button = dialogView.findViewById(R.id.btnDialLoanTypeCrLines)
            val btnDepSec: Button = dialogView.findViewById(R.id.btnDialLoanTypeDepSec)
            val btnGoldSec: Button = dialogView.findViewById(R.id.btnDialLoanTypeGold)
            val btnStud: Button = dialogView.findViewById(R.id.btnDialLoanTypeStud)
            val btnUnsec: Button = dialogView.findViewById(R.id.btnDialLoanTypeUnsecured)
            val btnSelAllOrClear: Button = dialogView.findViewById(R.id.btnDialSelectOrClear)

            btnMort.setOnClickListener(btnDialClickList)
            btnCar.setOnClickListener(btnDialClickList)
            btnBus.setOnClickListener(btnDialClickList)
            btnCons.setOnClickListener(btnDialClickList)
            btnCrLines.setOnClickListener(btnDialClickList)
            btnDepSec.setOnClickListener(btnDialClickList)
            btnGoldSec.setOnClickListener(btnDialClickList)
            btnStud.setOnClickListener(btnDialClickList)
            btnUnsec.setOnClickListener(btnDialClickList)

            fun selectAll() {
                btnCheck(btnMort)
                btnCheck(btnCons)
                btnCheck(btnCar)
                btnCheck(btnStud)
                btnCheck(btnCrLines)
                btnCheck(btnUnsec)
                btnCheck(btnDepSec)
                btnCheck(btnGoldSec)
                btnCheck(btnBus)
                btnCheck(btnSelAllOrClear)
            }

            fun clear() {
                btnUncheck(btnMort)
                btnUncheck(btnCons)
                btnUncheck(btnCar)
                btnUncheck(btnStud)
                btnUncheck(btnCrLines)
                btnUncheck(btnUnsec)
                btnUncheck(btnDepSec)
                btnUncheck(btnGoldSec)
                btnUncheck(btnBus)
                btnUncheck(btnSelAllOrClear)
            }

            btnSelAllOrClear.setOnClickListener {
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
                types == null -> btnSelAllOrClear.performClick()
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
                    if (types.contains(LoanType.GOLD_PLEDGE_SECURED)) btnCheck(btnGoldSec)
                    else btnUncheck(btnGoldSec)
                    if (types.contains(LoanType.BUSINESS)) btnCheck(btnBus)
                    else btnUncheck(btnBus)
                    if (types.size < 9) btnUncheck(btnSelAllOrClear)
                    else btnCheck(btnSelAllOrClear)
                }
            }

            //click SAVE
            dialogBuilder.setPositiveButton(
                getString(R.string.save)
            ) { _, _ ->

                if (checkedTypes.size < 9)
                    tvLoanTypeFilterBalFr.visibility = View.VISIBLE
                else
                    tvLoanTypeFilterBalFr.visibility = View.GONE

                balanceViewModel.setSelLoanTypeList(checkedTypes)
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
    private fun getDialFilByLoanCur(context: Context?, curs: List<String>) {
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
                balanceViewModel.setSelLoanCurList(list)
                tvLoanCurFilterBalFr.visibility = View.VISIBLE
            }

            //click CANCEL
            dialogBuilder.setNegativeButton(
                getString(R.string.cancel)
            ) { _, _ -> }

            val alertDialog = dialogBuilder.create()
            alertDialog.show()
        }
    }

    fun setSortLoanByAcc(acc: Boolean?) {
        when (acc) {
            null, false -> balanceViewModel.setSortByLoanRate(true)
            true -> balanceViewModel.setSortByLoanRate(false)
        }
        tvLoanSortFilterBalFr.visibility = View.VISIBLE
    }

    fun setSortDepByAcc(acc: Boolean?) {
        when (acc) {
            null, false -> balanceViewModel.setSortByDepRate(true)
            true -> balanceViewModel.setSortByDepRate(false)
        }
        tvDepSortFilterBalFr.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        balanceViewModel.removeSources()
    }
}