package com.example.fincalc.ui.port.loans

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fincalc.R
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.models.credit.LoanType
import com.example.fincalc.models.credit.TableLoan
import com.example.fincalc.ui.loan.AdapterRecScheduleLoan
import java.text.DecimalFormat

class AdapterRecLoansDetail(
    var loanList: List<Loan>, private val context: Context?
) :
    RecyclerView.Adapter<AdapterRecLoansDetail.Holder>() {

    private val dec = DecimalFormat("#,###.#")

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
        val tvTotalPayment: TextView = itemView.findViewById(R.id.tvLoanFrTotalPayment)
        val tvLoanFrRealRate: TextView = itemView.findViewById(R.id.tvLoanFrRealRate)
        val ivLoansFr: ImageView = itemView.findViewById(R.id.ivLoansFr)
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

            if (curLoan.bank != "") {
                val bank = res.getString(R.string.bank) + ": ${curLoan.bank}"
                holder.tvBank.text = bank
                holder.tvBank.visibility = View.VISIBLE
            } else {
                holder.tvBank.visibility = View.GONE
            }

            val sum = res.getString(R.string.Amount) + ": ${curLoan.amount}"
            holder.tvAmount.text = sum

            if (curLoan.type == LoanType.OTHER)
                holder.tvType.visibility = View.GONE
            else {
                val text = context.getString(curLoan.type.id)
                val typeText = res.getString(R.string.LoanType) + ": $text"
                holder.tvType.text = typeText
                holder.tvType.visibility = View.VISIBLE
            }

            val term = res.getString(R.string.Term_months) + ": ${curLoan.months}"
            holder.tvTerm.text = term

            val rate = res.getString(R.string.Interest_rate) + ": ${curLoan.rate}%"
            holder.tvRate.text = rate

            val cur = res.getString(R.string.Currency) + ": ${curLoan.currency}"
            holder.tvCur.text = cur

            //OneTimeCom

            val oneTimeComRes = getComResult(
                curLoan.oneTimeComSum, curLoan.oneTimeComRate,
                curLoan.minOneTimeComSumOrRate, true
            )

            if (oneTimeComRes == "")
                holder.tvOneTimeCom.visibility = View.GONE
            else {
                val oneTCom = res.getString(R.string.One_Time_Commission) + ": $oneTimeComRes"
                holder.tvOneTimeCom.text = oneTCom
                holder.tvOneTimeCom.visibility = View.VISIBLE
            }

            //MonthlyTimeCom
            val monthlyComRes: String = getComResult(
                curLoan.monthComSum, curLoan.monthComRate,
                curLoan.minMonthComSumOrRate, false
            )
            if (monthlyComRes == "")
                holder.tvMonthlyCom.visibility = View.GONE
            else {
                val monthlyCom = res.getString(R.string.Monthly_Commission) + ": $monthlyComRes"
                holder.tvMonthlyCom.text = monthlyCom
                holder.tvMonthlyCom.visibility = View.VISIBLE
            }

            //AnnualCom
            val annualComRes = getComResult(
                curLoan.annComSum, curLoan.annComRate,
                curLoan.minAnnComSumOrRate, false
            )

            if (annualComRes == "")
                holder.tvAnnualCom.visibility = View.GONE
            else {
                val annualCom = res.getString(R.string.Annual_Commission) + ": $annualComRes"
                holder.tvAnnualCom.text = annualCom
                holder.tvAnnualCom.visibility = View.VISIBLE
            }

            if (curLoan.otherCosts == 0)
                holder.tvCosts.visibility = View.GONE
            else {
                val cost = res.getString(R.string.Other_One_Time_Costs) +
                        ": ${curLoan.otherCosts}"
                holder.tvCosts.text = cost
                holder.tvCosts.visibility = View.VISIBLE
            }

            //Recycler

            holder.ivLoansFr.setImageResource(
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
            val total = context.getString(R.string.TOTAL_PAYMENT) + ": " +
                    dec.format(totalRes).toString() + " " + curLoan.currency
            holder.tvTotalPayment.text = total
            val realRate =
                context.getString(R.string.RealRate) + ": " + dec.format(loanTable.realRate).toString() + "%"
            holder.tvLoanFrRealRate.text = realRate

            val adapter = AdapterRecScheduleLoan(item = loanTable)
            holder.recSchedule.setHasFixedSize(true)
            holder.recSchedule.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            holder.recSchedule.adapter = adapter
        }
    }

    private fun getComResult(sum: Int, rate: Float, check: Boolean, oneTCom: Boolean): String {

        context?.let {
            val butMin = context.getString(R.string.But_Min)
            val ofTheSum =
                if (oneTCom) context.getString(R.string.ofTheAmount)
                else context.getString(R.string.ofTheBalance)

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
}