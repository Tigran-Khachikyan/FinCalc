package com.my_1st.fincalc.ui.loan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.my_1st.fincalc.R

class AdapterSpinnerTypes(
    context: Context, resource: Int, types: Array<String>
) :
    ArrayAdapter<String>(context, resource, types) {

    private val mTypes = types
    private val mContext = context
    private val mResource = resource

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
        createItemView(position, parent)


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View =
        createItemView(position, parent)


    private fun createItemView(position: Int, parent: ViewGroup): View {
        val view = LayoutInflater.from(mContext).inflate(mResource, parent, false)
        val tvType: TextView = view.findViewById(R.id.tvSpinnerLoanType)
        tvType.text = mTypes[position]
        return view
    }
}
