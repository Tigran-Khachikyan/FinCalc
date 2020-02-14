package com.example.fincalc.ui.port.filter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.fincalc.R
import com.example.fincalc.models.credit.LoanType
import com.example.fincalc.models.deposit.Frequency
import com.example.fincalc.models.rates.mapRatesNameIcon
import com.example.fincalc.ui.BUTTON_DIALOG_SIZE_PRESSED
import com.example.fincalc.ui.BUTTON_DIALOG_SIZE_UNPRESSED
import com.example.fincalc.ui.setCustomSizeVector

@Suppress("UNCHECKED_CAST")
class AdapterRecyclerMultiChoice(
    val context: Context,
    private val possibleOptions: MutableSet<*>,
    var selOptions: MutableSet<*>
) : RecyclerView.Adapter<AdapterRecyclerMultiChoice.Holder>() {


    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val raw: Button = itemView.findViewById(R.id.btnMultiChoice)

        init {
            raw.setOnClickListener {
                val curButton = it as Button

                selOptions = when (val curPref = possibleOptions.elementAt(adapterPosition)) {
                    is LoanType -> {
                        curButton.changeSelection(
                            selOptions as MutableSet<LoanType>,
                            possibleOptions as MutableSet<LoanType>,
                            adapterPosition, null
                        )
                    }
                    is Frequency -> {
                        curButton.changeSelection(
                            selOptions as MutableSet<Frequency>,
                            possibleOptions as MutableSet<Frequency>,
                            adapterPosition, null
                        )
                    }
                    is String -> {
                        val flag = mapRatesNameIcon[curPref]?.second ?: 0
                        curButton.changeSelection(
                            selOptions as MutableSet<String>,
                            possibleOptions as MutableSet<String>,
                            adapterPosition, flag
                        )
                    }
                    else -> TODO()
                }
                // notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.test_multichoice_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = possibleOptions.size

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val text = when (val rawPos = possibleOptions.elementAt(position)) {
            is LoanType -> {
                holder.raw.setViewChecked((selOptions as MutableSet<LoanType>).contains(rawPos), null)
                context.getString(rawPos.id)
            }
            is Frequency -> {
                holder.raw.setViewChecked((selOptions as MutableSet<Frequency>).contains(rawPos), null)
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
            else -> TODO()
        }
        holder.raw.text = text
    }
}

private fun Button.setViewChecked(checked: Boolean, icon: Int?) {
    if (checked) {
        background = context.getDrawable(R.drawable.btn_option_checked)
        //  setCompoundDrawablesWithIntrinsicBounds(icon, 0, R.drawable.ic_check, 0)
        setCustomSizeVector(
            context,
            resLeft = icon,
            sizeLeftdp = 24,
            resRight = R.drawable.ic_check,
            sizeRightdp = 24
        )
        textSize = BUTTON_DIALOG_SIZE_PRESSED
        setTextColor(Color.WHITE)
    } else {
        background = context?.getDrawable(R.drawable.btn_expand)
        // setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
        setCustomSizeVector(context, resLeft = icon, sizeLeftdp = 24)

        textSize = BUTTON_DIALOG_SIZE_UNPRESSED
        setTextColor(Color.BLACK)
    }
}


private val Button.isChecked: Boolean
    get() = currentTextColor == Color.WHITE

/*private fun <T> Button.changeSelection(
    selOpt: ArrayList<T>,
    possibleOptions: List<T>,
    position: Int
): ArrayList<T> {

    Log.d("uuuuuuui","BOTTON.CHANGE() is triggered" )
    Log.d("uuuuuuui","BOTTON.CHECKED(): ${this.isChecked}" )
    Log.d("uuuuuuui","BOTTON.currentTextColor: ${this.currentTextColor}" )
    Log.d("uuuuuuui","currentTextColor == Color.WHITE : ${currentTextColor == Color.WHITE}" )

    if (isChecked) {
        selOpt.remove(possibleOptions[position])
        setViewChecked(false)
    }
    else {
        selOpt.add(possibleOptions[position])
        setViewChecked(true)
    }
    Log.d("uuuuuuui","BOTTON.CHANGE() return : ${selOpt.size}" )

    return selOpt
}*/

private fun <T> Button.changeSelection(
    selectedOptions: MutableSet<T>,
    possibleOptions: MutableSet<T>,
    position: Int,
    icon: Int?
): MutableSet<T> {


    if (isChecked) {
        selectedOptions.remove(possibleOptions.elementAt(position))
        setViewChecked(false, icon)
    } else {
        selectedOptions.add(possibleOptions.elementAt(position))
        setViewChecked(true, icon)
    }
    Log.d("uuuuurrib", "BOTTON.changeSelection() return : ${selectedOptions.size}")

    return selectedOptions
}


