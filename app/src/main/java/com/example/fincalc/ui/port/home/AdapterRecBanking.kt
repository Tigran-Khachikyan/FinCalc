package com.example.fincalc.ui.port.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.fincalc.R
import com.example.fincalc.data.db.dep.Deposit
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.models.Banking
import com.example.fincalc.models.credit.LoanType
import com.example.fincalc.models.deposit.Frequency
import com.example.fincalc.ui.decimalFormatter1p

@Suppress("DEPRECATION", "UNUSED_VARIABLE")
class AdapterRecBanking(
    val context: Context,
    var list: List<Banking>,
    var onViewHolderClick: OnViewHolderClick?,
    var port: BaseViewModel
) :
    RecyclerView.Adapter<AdapterRecBanking.Holder>() {

    inner class Holder(
        itemView: View,
        private val onHolderClick: OnViewHolderClick?,
        var port: BaseViewModel
    ) : RecyclerView.ViewHolder(itemView) {

        val tvSum: TextView = itemView.findViewById(R.id.tvRecBalanceSum)
        val tvTerm: TextView = itemView.findViewById(R.id.tvRecBalanceTerm)
        val tvBank: TextView = itemView.findViewById(R.id.tvRecBalanceBank)
        val tvDate: TextView = itemView.findViewById(R.id.tvRecBalanceDate)
        val tvRate: TextView = itemView.findViewById(R.id.tvRecBalanceRate)
        val iv: ImageView = itemView.findViewById(R.id.ivRecBalance)
        val layBackData: ConstraintLayout = itemView.findViewById(R.id.layBackData)

        init {
            itemView.setOnClickListener {
                val bankingId = list[adapterPosition].id
                onHolderClick?.openBankingFragment(bankingId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_balance, parent, false)
        return Holder(view, onViewHolderClick, port)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val bank = list[position].bank
        if (bank == "") holder.tvBank.visibility = View.GONE
        else {
            holder.tvBank.visibility = View.VISIBLE
            val bankText = context.getString(R.string.bank) + ": " + list[position].bank
            holder.tvBank.text = bankText
        }
        val cur = list[position].currency
        val amount = decimalFormatter1p.format(list[position].amount) + " $cur"
        holder.tvSum.text = amount
        val term = list[position].months.toString() + " " + context.getString(R.string.Term_months)
        holder.tvTerm.text = term
        val rate = list[position].rate.toString() + "%"
        holder.tvRate.text = rate
        holder.tvDate.text = list[position].date

        if (list[position] is Loan) {

            val typeEnum = (list[position] as Loan).type
            holder.iv.setImageResource(
                when ((list[position] as Loan).type) {
                    LoanType.MORTGAGE -> R.drawable.ic_mortgage
                    LoanType.BUSINESS -> R.drawable.ic_business_loan
                    LoanType.GOLD_PLEDGE_SECURED -> R.drawable.ic_precious_metals
                    LoanType.CAR_LOAN -> R.drawable.ic_car_loan
                    LoanType.DEPOSIT_SECURED -> R.drawable.ic_loan_other
                    LoanType.CONSUMER_LOAN -> R.drawable.ic_consumer
                    LoanType.STUDENT_LOAN -> R.drawable.ic_student_loan
                    LoanType.UNSECURED -> R.drawable.ic_loan_other
                    LoanType.CREDIT_LINES -> R.drawable.ic_credit_lines
                    LoanType.OTHER -> R.drawable.ic_loan_other
                }
            )
            holder.layBackData.setBackgroundResource(R.color.PortPrimaryDark)
        } else if (list[position] is Deposit) {

            val freqEnum = (list[position] as Deposit).frequency
            holder.iv.setImageResource(
                when ((list[position] as Deposit).frequency) {
                    Frequency.MONTHLY -> R.drawable.ic_monthly
                    Frequency.QUARTERLY -> R.drawable.ic_quarterly
                    else -> R.drawable.ic_contract
                }
            )
            holder.layBackData.setBackgroundResource(R.color.DepPrimaryDark)
        }
    }
}
