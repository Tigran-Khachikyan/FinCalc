package com.my_1st.fincalc.ui.port.filter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.my_1st.fincalc.R
import com.my_1st.fincalc.models.credit.LoanType
import com.my_1st.fincalc.models.deposit.Frequency
import com.my_1st.fincalc.models.rates.mapRatesNameIcon
import com.my_1st.fincalc.ui.isChecked
import com.my_1st.fincalc.ui.setViewChecked

@Suppress("UNCHECKED_CAST")
class AdapterRecyclerMultiChoice(
    val context: Context, private val possibleOptions: MutableSet<*>, var selOptions: MutableSet<*>
) : RecyclerView.Adapter<AdapterRecyclerMultiChoice.Holder>() {

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rawButton: Button = itemView.findViewById(R.id.btnMultiChoice)

        init {
            rawButton.setOnClickListener {
                selOptions = when (val curPref = possibleOptions.elementAt(adapterPosition)) {
                    is LoanType -> {
                        rawButton.changeSelection(
                            selOptions as MutableSet<LoanType>,
                            possibleOptions as MutableSet<LoanType>,
                            adapterPosition, null
                        )
                    }
                    is Frequency -> {
                        rawButton.changeSelection(
                            selOptions as MutableSet<Frequency>,
                            possibleOptions as MutableSet<Frequency>,
                            adapterPosition, null
                        )
                    }
                    is String -> {
                        val flag = mapRatesNameIcon[curPref]?.second ?: 0
                        rawButton.changeSelection(
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
                holder.rawButton.setViewChecked(
                    (selOptions as MutableSet<LoanType>).contains(rawPos), null
                )
                context.getString(rawPos.id)
            }
            is Frequency -> {
                holder.rawButton.setViewChecked(
                    (selOptions as MutableSet<Frequency>).contains(rawPos), null
                )
                context.getString(rawPos.id)
            }
            is String -> {
                holder.rawButton.text = rawPos
                val nameRes = mapRatesNameIcon[rawPos]?.first
                val iconRes = mapRatesNameIcon[rawPos]?.second
                holder.rawButton.setViewChecked(
                    (selOptions as MutableSet<String>).contains(rawPos), iconRes
                )
                rawPos + " (${nameRes?.let { context.getString(it) }})"
            }
            else -> ""
        }
        holder.rawButton.text = text
    }

    private fun <T> Button.changeSelection(
        selectedOptions: MutableSet<T>, possibleOptions: MutableSet<T>, position: Int, icon: Int?
    ): MutableSet<T> {
        if (isChecked) selectedOptions.remove(possibleOptions.elementAt(position))
        else selectedOptions.add(possibleOptions.elementAt(position))
        setViewChecked(!isChecked, icon)
        return selectedOptions
    }
}






