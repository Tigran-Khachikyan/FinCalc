package com.example.fincalc.ui.port.loans

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fincalc.R
import com.example.fincalc.models.credit.LoanType
import com.example.fincalc.models.credit.TableLoan
import com.example.fincalc.models.rates.mapRatesNameIcon
import com.example.fincalc.ui.decimalFormatter1p
import com.example.fincalc.ui.decimalFormatter2p
import com.example.fincalc.ui.loan.AdapterRecScheduleLoan
import com.example.fincalc.ui.port.home.LOAN_ID_KEY
import com.example.fincalc.ui.port.home.LoansFilterViewModel
import com.example.fincalc.ui.showDialogRemoveBanking
import kotlinx.android.synthetic.main.fragment_loans.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LoansFragment : Fragment(), CoroutineScope {

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Main + job
    private lateinit var loanViewModel: LoanViewModel
    private lateinit var loansFilterViewModel: LoansFilterViewModel
    private lateinit var adapter: AdapterRecScheduleLoan

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        job = Job()
        loanViewModel = ViewModelProvider(this).get(LoanViewModel::class.java)
        loansFilterViewModel = ViewModelProvider(this).get(LoansFilterViewModel::class.java)
        return inflater.inflate(R.layout.fragment_loans, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AdapterRecScheduleLoan(null)
        recScheduleLoanFr.setHasFixedSize(true)
        recScheduleLoanFr.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recScheduleLoanFr.adapter = adapter

        val loanId = arguments?.getInt(LOAN_ID_KEY)

        fabRemoveLoansFr.setOnClickListener {
            loanId?.let {
                showDialogRemoveBanking(fabRemoveLoansFr, loansFilterViewModel, true, id = it) {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }

        loanId?.let {
            loanViewModel.getLoan(loanId)?.observe(viewLifecycleOwner, Observer { curLoan ->

                if (curLoan.bank != "") {
                    val bank = requireContext().getString(R.string.bank) + ": ${curLoan.bank}"
                    tvBankLoanFr.text = bank
                    tvBankLoanFr.visibility = View.VISIBLE
                } else tvBankLoanFr.visibility = View.GONE


                val sum = requireContext().getString(R.string.Amount) +
                        ": ${decimalFormatter1p.format(curLoan.amount)} ${curLoan.currency}"
                tvAmountLoanFr.text = sum

                if (curLoan.type == LoanType.OTHER)
                    tvTypeLoanFr.visibility = View.GONE
                else {
                    val text = requireContext().getString(curLoan.type.id)
                    val typeText = requireContext().getString(R.string.LoanType) + ": $text"
                    tvTypeLoanFr.text = typeText
                    tvTypeLoanFr.visibility = View.VISIBLE
                }

                val term = requireContext().getString(R.string.Term_months) + ": ${curLoan.months}"
                tvTermLoanFr.text = term

                val rate = requireContext().getString(R.string.Interest_rate) + ": ${curLoan.rate}%"
                tvRateLoanFr.text = rate

                val flag = mapRatesNameIcon[curLoan.currency]?.second
                flag?.let { ivCurrencyLoanFr.setImageResource(flag) }

                //OneTimeCom
                val oneTimeComRes = getComResult(
                    curLoan.oneTimeComSum, curLoan.oneTimeComRate,
                    curLoan.minOneTimeComSumOrRate, true
                )

                if (oneTimeComRes == "")
                    tvOneTimeComLoanFr.visibility = View.GONE
                else {
                    val oneTCom =
                        requireContext().getString(R.string.One_Time_Commission) + ": $oneTimeComRes"
                    tvOneTimeComLoanFr.text = oneTCom
                    tvOneTimeComLoanFr.visibility = View.VISIBLE
                }

                //MonthlyTimeCom
                val monthlyComRes: String = getComResult(
                    curLoan.monthComSum, curLoan.monthComRate,
                    curLoan.minMonthComSumOrRate, false
                )
                if (monthlyComRes == "")
                    tvMonthlyComLoanFr.visibility = View.GONE
                else {
                    val monthlyCom =
                        requireContext().getString(R.string.Monthly_Commission) + ": $monthlyComRes"
                    tvMonthlyComLoanFr.text = monthlyCom
                    tvMonthlyComLoanFr.visibility = View.VISIBLE
                }

                //AnnualCom
                val annualComRes = getComResult(
                    curLoan.annComSum, curLoan.annComRate,
                    curLoan.minAnnComSumOrRate, false
                )

                if (annualComRes == "")
                    tvAnnualComLoanFr.visibility = View.GONE
                else {
                    val annualCom =
                        requireContext().getString(R.string.Annual_Commission) + ": $annualComRes"
                    tvAnnualComLoanFr.text = annualCom
                    tvAnnualComLoanFr.visibility = View.VISIBLE
                }

                if (curLoan.otherCosts == 0)
                    tvCostsLoanFr.visibility = View.GONE
                else {
                    val cost = requireContext().getString(R.string.Other_One_Time_Costs) +
                            ": ${curLoan.otherCosts}"
                    tvCostsLoanFr.text = cost
                    tvCostsLoanFr.visibility = View.VISIBLE
                }

                val loanTable = TableLoan(curLoan)
                tvLoanFrTotalPayment.text =
                    decimalFormatter1p.format(loanTable.totalPayment + loanTable.oneTimeComAndCosts)
                val realRate = decimalFormatter2p.format(loanTable.realRate).replace(',', '.') + "%"
                tvLoanFrRealRate.text = realRate

                adapter.item = loanTable
                adapter.notifyDataSetChanged()

                launch {
                    delay(500)
                    progressBarLoansFrag.visibility = View.GONE
                    appBarLayoutLoansFrag.visibility = View.VISIBLE
                    layLoansResultFrag.visibility = View.VISIBLE
                }
            })
        }
    }


    private fun getComResult(sum: Int, rate: Float, check: Boolean, oneTCom: Boolean): String {

        context?.let {
            val butMin = requireContext().getString(R.string.But_Min)
            val ofTheSum =
                if (oneTCom) requireContext().getString(R.string.ofTheAmount)
                else requireContext().getString(R.string.ofTheBalance)

            return when {
                check && sum != 0 && rate != 0F -> "$rate% $ofTheSum, $butMin $sum"
                !check && sum != 0 && rate != 0F -> "$rate% $ofTheSum, $sum"
                sum != 0 && rate == 0F -> "$sum"
                sum == 0 && rate != 0F -> "$rate% $ofTheSum"
                else -> ""
            }
        }
        return ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
    }

}