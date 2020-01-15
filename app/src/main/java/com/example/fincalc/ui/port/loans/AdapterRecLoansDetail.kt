package com.example.fincalc.ui.port.loans

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fincalc.R
import com.example.fincalc.data.db.Loan
import com.example.fincalc.data.db.LoanType
import com.example.fincalc.models.loan.CalculatorLoan.getSchedule
import com.example.fincalc.ui.loan.AdapterRecScheduleLoan

class AdapterRecLoansDetail(
    var loanList: List<Loan>, val context: Context?
) :
    RecyclerView.Adapter<AdapterRecLoansDetail.Holder>() {

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvBank: TextView = itemView.findViewById(R.id.tvBankLoanFr)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmountLoanFr)
        val tvType: TextView = itemView.findViewById(R.id.tvTypeLoanFr)
        val tvTerm: TextView = itemView.findViewById(R.id.tvTermLoanFr)
        val tvRate: TextView = itemView.findViewById(R.id.tvRateLoanFr)
        val tvCur: TextView = itemView.findViewById(R.id.tvCurrencyLoanFr)
        val tvOneTimeCom: TextView = itemView.findViewById(R.id.tvOneTimeComLoanFr)
        val tvMonthlyCom: TextView = itemView.findViewById(R.id.tvMonthlyComLoanFr)
        val tvAnnualCom: TextView = itemView.findViewById(R.id.tvAnnualComLoanFr)
        val tvCosts: TextView = itemView.findViewById(R.id.tvCostsLoanFr)
        val recSchedule: RecyclerView = itemView.findViewById(R.id.recScheduleLoanFr)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_loans_detail, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = loanList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {

        context?.let {

            val curLoan = loanList[position]
            val res = context.resources

            val bank = res.getString(R.string.bank) + ": ${curLoan.bank}"
            holder.tvBank.text = bank

            val sum = res.getString(R.string.Amount) + ": ${curLoan.queryLoan.sum}"
            holder.tvAmount.text = sum

            if (curLoan.type == LoanType.NONE)
                holder.tvType.visibility = View.GONE
            else {
                val type = res.getString(R.string.LoanType) + ": ${curLoan.type}"
                holder.tvType.text = type
            }

            val term = res.getString(R.string.Term_months) + ": ${curLoan.queryLoan.months}"
            holder.tvTerm.text = term

            val rate = res.getString(R.string.Interest_rate) + ": ${curLoan.queryLoan.rate}"
            holder.tvRate.text = rate

            val cur = res.getString(R.string.Currency) + ": ${curLoan.currency}"
            holder.tvCur.text = cur

            //OneTimeCom

            val oneTcomRes = getComResult(
                curLoan.queryLoan.oneTimeCommissionSum, curLoan.queryLoan.oneTimeCommissionRate,
                curLoan.queryLoan.minOneTimeCommissionSumOrRate, true
            )

            if (oneTcomRes == "")
                holder.tvOneTimeCom.visibility = View.GONE
            else {
                val oneTCom = res.getString(R.string.One_Time_Commission) + ": $oneTcomRes"
                holder.tvOneTimeCom.text = oneTCom
            }

            //MonthlyTimeCom
            val monthlyComRes: String = getComResult(
                curLoan.queryLoan.monthlyCommissionSum, curLoan.queryLoan.monthlyCommissionRate,
                curLoan.queryLoan.minMonthlyCommissionSumOrRate, false
            )
            if (monthlyComRes == "")
                holder.tvMonthlyCom.visibility = View.GONE
            else {
                val monthlyCom = res.getString(R.string.Monthly_Commission) + ": $monthlyComRes"
                holder.tvMonthlyCom.text = monthlyCom
            }

            //AnnualCom
            val annualComRes = getComResult(
                curLoan.queryLoan.annualCommissionSum, curLoan.queryLoan.annualCommissionRate,
                curLoan.queryLoan.minAnnualCommissionSumOrRate, false
            )
            if (annualComRes == "")
                holder.tvAnnualCom.visibility = View.GONE
            else {
                val annualCom = res.getString(R.string.Annual_Commission) + ": $annualComRes"
                holder.tvAnnualCom.text = annualCom
            }

            if (curLoan.queryLoan.oneTimeOtherCosts == 0.0)
                holder.tvCosts.visibility = View.GONE
            else {
                val cost = res.getString(R.string.Other_One_Time_Costs) +
                        ": ${curLoan.queryLoan.oneTimeOtherCosts}"
                holder.tvCosts.text = cost
            }

            //Recycler

            val schedule = getSchedule(curLoan.queryLoan, curLoan.repayment_program)
            val adapter = AdapterRecScheduleLoan(item = schedule)
            holder.recSchedule.setHasFixedSize(true)
            holder.recSchedule.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            holder.recSchedule.adapter = adapter
        }
    }

    private fun getComResult(sum: Double, rate: Float, check: Boolean, oneTCom: Boolean): String {

        context?.let {
            val butMin = context.getString(R.string.But_Min)
            val ofTheSum =
                if (oneTCom) context.getString(R.string.ofTheAmount)
                else context.getString(R.string.ofTheBalance)

            return when {
                check && sum != 0.0 && rate != 0F -> "$rate % $ofTheSum, $butMin $sum"
                !check && sum != 0.0 && rate != 0F -> "$rate % $ofTheSum, $sum"
                sum != 0.0 && rate == 0F -> "$$sum"
                sum == 0.0 && rate != 0F -> "$$rate $ofTheSum"
                else -> ""
            }
        }
        return ""
    }


}