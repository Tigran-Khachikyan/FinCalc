package com.example.fincalc.ui.port.balance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fincalc.R
import com.example.fincalc.data.db.dep.Deposit
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.models.Banking
import com.example.fincalc.models.credit.LoanType
import com.example.fincalc.models.deposit.Frequency
import com.example.fincalc.ui.port.OnViewHolderClick
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AdapterRecBalance(
    var list: List<Banking>,
    _balanceViewModel: BalanceViewModel,
    var onViewHolderClick: OnViewHolderClick?
) :
    RecyclerView.Adapter<AdapterRecBalance.Holder>() {

    private val balanceViewModel = _balanceViewModel

    inner class Holder(itemView: View, private val mListener: OnViewHolderClick?) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        val tv1: TextView = itemView.findViewById(R.id.tvRecBalance1)
        val tv2: TextView = itemView.findViewById(R.id.tvRecBalance2)
        val tv3: TextView = itemView.findViewById(R.id.tvRecBalance3)
        val tv4: TextView = itemView.findViewById(R.id.tvRecBalance4)
        val iv: ImageView = itemView.findViewById(R.id.ivRecBalance)
        val fab: FloatingActionButton = itemView.findViewById(R.id.fabRecBalanceDelete)

        override fun onClick(p0: View?) {
            p0?.let {
                val loanId = list[adapterPosition].id
                mListener?.openBankProdById(loanId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerbalance, parent, false)
        return Holder(view, onViewHolderClick)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.tv1.text = list[position].bank
        holder.tv3.text = list[position].amount.toString()
        holder.tv4.text = list[position].rate.toString()

        if (list[position] is Loan) {
            holder.tv2.text = (list[position] as Loan).type.name
            holder.fab.setOnClickListener {
                balanceViewModel.deleteLoan(list[position] as Loan)
            }
            holder.iv.setImageResource(
                when ((list[position] as Loan).type) {
                    LoanType.MORTGAGE -> R.mipmap.type_mortgage
                    LoanType.BUSINESS -> R.mipmap.type_business
                    LoanType.GOLD_PLEDGE_SECURED -> R.mipmap.type_monthly
                    LoanType.CAR_LOAN -> R.mipmap.type_monthly
                    LoanType.DEPOSIT_SECURED -> R.mipmap.loan_logo
                    LoanType.CONSUMER_LOAN -> R.mipmap.loan_logo
                    LoanType.STUDENT_LOAN -> R.mipmap.type_monthly
                    LoanType.UNSECURED -> R.mipmap.loan_logo
                    LoanType.CREDIT_LINES -> R.mipmap.type_card_loans
                    LoanType.OTHER -> R.mipmap.loan_logo
                }
            )
        } else if (list[position] is Deposit) {
            holder.tv2.text = (list[position] as Deposit).frequency.name
            holder.fab.setOnClickListener {
                balanceViewModel.deleteDep(list[position] as Deposit)
            }
            holder.iv.setImageResource(
                when ((list[position] as Deposit).frequency) {
                    Frequency.MONTHLY -> R.mipmap.type_monthly
                    Frequency.QUARTERLY -> R.mipmap.type_quarterly
                    Frequency.AT_THE_END -> R.mipmap.type_at_the_end
                    Frequency.OTHER -> R.mipmap.deposit_logo
                }
            )
        }
    }
}
