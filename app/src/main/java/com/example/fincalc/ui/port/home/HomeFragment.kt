package com.example.fincalc.ui.port.home


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.fincalc.R
import com.example.fincalc.data.db.dep.Deposit
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.ui.*
import com.example.fincalc.ui.dep.DepositActivity
import com.example.fincalc.ui.loan.LoanActivity
import com.example.fincalc.ui.port.OnViewHolderClick
import com.example.fincalc.ui.port.deps.DepositFragment
import com.example.fincalc.ui.port.filter.FilterQuery
import com.example.fincalc.ui.port.filter.SearchOption
import com.example.fincalc.ui.port.filter.SearchOption.*
import com.example.fincalc.ui.port.loans.LoansFragment
import com.nightonke.boommenu.BoomButtons.BoomButton
import com.nightonke.boommenu.OnBoomListenerAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlin.coroutines.CoroutineContext

const val LOAN_ID_KEY = "Loan Key"
const val DEPOSIT_ID_KEY = "Deposit Key"

@Suppress("UNCHECKED_CAST")
class HomeFragment : Fragment(), CoroutineScope {

    private lateinit var job: Job
    private lateinit var loansFilterViewModel: LoansFilterViewModel
    private lateinit var depFilterViewModel: DepFilterViewModel
    private lateinit var adapterRecLoan: AdapterRecBanking
    private lateinit var adapterRecDep: AdapterRecBanking
    private var delay: Boolean = false

    override val coroutineContext: CoroutineContext
        get() = Main + job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        job = Job()
        loansFilterViewModel = ViewModelProvider(this).get(LoansFilterViewModel::class.java)
        depFilterViewModel = ViewModelProvider(this).get(DepFilterViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvStatusPort.setFont(FONT_PATH)

        /*LOANS*/
        loansFilterViewModel.setSortPref(null)

        bmbLoansMenu.initialize(BMBTypes.LOAN)

        adapterRecLoan = AdapterRecBanking(arrayListOf(), null, loansFilterViewModel)
        // recLoanPort.initialize(adapterRecLoan)
        recLoanPort.setHasFixedSize(true)
        recLoanPort.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        recLoanPort.adapter = adapterRecLoan
        indicatorLoans.attachToRecyclerView(recLoanPort)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recLoanPort)

        fabAddLoan.setOnClickListener {
            val intent = Intent(requireActivity(), LoanActivity::class.java)
            startActivity(intent)
            Animatoo.animateSpin(requireActivity())
        }

        btnLoansFilter1.setOnClickListener {
            btnLoansFilter1.editOrRemoveFilter(requireContext(), loansFilterViewModel)
        }

        btnLoansFilter2.setOnClickListener {
            btnLoansFilter2.editOrRemoveFilter(requireContext(), loansFilterViewModel)
        }

        btnLoansFilter3.setOnClickListener {
            btnLoansFilter3.editOrRemoveFilter(requireContext(), loansFilterViewModel)
        }

        adapterRecLoan.onViewHolderClick = object : OnViewHolderClick {
            override fun openBankingFragment(id: Int) {
                val bundle = Bundle()
                bundle.putInt(LOAN_ID_KEY, id)
                val loanFragment = LoansFragment()
                loanFragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()
                    ?.add(R.id.FragmentContainerPort, loanFragment)?.addToBackStack("")
                    ?.commit()
            }
        }

        bmbLoansMenu.onBoomListener = object : OnBoomListenerAdapter() {
            override fun onClicked(index: Int, boomButton: BoomButton) {
                super.onClicked(index, boomButton)
                when (index) {
                    0 -> showDialogTypeFilter(requireContext(), loansFilterViewModel)
                    1 -> showDialogCurrencyFilter(requireContext(), loansFilterViewModel)
                    2 -> showDialogSort(requireContext(), loansFilterViewModel)
                    3 -> showDialogRemoveBanking(
                        bmbLoansMenu, loansFilterViewModel, allLoans = true
                    ) {
                        showSnackBar(R.string.SuccessfullyRemoved, bmbLoansMenu)
                    }
                }
            }
        }


        /*DEPOSIT*/
        depFilterViewModel.setSortPref(null)

        bmbDepMenu.initialize(BMBTypes.DEPOSIT)

        adapterRecDep = AdapterRecBanking(arrayListOf(), null, depFilterViewModel)
        //recDepPort.initialize(adapterRecDep)
        recDepPort.setHasFixedSize(true)
        recDepPort.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        recDepPort.adapter = adapterRecDep
        indicatorDep.attachToRecyclerView(recDepPort)
        val snapHelperDep = PagerSnapHelper()
        snapHelperDep.attachToRecyclerView(recDepPort)

        fabAddDep.setOnClickListener {
            val intent = Intent(requireActivity(), DepositActivity::class.java)
            startActivity(intent)
            Animatoo.animateSpin(requireActivity())
        }

        btnDepFilter1.setOnClickListener {
            btnDepFilter1.editOrRemoveFilter(requireContext(), depFilterViewModel)
        }

        btnDepFilter2.setOnClickListener {
            btnDepFilter2.editOrRemoveFilter(requireContext(), depFilterViewModel)
        }

        btnDepFilter3.setOnClickListener {
            btnDepFilter3.editOrRemoveFilter(requireContext(), depFilterViewModel)
        }

        adapterRecDep.onViewHolderClick = object : OnViewHolderClick {
            override fun openBankingFragment(id: Int) {
                val bundle = Bundle()
                bundle.putInt(DEPOSIT_ID_KEY, id)
                val depFragment = DepositFragment()
                depFragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()
                    ?.add(R.id.FragmentContainerPort, depFragment)?.addToBackStack("")
                    ?.commit()
            }
        }

        bmbDepMenu.onBoomListener = object : OnBoomListenerAdapter() {
            override fun onClicked(index: Int, boomButton: BoomButton) {
                super.onClicked(index, boomButton)
                when (index) {
                    0 -> showDialogTypeFilter(requireContext(), depFilterViewModel)
                    1 -> showDialogCurrencyFilter(requireContext(), depFilterViewModel)
                    2 -> showDialogSort(requireContext(), depFilterViewModel)
                    3 -> showDialogRemoveBanking(
                        bmbDepMenu, depFilterViewModel, allLoans = false
                    ) {
                        showSnackBar(R.string.SuccessfullyRemoved, bmbDepMenu)
                    }
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()

        //Loans
        loansFilterViewModel.getLoanList().observe(this, Observer { it ->

            progressBarHomeFr.visibility = View.VISIBLE

            val sortTextRes = when (it.sort) {
                null -> R.string.latest
                true -> R.string.highestRates
                false -> R.string.lowestRates
            }
            val loans = it.bankingList as List<Loan>?
            val queue = it.searchOptions
            launch {
                val delayTime: Long = if (!delay) {
                    delay = true
                    0
                } else 700

                delay(delayTime)

                progressBarHomeFr.visibility = View.GONE

                if (queue.size > 0)
                    btnLoansFilter1.setTextFromQuerySet(
                        requireContext(), queue.elementAt(0), sortTextRes, true
                    )
                else {
                    btnLoansFilter1.visibility = View.GONE
                    btnLoansFilter2.visibility = View.GONE
                    btnLoansFilter3.visibility = View.GONE
                }

                if (queue.size > 1)
                    btnLoansFilter2.setTextFromQuerySet(
                        requireContext(), queue.elementAt(1), sortTextRes, true
                    )
                else {
                    btnLoansFilter2.visibility = View.GONE
                    btnLoansFilter3.visibility = View.GONE
                }

                if (queue.size > 2)
                    btnLoansFilter3.setTextFromQuerySet(
                        requireContext(), queue.elementAt(2), sortTextRes, true
                    )
                else
                    btnLoansFilter3.visibility = View.GONE


                val textLoanHeader = when {
                    loans == null || loans.isEmpty() -> getString(R.string.noLoanFound)
                    loans.size == 1 -> "1 ${getString(R.string.loan)}"
                    else -> loans.size.toString() + " " + getString(R.string.Loans)
                }
                tvLoanHeader.text = textLoanHeader

                loans?.let {

                    adapterRecLoan.list = it
                    adapterRecLoan.notifyDataSetChanged()
                    recLoanPort.scrollToPosition(0)
                    recLoanPort.invalidate()
                }
            }
        })

        //Deposit
        depFilterViewModel.getDepList().observe(this, Observer { it ->

            progressBarHomeFr.visibility = View.VISIBLE

            val sortTextRes = when (it.sort) {
                null -> R.string.latest
                true -> R.string.highestRates
                false -> R.string.lowestRates
            }
            val depList = it.bankingList as List<Deposit>?
            val queue = it.searchOptions

            launch {
                val delayTime: Long = if (!delay) {
                    delay = true
                    0
                } else 5000
                delay(delayTime)

                progressBarHomeFr.visibility = View.GONE

                if (queue.size > 0)
                    btnDepFilter1.setTextFromQuerySet(
                        requireContext(), queue.elementAt(0), sortTextRes, false
                    )
                else {
                    btnDepFilter1.visibility = View.GONE
                    btnDepFilter2.visibility = View.GONE
                    btnDepFilter3.visibility = View.GONE
                }

                if (queue.size > 1)
                    btnDepFilter2.setTextFromQuerySet(
                        requireContext(), queue.elementAt(1), sortTextRes, false
                    )
                else {
                    btnDepFilter2.visibility = View.GONE
                    btnDepFilter3.visibility = View.GONE
                }

                if (queue.size > 2)
                    btnDepFilter3.setTextFromQuerySet(
                        requireContext(), queue.elementAt(2), sortTextRes, false
                    )
                else
                    btnDepFilter3.visibility = View.GONE

                val textDepHeader = when {
                    depList == null || depList.isEmpty() -> getString(R.string.noDepositFound)
                    depList.size == 1 -> "1 ${getString(R.string.deposit)}"
                    else -> depList.size.toString() + " " + getString(R.string.deposits)
                }
                tvDepHeader.text = textDepHeader

                depList?.let {
                    adapterRecDep.list = it
                    adapterRecDep.notifyDataSetChanged()
                    recDepPort.scrollToPosition(0)
                    recDepPort.invalidate()
                }
            }
        })
    }

    private fun Button.setTextFromQuerySet(
        c: Context,
        opt: SearchOption,
        sortTextRes: Int,
        isLoan: Boolean
    ) {
        visibility = View.VISIBLE
        text = when (opt) {
            FILTER_TYPE -> {
                setCustomSizeVector(
                    c,
                    resLeft = R.drawable.ic_filter, sizeLeftdp = 24,
                    resRight = R.drawable.ic_next, sizeRightdp = 24
                )
                if (isLoan) c.getString(R.string.filteredByType)
                else c.getString(R.string.filteredByFreq)

            }
            FILTER_CURRENCY -> {
                setCustomSizeVector(
                    c,
                    resLeft = R.drawable.ic_filter, sizeLeftdp = 24,
                    resRight = R.drawable.ic_next, sizeRightdp = 24
                )
                c.getString(R.string.filteredByCur)
            }
            SORT -> {
                setCustomSizeVector(
                    c,
                    resLeft = R.drawable.ic_sort, sizeLeftdp = 24,
                    resRight = R.drawable.ic_next, sizeRightdp = 24
                )
                c.getString(sortTextRes)
            }
        }
    }

    private fun Button.editOrRemoveFilter(context: Context, filterQuery: FilterQuery) {
        val option = when (text) {
            requireContext().getString(R.string.filteredByCur) -> FILTER_CURRENCY
            requireContext().getString(R.string.filteredByType) -> FILTER_TYPE
            requireContext().getString(R.string.filteredByFreq) -> FILTER_TYPE
            requireContext().getString(R.string.latest) -> SORT
            requireContext().getString(R.string.lowestRates) -> SORT
            requireContext().getString(R.string.highestRates) -> SORT
            else -> TODO()
        }
        showDialogRemoveOrEditFilter(context, filterQuery, option)
    }

    override fun onStop() {
        super.onStop()
        job.cancel()
        loansFilterViewModel.removeSources()
        depFilterViewModel.removeSources()

    }

}



