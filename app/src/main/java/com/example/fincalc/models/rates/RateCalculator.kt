package com.example.fincalc.models.rates

import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.fincalc.R
import com.example.fincalc.ui.decimalFormatter3p
import java.util.HashMap
import kotlin.math.absoluteValue

fun getRateValuesString(
    mainCur: String, selCurVal: Double, map: HashMap<String, Double>
): String? {

    val value = getRateValuesDouble(mainCur, selCurVal, map)
    return if (value != null)
        decimalFormatter3p.format(value).replace(',', '.')
    else null
}

fun getRateValuesDouble(
    mainCur: String, selCurVal: Double, map: HashMap<String, Double>
): Double? {

    val value = map[mainCur]
    return if (value != null && value != 0.0) selCurVal / value else null
}

fun getGrowthRate(latValue: Double?, oldValue: Double?): Float? {
    return if (latValue != null && oldValue != null) {
        val dif = latValue - oldValue
        val res = (100 * dif / oldValue).toFloat()
        Log.d("yyyyyyy"," res INSIDE: $res")

        when {
            res == 0F || res.absoluteValue < 0.0001 -> 0F
            res.absoluteValue > 0.0001 && res.absoluteValue < 0.001 -> if (res > 0.0) 0.001F else -0.001F
            else -> res
        }
    } else null
}

fun getGrowthView(tv: TextView, coef: Float?) {
    coef?.let {

        var text: String = decimalFormatter3p.format(coef).replace(',', '.') + "%"
        if (text[0] == '-')
            text = text.subSequence(1, text.length).toString()
        Log.d("hhhu", "text: $text")
        if (coef == 0F) tv.visibility = View.INVISIBLE
        else {
            tv.visibility = View.VISIBLE
            tv.text = text
            if (coef > 0.0) tv.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_arrow_drop_up_black_24dp, 0, 0, 0
            )
            else tv.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_arrow_drop_down_black_24dp, 0, 0, 0
            )
        }
    }
}