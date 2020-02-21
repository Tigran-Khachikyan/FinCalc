package com.example.fincalc.ui.port.filter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.fincalc.R
import com.example.fincalc.models.credit.LoanType
import com.example.fincalc.models.deposit.Frequency
import com.example.fincalc.models.rates.mapRatesNameIcon
import com.example.fincalc.ui.isChecked
import com.example.fincalc.ui.setViewChecked

@Suppress("UNCHECKED_CAST")
class AdapterRecyclerMultiChoice(
    val context: Context, private val possibleOptions: MutableSet<*>, var selOptions: MutableSet<*>
) : RecyclerView.Adapter<AdapterRecyclerMultiChoice.Holder>() {

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val raw: Button = itemView.findViewById(R.id.btnMultiChoice)

        init {
            raw.setOnClickListener {
                selOptions = when (val curPref = possibleOptions.elementAt(adapterPosition)) {
                    is LoanType -> {
                        raw.changeSelection(
                            selOptions as MutableSet<LoanType>,
                            possibleOptions as MutableSet<LoanType>,
                            adapterPosition, null
                        )
                    }
                    is Frequency -> {
                        raw.changeSelection(
                            selOptions as MutableSet<Frequency>,
                            possibleOptions as MutableSet<Frequency>,
                            adapterPosition, null
                        )
                    }
                    is String -> {
                        val flag = mapRatesNameIcon[curPref]?.second ?: 0
                        raw.changeSelection(
                            selOptions as MutableSet<String>,
                            possibleOptions as MutableSet<String>,
                            adapterPosition, flag
                        )
                    }
                    else -> TODO()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.multichoice_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = possibleOptions.size

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val text = when (val rawPos = possibleOptions.elementAt(position)) {
            is LoanType -> {
                holder.raw.setViewChecked(
                    (selOptions as MutableSet<LoanType>).contains(rawPos), null
                )
                context.getString(rawPos.id)
            }
            is Frequency -> {
                holder.raw.setViewChecked(
                    (selOptions as MutableSet<Frequency>).contains(rawPos), null
                )
                context.getString(rawPos.id)
            }
            is String -> {
                holder.raw.text = rawPos
                val nameRes = mapRatesNameIcon[rawPos]?.first
                val iconRes = mapRatesNameIcon[rawPos]?.second
                holder.raw.setViewChecked(
                    (selOptions as MutableSet<String>).contains(rawPos), iconRes
                )
                rawPos + " (${nameRes?.let { context.getString(it) }})"
            }
            else -> ""
        }
        holder.raw.text = text
    }

    private fun <T> Button.changeSelection(
        selectedOptions: MutableSet<T>, possibleOptions: MutableSet<T>, position: Int, icon: Int?
    ): MutableSet<T> {

        if (isChecked) {
            selectedOptions.remove(possibleOptions.elementAt(position))
            setViewChecked(false, icon)
        } else {
            selectedOptions.add(possibleOptions.elementAt(position))
            setViewChecked(true, icon)
        }
        return selectedOptions
    }
}






