package com.example.fincalc.ui.port.deps

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fincalc.R
import com.example.fincalc.data.db.dep.Deposit
import com.example.fincalc.models.deposit.Frequency
import com.example.fincalc.models.deposit.TableDep
import com.example.fincalc.ui.dep.AdapterRecViewDep
import java.text.DecimalFormat

class AdapterRecDepDetail(
    var depList: List<Deposit>, private val context: Context?
) :
    RecyclerView.Adapter<AdapterRecDepDetail.Holder>() {

    private val dec = DecimalFormat("#,###.#")

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvBank: TextView = itemView.findViewById(R.id.tvBankDepFr)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmountDepFr)
        val tvFreq: TextView = itemView.findViewById(R.id.tvFreqDepFr)
        val tvTerm: TextView = itemView.findViewById(R.id.tvTermDepFr)
        val tvRate: TextView = itemView.findViewById(R.id.tvRateDepFr)
        val tvCur: TextView = itemView.findViewById(R.id.tvCurrencyDepFr)
        val tvCapital: TextView = itemView.findViewById(R.id.tvCapitalizedDepFr)
        val tvTax: TextView = itemView.findViewById(R.id.tvTaxDepFr)
        val recSchedule: RecyclerView = itemView.findViewById(R.id.recScheduleDepFr)
        val tvIncome: TextView = itemView.findViewById(R.id.tvDepFrTotalPayment)
        val tvEffectRate: TextView = itemView.findViewById(R.id.tvDepFrEffRate)
        val ivDepFr: ImageView = itemView.findViewById(R.id.ivDepFr)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_deposits_detail, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = depList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {

        context?.let {

            val curDep = depList[position]
            val res = context.resources

            if (curDep.bank != "") {
                val bank = res.getString(R.string.bank) + ": ${curDep.bank}"
                holder.tvBank.text = bank
                holder.tvBank.visibility = View.VISIBLE
            } else
                holder.tvBank.visibility = View.GONE

            val sum = res.getString(R.string.Amount) + ": ${curDep.amount}"
            holder.tvAmount.text = sum

            val text = context.getString(curDep.frequency.id)
            val typeText = res.getString(R.string.LoanType) + ": $text"
            holder.tvFreq.text = typeText

            val term = res.getString(R.string.Term_months) + ": ${curDep.months}"
            holder.tvTerm.text = term

            val rate = res.getString(R.string.Interest_rate) + ": ${curDep.rate}%"
            holder.tvRate.text = rate

            val cur = res.getString(R.string.Currency) + ": ${curDep.currency}"
            holder.tvCur.text = cur

            val capitalize = curDep.capitalize

            Log.d("cappp", "capital: $capitalize")
            if (!capitalize)
                holder.tvCapital.visibility = View.GONE
            else {
                holder.tvCapital.text = res.getString(R.string.withCapitalizing)
                holder.tvCapital.visibility = View.VISIBLE
            }
            //Tax
            val tax = context.getString(R.string.Tax) + ": ${curDep.taxRate}"
            holder.tvTax.text = tax


            //Recycler

            holder.ivDepFr.setImageResource(
                when (curDep.frequency) {
                    Frequency.MONTHLY -> R.mipmap.type_car_loan
                    Frequency.QUARTERLY -> R.mipmap.type_quarterly
                    Frequency.AT_THE_END -> R.mipmap.type_at_the_end
                    Frequency.OTHER -> R.mipmap.deposit_logo
                }
            )

            val depTable = TableDep(curDep)

            val income = depTable.totalPayment - curDep.amount
            val incomeText = context.getString(R.string.Income) + ": ${dec.format(income)} ${curDep.currency}"
            holder.tvIncome.text = incomeText

            val effectRate = context.getString(R.string.EffectiveRate) + ": " +
                    dec.format(depTable.effectiveRate).toString() + "%"
            holder.tvEffectRate.text = effectRate

            val adapter = AdapterRecViewDep(depTable)
            holder.recSchedule.setHasFixedSize(true)
            holder.recSchedule.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            holder.recSchedule.adapter = adapter
        }
    }
}