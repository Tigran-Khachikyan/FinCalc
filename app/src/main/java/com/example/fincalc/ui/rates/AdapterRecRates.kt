package com.example.fincalc.ui.rates

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fincalc.R
import com.example.fincalc.ui.decimalFormatter3p
import kotlin.math.absoluteValue

class AdapterRecRates(
    val context: Context,
    var ratesRows: List<RatesBar>?
) :
    RecyclerView.Adapter<AdapterRecRates.Holder>() {


    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvRateName)
        val tvNumber: TextView = itemView.findViewById(R.id.tvNumberRate)
        val tvCode: TextView = itemView.findViewById(R.id.tvRateCode)
        val ivIcon: ImageView = itemView.findViewById(R.id.ivRateIcon)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvGrowth: TextView = itemView.findViewById(R.id.tvGrowth)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.recycler_rates, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = ratesRows?.size ?: 0

    override fun onBindViewHolder(holder: Holder, position: Int) {
        ratesRows?.let {
            val curRatesRow = (ratesRows as List<RatesBar>)[position]
            val number = (position + 1).toString()
            holder.tvNumber.text = number
            holder.tvPrice.text = decimalFormatter3p.format(curRatesRow.price).replace(',', '.')
            holder.tvName.text = curRatesRow.name
            holder.tvCode.text = curRatesRow.code
            holder.ivIcon.setImageResource(curRatesRow.icon)
            curRatesRow.growth?.let {
                if (it == 0F) holder.tvGrowth.text = "-"
                else {
                    if (it > 0F)
                        holder.tvGrowth.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_arrow_drop_up_black_24dp, 0, 0, 0
                        )
                    else
                        holder.tvGrowth.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_arrow_drop_down_black_24dp, 0, 0, 0
                        )
                    val text =
                        decimalFormatter3p.format(it.absoluteValue).replace(',', '.') + "%"
                    holder.tvGrowth.text = text
                }
            }
        }
    }
}
