package com.example.fincalc.ui.loan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fincalc.R
import com.example.fincalc.models.credit.TableLoan
import java.text.DecimalFormat



class AdapterRecScheduleLoan(var item: TableLoan?) : RecyclerView.Adapter<AdapterRecScheduleLoan.GenericViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            1 ->
                ScheduleListHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.recyclerloan,
                        parent,
                        false
                    )
                )
            0 ->
                ScheduleTotalHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.recyclerloantotal,
                        parent,
                        false
                    )
                )
            else -> CommissionAndCostHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.recyclercommissionandcosts,
                    parent,
                    false
                )
            )

        }

    override fun getItemCount() =
        if (item != null)
            (item as TableLoan).rowCount + 2 else 0


    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {

        if (item != null)
            holder.setDataOnView(position)
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> 2 // 2 is Commission row
            itemCount - 1 -> 0 // 0 is Total row
            else -> 1
        }
    }
    val dec = DecimalFormat("#,###.#")

    inner class ScheduleListHolder(itemView: View) : GenericViewHolder(itemView) {

        private val tvNumber = itemView.findViewById<TextView>(R.id.tvNumber)
        private val tvLoanSumRemain = itemView.findViewById<TextView>(R.id.tvLoanSumRemain)
        private val tvLoanSumMonthly = itemView.findViewById<TextView>(R.id.tvLoanSumMonthly)
        private val tvPercentMonthly = itemView.findViewById<TextView>(R.id.tvPercentMonthly)
        private val tvCommissionMonthly = itemView.findViewById<TextView>(R.id.tvCommissionMonthly)
        private val tvTotalPaymentMonthly =
            itemView.findViewById<TextView>(R.id.tvTotalPaymentMonthly)

        override fun setDataOnView(position: Int) {

            val items = item?.rows?.get(position-1)

            tvNumber.text = items?.curRowN.toString()
            tvLoanSumRemain.text = dec.format(items?.balance)
            tvLoanSumMonthly.text = dec.format(items?.monthLoan)
            tvPercentMonthly.text = dec.format(items?.percent)
            tvCommissionMonthly.text = dec.format(items?.monthCom)
            tvTotalPaymentMonthly.text = dec.format(items?.payment)
        }
    }


    inner class ScheduleTotalHolder(itemView: View) : GenericViewHolder(itemView) {

        private val tvTotalSum = itemView.findViewById<TextView>(R.id.tvTotalSum)
        private val tvTotalPercent = itemView.findViewById<TextView>(R.id.tvTotalPercent)
        private val tvTotalCommission = itemView.findViewById<TextView>(R.id.tvTotalCommission)
        private val tvTotalPayment = itemView.findViewById<TextView>(R.id.tvTotalPayment)

        override fun setDataOnView(position: Int) {

            tvTotalSum.text = dec.format(item?.sumBasic)
            tvTotalPercent.text = dec.format(item?.totalPercent)
            tvTotalCommission.text = dec.format(item?.totalComDuring)
            tvTotalPayment.text = dec.format(item?.totalPayment)
        }
    }

    inner class CommissionAndCostHolder(itemView: View) : GenericViewHolder(itemView) {

        private val tvOneTimeCommissionAndCosts =
            itemView.findViewById<TextView>(R.id.tvOneTimeCommissionAndCosts)

        override fun setDataOnView(position: Int) {

            tvOneTimeCommissionAndCosts.text = dec.format(item?.oneTimeComAndCosts)
        }
    }
    abstract class GenericViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        abstract fun setDataOnView(position: Int)
    }
}
