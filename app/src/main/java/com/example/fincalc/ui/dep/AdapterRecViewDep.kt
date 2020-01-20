package com.example.fincalc.ui.dep

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fincalc.R
import com.example.fincalc.models.deposit.TableDep
import java.text.DecimalFormat

class AdapterRecViewDep(var scheduleDep: TableDep?) :
    RecyclerView.Adapter<AdapterRecViewDep.GenericViewHolderDep>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolderDep {
        return when (viewType) {
            0 -> RowViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerdep, parent, false)
            )
            else -> TotalViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerdeptotal, parent, false)
            )
        }
    }

    override fun getItemCount(): Int =
        if (scheduleDep != null) (scheduleDep as TableDep).rows.size + 1 else 0

    override fun onBindViewHolder(holder: GenericViewHolderDep, position: Int) {
        holder.setDataOnView(position)
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            itemCount - 1 -> 1
            else -> 0
        }
    }

    val dec = DecimalFormat("#,###.#")

    inner class RowViewHolder(itemView: View) : GenericViewHolderDep(itemView) {
        private val tvNumber: TextView = itemView.findViewById(R.id.tvNumberDep)
        private val tvBalance: TextView = itemView.findViewById(R.id.tvDepBalance)
        private val tvPercent: TextView = itemView.findViewById(R.id.tvDepPercent)
        private val tvPerAfterTax: TextView = itemView.findViewById(R.id.tvPercentAfterTax)
        private val tvPayment: TextView = itemView.findViewById(R.id.tvDepPayment)

        override fun setDataOnView(position: Int) {
            val rowList = scheduleDep?.rows
            tvNumber.text = dec.format(rowList?.get(position)?.curRowN)
            tvBalance.text = dec.format(rowList?.get(position)?.balance)
            tvPercent.text = dec.format(rowList?.get(position)?.percent)
            tvPerAfterTax.text = dec.format(rowList?.get(position)?.percAfterTax)
            tvPayment.text = dec.format(rowList?.get(position)?.payment)
        }
    }

    inner class TotalViewHolder(itemView: View) : GenericViewHolderDep(itemView) {
        private val tvTotalPercentDep: TextView = itemView.findViewById(R.id.tvTotalPercentDep)
        private val tvTotalPercentAfterTax: TextView =
            itemView.findViewById(R.id.tvTotalPerAfterTax)
        private val tvTotalPayment: TextView = itemView.findViewById(R.id.tvTotalPaymentDep)

        override fun setDataOnView(position: Int) {
            tvTotalPercentDep.text = dec.format(scheduleDep?.totalPercent)
            tvTotalPercentAfterTax.text = dec.format(scheduleDep?.totalPerAfterTax)
            tvTotalPayment.text = dec.format(scheduleDep?.totalPayment)
        }
    }


    abstract class GenericViewHolderDep(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun setDataOnView(position: Int)
    }
}