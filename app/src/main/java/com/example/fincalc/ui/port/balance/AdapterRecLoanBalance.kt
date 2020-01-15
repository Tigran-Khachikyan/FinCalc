package com.example.fincalc.ui.port.balance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fincalc.R
import com.example.fincalc.data.db.Loan
import com.example.fincalc.data.db.LoanType
import com.example.fincalc.ui.port.OnViewHolderClick
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AdapterRecLoanBalance(
    var loanList: List<Loan>,
    _balanceViewModel: BalanceViewModel,
     var onViewHolderClick: OnViewHolderClick
) :
    RecyclerView.Adapter<AdapterRecLoanBalance.Holder>() {

    private val balanceViewModel = _balanceViewModel

    inner class Holder(itemView: View, private val mListener: OnViewHolderClick) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        val tv1: TextView = itemView.findViewById(R.id.tvRecBalance1)
        val tv2: TextView = itemView.findViewById(R.id.tvRecBalance2)
        val tv3: TextView = itemView.findViewById(R.id.tvRecBalance3)
        val iv: ImageView = itemView.findViewById(R.id.ivRecBalance)
        val fab: FloatingActionButton = itemView.findViewById(R.id.fabRecBalanceDelete)

        override fun onClick(p0: View?) {
            p0?.let {
                mListener.openLoan(position = adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerbalance, parent, false)
        return Holder(view, onViewHolderClick)
    }

    override fun getItemCount(): Int = loanList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.tv1.text = loanList[position].bank
        holder.tv2.text = loanList[position].type.name
        holder.tv3.text = loanList[position].queryLoan.sum.toString()

        holder.fab.setOnClickListener {
            balanceViewModel.deleteLoan(loanList[position])
        }

        holder.iv.setImageResource(
            when (loanList[position].type) {
                LoanType.MORTGAGE -> R.mipmap.loan_image
                LoanType.BUSINESS -> R.mipmap.exchange_logo
                LoanType.GOLD_PLEDGE_SECURED -> R.mipmap.loan_image
                LoanType.CAR_LOAN -> R.mipmap.loan_image
                LoanType.DEPOSIT_SECURED -> R.mipmap.deposit_logo
                LoanType.CONSUMER_LOAN -> R.mipmap.loan_image
                LoanType.STUDENT_LOAN -> R.mipmap.deposit_logo
                LoanType.UNSECURED -> R.mipmap.loan_image
                LoanType.CREDIT_LINES -> R.mipmap.portfolio_logo
                LoanType.NONE -> R.mipmap.loan_image
            }
        )
    }
}
