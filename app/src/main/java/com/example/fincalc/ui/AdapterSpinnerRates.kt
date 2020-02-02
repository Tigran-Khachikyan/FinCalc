package com.example.fincalc.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.fincalc.R

class AdapterSpinnerRates(
    context: Context,
    resource: Int,
    listCodes: Array<String>,
    listImages: Array<Int>
) :
    ArrayAdapter<String>(context, resource, listCodes) {

    private val mCurrencyCodes = listCodes
    private val mContext = context
    private val mResource = resource
    private val mCurrencyFlags = listImages


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, parent)
    }

    private fun createItemView(position: Int, parent: ViewGroup): View {
        val view = LayoutInflater.from(mContext).inflate(mResource, parent, false)

        val textView = view.findViewById<TextView>(R.id.tvSpinnerCode)
        val curCode = " " + mCurrencyCodes[position]
        val curFlag = mCurrencyFlags[position]
        textView.text = curCode
        textView.setCompoundDrawablesWithIntrinsicBounds(curFlag, 0, 0, 0)

        return view
    }
}
