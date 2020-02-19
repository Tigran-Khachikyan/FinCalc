package com.example.fincalc.ui.port.home

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
import com.example.fincalc.ui.port.OnViewHolderClick
import com.example.fincalc.ui.showDialogRemoveBanking

@Suppress("DEPRECATION", "UNUSED_VARIABLE")
class AdapterRecBanking(
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
        if (bank == "")
            holder.tvBank.visibility = View.GONE
        else {
            holder.tvBank.visibility = View.VISIBLE
            holder.tvBank.text = list[position].bank
        }
        val cur = list[position].currency
        val amount = decimalFormatter1p.format(list[position].amount) + " $cur"
        holder.tvSum.text = amount
        val rate = list[position].rate.toString() + "%"
        holder.tvRate.text = rate
        holder.tvDate.text = list[position].date

        if (list[position] is Loan) {

            val typeEnum = (list[position] as Loan).type
            holder.iv.setImageResource(
                when ((list[position] as Loan).type) {
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
            holder.layBackData.setBackgroundResource(R.color.PortPrimaryLight)
        } else if (list[position] is Deposit) {

            val freqEnum = (list[position] as Deposit).frequency
            holder.iv.setImageResource(
                when ((list[position] as Deposit).frequency) {
                    Frequency.MONTHLY -> R.mipmap.type_monthly
                    Frequency.QUARTERLY -> R.mipmap.type_quarter
                   else -> R.mipmap.type_at_the_end
                }
            )
            holder.layBackData.setBackgroundResource(R.color.DepPrimaryLight)
        }
    }
}
