package com.example.fincalc.ui

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.textclassifier.TextClassifier
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.fincalc.R

class AdapterSpinnerRates(
    context: Context,
    resource: Int,
    listCodes: Array<String>,
    listImages: Array<Int>,
    small:Boolean
) :
    ArrayAdapter<String>(context, resource, listCodes) {

    private val mCurrencyCodes = listCodes
    private val mContext = context
    private val mResource = resource
    private val mCurrencyFlags = listImages
    private val mSmall = small


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(mContext).inflate(mResource, parent, false)

        val textView = view.findViewById<TextView>(R.id.tvSpinnerCode)
        val imageView = view.findViewById<ImageView>(R.id.ivFlag)
        val curCode = mCurrencyCodes[position]
        val curFlag = mCurrencyFlags[position]
        textView.text = curCode
        imageView.setImageResource(curFlag)
        if(mSmall){
            textView.textSize = 20.0F
            textView.setTypeface(Typeface.DEFAULT);
            imageView.layoutParams.height = 50
            imageView.layoutParams.width = 50
        }

        return view
    }
}
