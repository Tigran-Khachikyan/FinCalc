package com.example.fincalc.ui.port.home


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo

import com.example.fincalc.R
import com.example.fincalc.data.db.dep.Deposit
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.models.Banking
import com.example.fincalc.models.credit.LoanType
import com.example.fincalc.models.credit.getLoanTypeFromString
import com.example.fincalc.models.deposit.Frequency
import com.example.fincalc.models.deposit.getFreqFromString
import com.example.fincalc.ui.*
import com.example.fincalc.ui.dep.DepositActivity
import com.example.fincalc.ui.loan.LoanActivity
import com.example.fincalc.ui.port.AdapterRecBanking
import com.example.fincalc.ui.port.OnHolderDeleteClick
import com.example.fincalc.ui.port.OnViewHolderClick
import com.example.fincalc.ui.port.PortViewModel
import com.example.fincalc.ui.port.deps.DepositFragment
import com.example.fincalc.ui.port.loans.LoansFragment
import com.nightonke.boommenu.BoomButtons.BoomButton
import com.nightonke.boommenu.OnBoomListenerAdapter
import kotlinx.android.synthetic.main.fragment_home.*

const val LOAN_ID_KEY = "Loan Key"
const val DEPOSIT_ID_KEY = "Deposit Key"

@Suppress("UNCHECKED_CAST")
class HomeFragment : Fragment() {

    private lateinit var portViewModel: PortViewModel
    private lateinit var adapterRecLoan: AdapterRecBanking
    private lateinit var adapterRecDep: AdapterRecBanking

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        portViewModel = ViewModelProvider(this).get(PortViewModel::class.java)

        tvStatusPort.setFont(FONT_PATH)

        fabAddLoan.setOnClickListener {
            val intent = Intent(requireActivity(), LoanActivity::class.java)
            startActivity(intent)
            Animatoo.animateSpin(requireActivity())
        }

        fabAddDep.setOnClickListener {
            val intent = Intent(requireActivity(), DepositActivity::class.java)
            startActivity(intent)
            Animatoo.animateInAndOut(requireActivity())
        }

        bmbLoansMenu.initialize(BMBTypes.LOAN)
        bmbDepMenu.initialize(BMBTypes.DEPOSIT)


        //Loan adapter init
        adapterRecLoan = AdapterRecBanking(arrayListOf(), null, null)
        recLoanPort.setHasFixedSize(true)
        recLoanPort.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        recLoanPort.adapter = adapterRecLoan

        //Deposit adapter init
        adapterRecDep = AdapterRecBanking(arrayListOf(), null, null)
        recDepPort.setHasFixedSize(true)
        recDepPort.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        recDepPort.adapter = adapterRecDep

        //filter closing
        tvLoanTypeFilter.setOnClickListener {
            tvLoanTypeFilter.visibility = View.GONE
            portViewModel.setSelLoanTypeList(null)
        }

        tvLoanCurFilter.setOnClickListener {
            tvLoanCurFilter.visibility = View.GONE
            portViewModel.setSelLoanCurList(null)
        }

        tvLoanSortFilter.setOnClickListener {
            tvLoanSortFilter.visibility = View.GONE
            portViewModel.setSortByLoanRate(null)
        }

        tvDepTypeFilter.setOnClickListener {
            tvDepTypeFilter.visibility = View.GONE
            portViewModel.setSelDepFreqList(null)
        }

        tvDepCurFilter.setOnClickListener {
            tvDepCurFilter.visibility = View.GONE
            portViewModel.setSelDepCurList(null)
        }

        tvDepSortFilter.setOnClickListener {
            tvDepSortFilter.visibility = View.GONE
            portViewModel.setSortByDepRate(null)
        }
    }


    override fun onStart() {
        super.onStart()

        //Loans
        portViewModel.getLoanList().observe(this, Observer { it ->



            val loans = it.bankingList as List<Loan>?
            val types = it.loanTypeList
            val currencies = it.currencies
            val isSortedAcc = it.isSortedAscending

            val textLoanHeader = when {
                loans == null || loans.isEmpty() -> getString(R.string.noLoanFound)
                loans.size == 1 -> "1 ${getString(R.string.loan)}"
                else -> loans.size.toString() + " " + getString(R.string.Loans)
            }
            tvLoanHeader.text = textLoanHeader

            loans?.let {

                adapterRecLoan.list = it
                adapterRecLoan.notifyDataSetChanged()

                adapterRecLoan.onViewHolderClick = object : OnViewHolderClick {
                    override fun openBankingFragment(id: Int) {
                        val bundle = Bundle()
                        bundle.putInt(LOAN_ID_KEY, id)
                        val loanFragment = LoansFragment()
                        loanFragment.arguments = bundle
                        Log.d("ftft", "LOANFRAGMENT: $loanFragment")
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.add(R.id.FragmentContainerPort, loanFragment)?.addToBackStack("")?.commit()
                    }
                }

                adapterRecLoan.onHolderDeleteClick = object : OnHolderDeleteClick {
                    override fun deleteBanking(id: Int) {
                        val item = it.find { loan -> loan.id == id }
                        showRemovingDialog(item)
                    }
                }

                bmbLoansMenu.onBoomListener = object : OnBoomListenerAdapter() {
                    override fun onClicked(index: Int, boomButton: BoomButton) {
                        super.onClicked(index, boomButton)
                        when (index) {
                            0 -> showDialLoanTypeFilter(types)
                            1 -> currencies?.let {
                                getDialFilterByCur(currencies, true)
                            }
                            2 -> setSortLoanByAcc(isSortedAcc)
                            3 -> showRemovingDialog(allLoans = true)
                        }
                    }
                }
            }
        })

        //Deposits
        portViewModel.getDepList().observe(viewLifecycleOwner, Observer { it ->

            val depList = it.bankingList as List<Deposit>?
            val freq = it.freqList
            val cur = it.currencies
            val isAcc = it.isSortedAscending

            val textDepHeader = when {
                depList == null || depList.isEmpty() -> getString(R.string.noDepositFound)
                depList.size == 1 -> "1 ${getString(R.string.deposit)}"
                else -> depList.size.toString() + " " + getString(R.string.deposits)
            }

            tvDepHeader.text = textDepHeader
            depList?.let {

                adapterRecDep.list = it
                adapterRecDep.notifyDataSetChanged()

                adapterRecDep.onViewHolderClick = object : OnViewHolderClick {
                    override fun openBankingFragment(id: Int) {
                        val bundle = Bundle()
                        bundle.putInt(DEPOSIT_ID_KEY, id)
                        val depFragment = DepositFragment()
                        depFragment.arguments = bundle
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.add(R.id.FragmentContainerPort, depFragment)?.addToBackStack("")?.commit()
                    }
                }

                adapterRecDep.onHolderDeleteClick = object : OnHolderDeleteClick {
                    override fun deleteBanking(id: Int) {
                        val item = it.find { dep -> dep.id == id }
                        showRemovingDialog(item)
                    }
                }

                bmbDepMenu.onBoomListener = object : OnBoomListenerAdapter() {
                    override fun onClicked(index: Int, boomButton: BoomButton) {
                        super.onClicked(index, boomButton)
                        when (index) {
                            0 -> showDialDepFreqFilter(freq)
                            1 -> cur?.let { getDialFilterByCur(cur, false) }
                            2 -> setSortDepByAcc(isAcc)
                            3 -> showRemovingDialog(allLoans = false)
                        }
                    }
                }
            }
        })
    }


    //Loans
    @SuppressLint("InflateParams")
    private fun showDialLoanTypeFilter(types: List<LoanType>?) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_filter_type, null)
        dialogBuilder.setView(dialogView)

        dialogBuilder.setTitle(R.string.Filtering)
        dialogBuilder.setIcon(R.drawable.ic_filter)
        dialogBuilder.setMessage(R.string.selectTheCriteria)

        //Buttons
        val typeList = arrayListOf<LoanType>()

        fun Button.setChecked(checked: Boolean) {
            setViewChecked(checked)
            val type = getLoanTypeFromString(text.toString(), context)
            type?.let {
                when (checked) {
                    true -> if (!typeList.contains(it)) typeList.add(it)
                    false -> if (typeList.contains(it)) typeList.remove(it)
                }
            }
        }

        val onClickListener = View.OnClickListener {

            val curBut = it as Button
            curBut.setChecked(!curBut.isChecked())
        }

        val btnMort: Button = dialogView.findViewById(R.id.btnDialLoanTypeMort)
        val btnCar: Button = dialogView.findViewById(R.id.btnDialLoanTypeCar)
        val btnBus: Button = dialogView.findViewById(R.id.btnDialLoanTypeBus)
        val btnCons: Button = dialogView.findViewById(R.id.btnDialLoanTypeCons)
        val btnCrLines: Button = dialogView.findViewById(R.id.btnDialLoanTypeCrLines)
        val btnDepSec: Button = dialogView.findViewById(R.id.btnDialLoanTypeDepSec)
        val btnGoldSec: Button = dialogView.findViewById(R.id.btnDialLoanTypeGold)
        val btnStud: Button = dialogView.findViewById(R.id.btnDialLoanTypeStud)
        val btnUnsecured: Button = dialogView.findViewById(R.id.btnDialLoanTypeUnsecured)
        val btnSelAllOrClear: Button = dialogView.findViewById(R.id.btnDialSelectOrClear)

        btnMort.setOnClickListener(onClickListener)
        btnCar.setOnClickListener(onClickListener)
        btnBus.setOnClickListener(onClickListener)
        btnCons.setOnClickListener(onClickListener)
        btnCrLines.setOnClickListener(onClickListener)
        btnDepSec.setOnClickListener(onClickListener)
        btnGoldSec.setOnClickListener(onClickListener)
        btnStud.setOnClickListener(onClickListener)
        btnUnsecured.setOnClickListener(onClickListener)

        fun selectAllOrClear(isSelectAll: Boolean) {
            btnMort.setChecked(isSelectAll)
            btnCons.setChecked(isSelectAll)
            btnCar.setChecked(isSelectAll)
            btnStud.setChecked(isSelectAll)
            btnCrLines.setChecked(isSelectAll)
            btnUnsecured.setChecked(isSelectAll)
            btnDepSec.setChecked(isSelectAll)
            btnGoldSec.setChecked(isSelectAll)
            btnBus.setChecked(isSelectAll)
            btnSelAllOrClear.text =
                if (isSelectAll) requireContext().getString(R.string.CLEAR)
                else requireContext().getString(R.string.SELECT_ALL)
        }

        btnSelAllOrClear.setOnClickListener {
            val curButton = it as Button
            val text = curButton.text.toString()
            selectAllOrClear(text == requireContext().getString(R.string.SELECT_ALL))
        }

        //initialize
        when {
            types == null || types.size == 9 -> selectAllOrClear(true)
            types.isEmpty() -> selectAllOrClear(false)
            else -> {
                btnMort.setChecked(types.contains(LoanType.MORTGAGE))
                btnCar.setChecked(types.contains(LoanType.CAR_LOAN))
                btnStud.setChecked(types.contains(LoanType.STUDENT_LOAN))
                btnCrLines.setChecked(types.contains(LoanType.CREDIT_LINES))
                btnUnsecured.setChecked(types.contains(LoanType.UNSECURED))
                btnDepSec.setChecked(types.contains(LoanType.DEPOSIT_SECURED))
                btnGoldSec.setChecked(types.contains(LoanType.GOLD_PLEDGE_SECURED))
                btnBus.setChecked(types.contains(LoanType.BUSINESS))
            }
        }

        //click SAVE
        dialogBuilder.setPositiveButton(
            getString(R.string.save)
        ) { _, _ ->

            if (typeList.size < 9) tvLoanTypeFilter.visibility = View.VISIBLE
            else tvLoanTypeFilter.visibility = View.GONE
            portViewModel.setSelLoanTypeList(typeList)
        }
        //click CANCEL
        dialogBuilder.setNegativeButton(
            getString(R.string.cancel)
        ) { _, _ -> }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        alertDialog.setCustomView()
    }

    private fun setSortLoanByAcc(acc: Boolean?) {
        val asc = when (acc) {
            null, false -> {
                portViewModel.setSortByLoanRate(true)
                getString(R.string.sortedByInterestRate) + " " +
                        getString(R.string.ascending)
            }
            true -> {
                portViewModel.setSortByLoanRate(false)
                getString(R.string.sortedByInterestRate) + " " +
                        getString(R.string.descending)
            }
        }
        tvLoanSortFilter.text = asc
        tvLoanSortFilter.visibility = View.VISIBLE
    }

    //Deposit
    @SuppressLint("InflateParams")
    private fun showDialDepFreqFilter(freq: List<Frequency>?) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_filter_type, null)
        dialogBuilder.setView(dialogView)

        dialogBuilder.setTitle(R.string.Filtering)
        dialogBuilder.setIcon(R.drawable.ic_filter)
        dialogBuilder.setMessage(R.string.selectTheCriteria)

        //Buttons
        val freqList = arrayListOf<Frequency>()

        fun Button.setChecked(checked: Boolean) {
            setViewChecked(checked)
            val type = getFreqFromString(text.toString(), context)
            type?.let {
                when (checked) {
                    true -> if (!freqList.contains(it)) freqList.add(it)
                    false -> if (freqList.contains(it)) freqList.remove(it)
                }
            }
        }

        val onClickListener = View.OnClickListener {
            val curBut = it as Button
            curBut.setChecked(!curBut.isChecked())
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

        btnMonthly.text = requireContext().getString(R.string.MonthlyPaymentDep)
        btnQuart.text = requireContext().getString(R.string.QuarterlyPaymentDep)
        btnAtTheEnd.text = requireContext().getString(R.string.AtTheEndPayment)
        btnOther.text = requireContext().getString(R.string.OTHER)

        btnMonthly.setOnClickListener(onClickListener)
        btnQuart.setOnClickListener(onClickListener)
        btnAtTheEnd.setOnClickListener(onClickListener)
        btnOther.setOnClickListener(onClickListener)

        fun selectAllOrClear(isSelectAll: Boolean) {
            btnMonthly.setChecked(true)
            btnOther.setChecked(true)
            btnQuart.setChecked(true)
            btnAtTheEnd.setChecked(true)
            btnSelAllOrClear.text =
                if (isSelectAll) requireContext().getString(R.string.CLEAR)
                else requireContext().getString(R.string.SELECT_ALL)
        }

        btnSelAllOrClear.setOnClickListener {
            val curButton = it as Button
            val text = curButton.text.toString()
            selectAllOrClear(text == requireContext().getString(R.string.SELECT_ALL))
        }

        //initialize
        when {
            freq == null || freq.size == 4 -> selectAllOrClear(true)
            freq.isEmpty() -> selectAllOrClear(false)
            else -> {
                btnMonthly.setChecked(freq.contains(Frequency.MONTHLY))
                btnQuart.setChecked(freq.contains(Frequency.QUARTERLY))
                btnAtTheEnd.setChecked(freq.contains(Frequency.AT_THE_END))
                btnOther.setChecked(freq.contains(Frequency.OTHER))
            }
        }
        //click SAVE
        dialogBuilder.setPositiveButton(
            getString(R.string.save)
        ) { _, _ ->

            if (freqList.size < 4) tvDepTypeFilter.visibility = View.VISIBLE
            else tvDepTypeFilter.visibility = View.GONE
            portViewModel.setSelDepFreqList(freqList)
        }
        //click CANCEL
        dialogBuilder.setNegativeButton(
            getString(R.string.cancel)
        ) { _, _ -> }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        alertDialog.setCustomView()
    }

    private fun setSortDepByAcc(acc: Boolean?) {
        val asc = when (acc) {
            null, false -> {
                portViewModel.setSortByDepRate(true)
                getString(R.string.sortedByInterestRate) + " " +
                        getString(R.string.ascending)
            }
            true -> {
                portViewModel.setSortByDepRate(false)
                getString(R.string.sortedByInterestRate) + " " +
                        getString(R.string.descending)
            }
        }
        tvDepSortFilter.text = asc
        tvDepSortFilter.visibility = View.VISIBLE
    }

    //Common
    @SuppressLint("InflateParams")
    private fun getDialFilterByCur(curs: List<String>, loan: Boolean) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_filter_currency, null)
        dialogBuilder.setView(dialogView)

        val spinner: Spinner = dialogView.findViewById(R.id.spinDialFilCurr)
        spinner.adapter =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, curs)
        spinner.setSelection(0)

        dialogBuilder.setMessage(R.string.CurSpinnerText)
        dialogBuilder.setIcon(R.drawable.ic_filter)
        dialogBuilder.setTitle(R.string.Filtering)
        //click SAVE
        dialogBuilder.setPositiveButton(
            getString(R.string.save)
        ) { _, _ ->

            val selection = spinner.selectedItem.toString()
            val list = arrayListOf<String>()
            list.add(selection)

            if (loan) {
                portViewModel.setSelLoanCurList(list)
                tvLoanCurFilter.visibility = View.VISIBLE
            } else {
                portViewModel.setSelDepCurList(list)
                tvDepCurFilter.visibility = View.VISIBLE
            }
        }
        //click CANCEL
        dialogBuilder.setNegativeButton(
            getString(R.string.cancel)
        ) { _, _ -> }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        alertDialog.setCustomView()
    }

    private fun Button.setViewChecked(checked: Boolean) {
        if (checked) {
            background = context.getDrawable(R.drawable.btn_option_checked)
            setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.checked, 0)
            textSize = BUTTON_DIALOG_SIZE_PRESSED
            setTextColor(Color.WHITE)
        } else {
            background = context?.getDrawable(R.drawable.btn_expand)
            setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            textSize = BUTTON_DIALOG_SIZE_UNPRESSED
            setTextColor(Color.BLACK)
        }
    }

    private fun Button.isChecked(): Boolean = textSize == BUTTON_DIALOG_SIZE_PRESSED * 2

    private fun showRemovingDialog(item: Banking? = null, allLoans: Boolean? = null) {
        if (item == null && allLoans == null)
            return

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.warning)
        builder.setIcon(R.drawable.ic_alert)
        val alertText = when {
            item != null -> {
                val text =
                    if (item is Loan) getString(R.string.loan)
                    else getString(R.string.deposit)
                getString(R.string.AreYouSureRemove) + " " + text + "?"
            }
            allLoans != null -> {
                val text =
                    if (allLoans) getString(R.string.Loans)
                    else getString(R.string.Deposits)
                getString(R.string.AreYouSureRemoveAll) + " " + text + "?"
            }
            else -> TODO()
        }
        builder.setMessage(alertText)
        builder.setPositiveButton(R.string.OK)
        { _, _ ->
            when {
                item != null -> {
                    if (item is Loan) portViewModel.deleteLoan(item)
                    else if (item is Deposit) portViewModel.deleteDep(item)
                }
                allLoans != null -> {
                    if (allLoans) portViewModel.deleteAllLoans()
                    else portViewModel.deleteAllDep()
                }
                else -> TODO()
            }
            showSnackBar(R.string.SuccessfullyRemoved, scrollView)
        }
        builder.setNegativeButton(R.string.cancel)
        { _, _ -> }

        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.setCustomView()
    }

    override fun onStop() {
        super.onStop()
        portViewModel.removeSources()
    }

}
