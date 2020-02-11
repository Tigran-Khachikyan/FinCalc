package com.example.fincalc.ui.port.loans

import android.os.Bundle
import android.util.Log
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
import kotlinx.android.synthetic.main.fragment_loans.*

/*val snapHelper = PagerSnapHelper()
snapHelper.attachToRecyclerView(recLoansPager)*/

class LoansFragment : Fragment() {

    private lateinit var loanViewModel: LoanViewModel
    private lateinit var adapter: AdapterRecScheduleLoan

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        loanViewModel = ViewModelProvider(this).get(LoanViewModel::class.java)
        return inflater.inflate(R.layout.fragment_loans, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AdapterRecScheduleLoan(null)
        recScheduleLoanFr.setHasFixedSize(true)
        recScheduleLoanFr.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recScheduleLoanFr.adapter = adapter

        val loanId = arguments?.getInt(LOAN_ID_KEY, 0)
        Log.d("sasass","IT: $loanId")

        loanId?.let {


            loanViewModel.getLoan(loanId)?.observe(viewLifecycleOwner, Observer { curLoan ->

                if (curLoan.bank != "") {
                    val bank = requireContext().getString(R.string.bank) + ": ${curLoan.bank}"
                    tvBankLoanFr.text = bank
                    tvBankLoanFr.visibility = View.VISIBLE
                } else tvBankLoanFr.visibility = View.GONE


                val sum = requireContext().getString(R.string.Amount) + ": ${curLoan.amount}"
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

                val cur = requireContext().getString(R.string.Currency) + ": ${curLoan.currency}"
                tvCurrencyLoanFr.text = cur
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

                ivLoansFr.setImageResource(
                    when (curLoan.type) {
                        LoanType.MORTGAGE -> R.mipmap.type_mortgage
                        LoanType.BUSINESS -> R.mipmap.type_business
                        LoanType.GOLD_PLEDGE_SECURED -> R.mipmap.type_gold_secured
                        LoanType.CAR_LOAN -> R.mipmap.type_car_loan
                        LoanType.DEPOSIT_SECURED -> R.mipmap.type_other_loan
                        LoanType.CONSUMER_LOAN -> R.mipmap.type_consumer
                        LoanType.STUDENT_LOAN -> R.mipmap.type_student
                        LoanType.UNSECURED -> R.mipmap.type_other_loan
                        LoanType.CREDIT_LINES -> R.mipmap.type_card_loans
                        LoanType.OTHER -> R.mipmap.type_other_loan
                    }
                )

                val loanTable = TableLoan(curLoan)
                val totalRes = loanTable.totalPayment + loanTable.oneTimeComAndCosts
                val total = requireContext().getString(R.string.TOTAL_PAYMENT) + ": " +
                        decimalFormatter1p.format(totalRes).toString() + " " + curLoan.currency
                tvLoanFrTotalPayment.text = total
                val realRate =
                    requireContext().getString(R.string.RealRate) + ": " + decimalFormatter1p.format(
                        loanTable.realRate
                    ).toString() + "%"
                tvLoanFrRealRate.text = realRate

                adapter.item = loanTable
                adapter.notifyDataSetChanged()
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

    override fun onStop() {
        super.onStop()
    }
}