package com.example.fincalc.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.fincalc.R
import com.example.fincalc.models.rates.mapRatesNameIcon

class AdapterSpinnerRates(
    context: Context,
    resource: Int,
    listCodes: Array<String>
) :
    ArrayAdapter<String>(context, resource, listCodes) {

    private val mCurrencyCodes = listCodes
    private val mContext = context
    private val mResource = resource

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, parent)
    }

    private fun createItemView(position: Int, parent: ViewGroup): View {
        val view = LayoutInflater.from(mContext).inflate(mResource, parent, false)

        val laySpinner: ConstraintLayout = view.findViewById(R.id.laySpinner)
        val tvRateCode: TextView = view.findViewById(R.id.tvRateCode)
        val tvRateName: TextView = view.findViewById(R.id.tvRateName)
        val ivRateIcon: ImageView = view.findViewById(R.id.ivRateIcon)
        val curCode = mCurrencyCodes[position]
        val curFlag = mapRatesNameIcon[curCode]?.second
        val curNameRes = mapRatesNameIcon[curCode]?.first

        tvRateCode.text = curCode
        curNameRes?.let {
            tvRateName.text = context.getString(curNameRes)
        }
        curFlag?.let {
            ivRateIcon.setImageResource(curFlag)
        }
        return view
    }
}
