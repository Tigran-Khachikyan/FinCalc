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
import com.example.fincalc.data.db.dep.Deposit
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.models.credit.LoanType
import com.example.fincalc.models.credit.getEnumFromSelection
import com.example.fincalc.models.deposit.Frequency
import com.example.fincalc.models.deposit.getFreqFromSelec
import com.example.fincalc.ui.dep.DepositActivity
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

        fabRecBalanceAddDep.setOnClickListener {
            val intent = Intent(context, DepositActivity::class.java)
            startActivity(intent)
            Animatoo.animateInAndOut(context)
        }

        bmbLoansMenuBalFr.initialize()
        bmbDepMenuBalFr.initialize()


        //Loan adapter init
        adapterRecLoan = AdapterRecBalance(arrayListOf(), balanceViewModel, null)
        recyclerLoanBalanceFr.setHasFixedSize(true)
        recyclerLoanBalanceFr.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
        recyclerLoanBalanceFr.adapter = adapterRecLoan

        //Deposit adapter init
        adapterRecDep = AdapterRecBalance(arrayListOf(), balanceViewModel, null)
        recyclerDepBalanceFr.setHasFixedSize(true)
        recyclerDepBalanceFr.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
        recyclerDepBalanceFr.adapter = adapterRecDep
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
                            1 -> cur?.let { getDialFilterByCur(context, cur, true) }
                            2 -> setSortLoanByAcc(isAcc)
                            3 -> balanceViewModel.deleteAllLoans()
                        }
                    }
                }
            }
        })

        //Deposits
        balanceViewModel.getDepList().observe(viewLifecycleOwner, Observer { depFil ->

            val depList = depFil.prodList as List<Deposit>?
            val freq = depFil.freqList
            val cur = depFil.curList
            val isAcc = depFil.sortByAcc

            val textDepHeader = when {
                depList == null || depList.isEmpty() -> "${context?.getString(R.string.noDepositFound)}"
                depList.size == 1 -> "1 ${context?.getString(R.string.deposit)}"
                else -> depList.size.toString() + " ${context?.getString(R.string.deposits)}"
            }

            layDepMenuBalFr.text = textDepHeader
            depList?.let {

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
                            0 -> getDialFilByDepFreq(context, freq)
                            1 -> cur?.let { getDialFilterByCur(context, cur, false) }
                            2 -> setSortDepByAcc(isAcc)
                            3 -> balanceViewModel.deleteAllDep()
                        }
                    }
                }
            }
        })
    }

    //Loans
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
                showSelected(btn, context)
                val curEnum = getEnumFromSelection(btn.text.toString(), context)
                checkedTypes.add(curEnum)
            }

            fun btnUncheck(btn: Button) {
                showUnSelected(btn, context)
                val curEnum = getEnumFromSelection(btn.text.toString(), context)
                if (checkedTypes.contains(curEnum))
                    checkedTypes.remove(curEnum)
            }

            val btnDialClickList = View.OnClickListener {

                val curBut = it as Button
                if (isBtnChecked(curBut)) btnUncheck(curBut)
                else btnCheck(curBut)
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
                    curButton.background = context.getDrawable(R.drawable.final_btnclear)
                } else {
                    clear()
                    curButton.text = context.getString(R.string.SELECT_ALL)
                    curButton.background = context.getDrawable(R.drawable.final_btnclear)
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

    fun setSortLoanByAcc(acc: Boolean?) {
        when (acc) {
            null, false -> balanceViewModel.setSortByLoanRate(true)
            true -> balanceViewModel.setSortByLoanRate(false)
        }
        tvLoanSortFilterBalFr.visibility = View.VISIBLE
    }


    //Deposit
    @SuppressLint("InflateParams")
    private fun getDialFilByDepFreq(context: Context?, freq: List<Frequency>?) {
        if (context != null) {
            val dialogBuilder = AlertDialog.Builder(context)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_filter_type, null)
            dialogBuilder.setView(dialogView)

            //Buttons
            val checkedFreq = arrayListOf<Frequency>()

            fun btnCheck(btn: Button) {
                showSelected(btn, context)
                val curFreq = getFreqFromSelec(btn.text.toString(), context)
                checkedFreq.add(curFreq)
            }

            fun btnUncheck(btn: Button) {
                showUnSelected(btn, context)
                val curFreq =
                    getFreqFromSelec(btn.text.toString(), context)
                if (checkedFreq.contains(curFreq))
                    checkedFreq.remove(curFreq)
            }

            val btnDialClickList = View.OnClickListener {
                val curBut = it as Button
                if (isBtnChecked(curBut)) btnUncheck(curBut)
                else btnCheck(curBut)
            }

            dialogView.findViewById<Button>(R.id.btnDialLoanTypeCrLines).visibility = View.GONE
            dialogView.findViewById<Button>(R.id.btnDialLoanTypeDepSec).visibility = View.GONE
            dialogView.findViewById<Button>(R.id.btnDialLoanTypeGold).visibility = View.GONE
            dialogView.findViewById<Button>(R.id.btnDialLoanTypeStud).visibility = View.GONE
            dialogView.findViewById<Button>(R.id.btnDialLoanTypeUnsecured).visibility = View.GONE

            val btnMonthly: Button = dialogView.findViewById(R.id.btnDialLoanTypeMort)
            val btnQuart: Button = dialogView.findViewById(R.id.btnDialLoanTypeCar)
            val btnAtTheEnd: Button = dialogView.findViewById(R.id.btnDialLoanTypeBus)
            val btnOther: Button = dialogView.findViewById(R.id.btnDialLoanTypeCons)
            val btnSelAllOrClear: Button = dialogView.findViewById(R.id.btnDialSelectOrClear)

            btnMonthly.text = context.getString(R.string.MonthlyPaymentDep)
            btnQuart.text = context.getString(R.string.QuarterlyPaymentDep)
            btnAtTheEnd.text = context.getString(R.string.AtTheEndPayment)
            btnOther.text = context.getString(R.string.OTHER)

            btnMonthly.setOnClickListener(btnDialClickList)
            btnQuart.setOnClickListener(btnDialClickList)
            btnAtTheEnd.setOnClickListener(btnDialClickList)
            btnOther.setOnClickListener(btnDialClickList)

            fun selectAll() {
                btnCheck(btnMonthly)
                btnCheck(btnOther)
                btnCheck(btnQuart)
                btnCheck(btnAtTheEnd)
                btnCheck(btnSelAllOrClear)
            }

            fun clear() {
                btnUncheck(btnMonthly)
                btnUncheck(btnOther)
                btnUncheck(btnQuart)
                btnUncheck(btnAtTheEnd)
                btnUncheck(btnSelAllOrClear)
            }

            btnSelAllOrClear.setOnClickListener {
                val curButton = it as Button
                if (!isBtnChecked(curButton)) {
                    selectAll()
                    curButton.text = context.getString(R.string.CLEAR)
                    curButton.background = context.getDrawable(R.drawable.final_btnclear)
                } else {
                    clear()
                    curButton.text = context.getString(R.string.SELECT_ALL)
                    curButton.background = context.getDrawable(R.drawable.final_btnclear)
                }
            }

            //initialize
            when {
                freq == null -> btnSelAllOrClear.performClick()
                freq.isEmpty() -> clear()
                else -> {
                    if (freq.contains(Frequency.MONTHLY)) btnCheck(btnMonthly)
                    else btnUncheck(btnMonthly)
                    if (freq.contains(Frequency.QUARTERLY)) btnCheck(btnQuart)
                    else btnUncheck(btnQuart)
                    if (freq.contains(Frequency.AT_THE_END)) btnCheck(btnAtTheEnd)
                    else btnUncheck(btnAtTheEnd)
                    if (freq.contains(Frequency.OTHER)) btnCheck(btnOther)
                    else btnUncheck(btnOther)
                    if (freq.size < 4) btnUncheck(btnSelAllOrClear)
                    else btnCheck(btnSelAllOrClear)
                }
            }

            //click SAVE
            dialogBuilder.setPositiveButton(
                getString(R.string.save)
            ) { _, _ ->

                if (checkedFreq.size < 4)
                    tvDepTypeFilterBalFr.visibility = View.VISIBLE
                else
                    tvDepTypeFilterBalFr.visibility = View.GONE

                balanceViewModel.setSelDepFreqList(checkedFreq)
            }
            //click CANCEL
            dialogBuilder.setNegativeButton(
                getString(R.string.cancel)
            ) { _, _ -> }

            val alertDialog = dialogBuilder.create()
            alertDialog.show()
        }
    }

    fun setSortDepByAcc(acc: Boolean?) {
        when (acc) {
            null, false -> balanceViewModel.setSortByDepRate(true)
            true -> balanceViewModel.setSortByDepRate(false)
        }
        tvDepSortFilterBalFr.visibility = View.VISIBLE
    }


    //Common
    @SuppressLint("InflateParams")
    private fun getDialFilterByCur(context: Context?, curs: List<String>, loan: Boolean) {
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

                if (loan) {
                    balanceViewModel.setSelLoanCurList(list)
                    tvLoanCurFilterBalFr.visibility = View.VISIBLE
                } else {
                    balanceViewModel.setSelDepCurList(list)
                    tvDepCurFilterBalFr.visibility = View.VISIBLE
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

    private fun showSelected(btn: Button, context: Context?) {
        btn.background = context?.getDrawable(R.drawable.final_btncalculate)
        btn.setCompoundDrawablesWithIntrinsicBounds(0, 0,  R.mipmap.checked,0)
        btn.textSize = BUTTON_DIALOG_SIZE_PRESSED
    }

    private fun showUnSelected(btn: Button, context: Context?) {
        btn.background = context?.getDrawable(R.drawable.final_btnexpand)
        btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        btn.textSize = BUTTON_DIALOG_SIZE_UNPRESSED
    }

    private fun isBtnChecked(btn: Button): Boolean = btn.textSize == BUTTON_DIALOG_SIZE_PRESSED * 2

    override fun onPause() {
        super.onPause()
        balanceViewModel.removeSources()
    }

}