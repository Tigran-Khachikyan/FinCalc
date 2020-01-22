package com.example.fincalc.ui.port.balance

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.fincalc.R
import com.example.fincalc.data.db.dep.Deposit
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.models.Banking
import com.example.fincalc.models.credit.LoanType
import com.example.fincalc.models.deposit.Frequency
import com.example.fincalc.ui.customizeAlertDialog
import com.example.fincalc.ui.port.OnViewHolderClick
import com.google.android.material.floatingactionbutton.FloatingActionButton

@Suppress("DEPRECATION", "UNUSED_VARIABLE")
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

        val bank = list[position].bank
        if (bank == "")
            holder.tv1.visibility = View.GONE
        else {
            holder.tv1.visibility = View.VISIBLE
            holder.tv1.text = list[position].bank
        }
        val cur = list[position].currency
        val amount = list[position].amount.toString() + " $cur"
        holder.tv3.text = amount
        val rate = list[position].rate.toString() + "%"
        holder.tv4.text = rate

        if (list[position] is Loan) {

            val typeEnum = (list[position] as Loan).type
            val typeString = holder.tv2.context.getString(typeEnum.id)
            holder.tv2.text = typeString
            holder.fab.setOnClickListener {
                getDialRemoveWarning(it.context, list[position])
            }
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
        } else if (list[position] is Deposit) {

            val freqEnum = (list[position] as Deposit).frequency
            val freqString = holder.tv2.context.getString(freqEnum.id)
            holder.tv2.text = freqString
            holder.fab.setOnClickListener {
                getDialRemoveWarning(it.context, list[position])
            }
            holder.iv.setImageResource(
                when ((list[position] as Deposit).frequency) {
                    Frequency.MONTHLY -> R.mipmap.type_monthly
                    Frequency.QUARTERLY -> R.mipmap.type_quarter
                    Frequency.AT_THE_END -> R.mipmap.type_at_the_end
                    Frequency.OTHER -> R.mipmap.deposit_logo
                }
            )
        }
    }

    private fun getDialRemoveWarning(context: Context?, prod: Banking) {
        if (context != null) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.warning)
            builder.setIcon(R.drawable.ic_alert)
            builder.setMessage(R.string.AreYouSureRemove)
            builder.setPositiveButton(R.string.OK) { _, _ ->
                if (prod is Loan)
                    balanceViewModel.deleteLoan(prod)
                else if (prod is Deposit)
                    balanceViewModel.deleteDep(prod)
            }
            builder.setNegativeButton(R.string.cancel) { _, _ ->
            }
            val alertDialog = builder.create()
            alertDialog.show()
            customizeAlertDialog(alertDialog, false)
        }
    }
}
