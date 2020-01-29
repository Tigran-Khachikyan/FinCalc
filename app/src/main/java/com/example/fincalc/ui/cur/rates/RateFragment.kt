package com.example.fincalc.ui.cur.rates

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.fincalc.R
import com.example.fincalc.data.network.api_rates.Rates
import com.example.fincalc.data.network.firebase.RatesUi
import com.example.fincalc.models.cur_met.currencyCodeList
import com.example.fincalc.models.cur_met.currencyFlagList
import com.example.fincalc.models.cur_met.getMapCurRates
import com.example.fincalc.ui.AdapterSpinnerRates
import com.example.fincalc.ui.customizeAlertDialog
import com.example.fincalc.ui.decimalFormatter2p
import com.example.fincalc.ui.decimalFormatter3p
import kotlinx.android.synthetic.main.activity_loan.*
import kotlinx.android.synthetic.main.fragment_rate.*


private const val PRIVATE_MODE = 0
private const val PREF_NAME = "Currency_Pref"
private const val CURRENCY_PREF = "Selected-currency"

class RateFragment : Fragment() {


    private lateinit var ratesViewModel: RatesViewModel
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        ratesViewModel = ViewModelProvider(this).get(RatesViewModel::class.java)
        return inflater.inflate(R.layout.fragment_rate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = context!!.getSharedPreferences(PREF_NAME, PRIVATE_MODE)

        val selectedCur = sharedPref.getString(CURRENCY_PREF, "USD")
        selectedCur?.let {
            ratesViewModel.setLatTableCur(selectedCur)
        }

        ratesViewModel.getLatTableRates().observe(viewLifecycleOwner, Observer {

            btnResetBaseCurrency.setOnClickListener {
                getDialog(context)
            }
            it?.let {
                setTableCurrencies(it.base)
                tvTableDateTime.text = it.ratesUi.dateTime?.toString()
                setTableRates(it.base, it.ratesUi.latRates)
                setTableGrowth(it.base, it.ratesUi)
            }

        })
    }


    @SuppressLint("InflateParams")
    private fun getDialog(context: Context?) {
        if (context != null) {
            val dialogBuilder = AlertDialog.Builder(context)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_save, null)
            dialogBuilder.setView(dialogView)

            dialogView.findViewById<EditText>(R.id.etDialBank).visibility = View.GONE
            dialogView.findViewById<Spinner>(R.id.spinnerDialLoanType).visibility = View.GONE
            dialogView.findViewById<TextView>(R.id.tvDialLoanType).visibility = View.GONE
            dialogView.findViewById<TextView>(R.id.tvDialBank).visibility = View.GONE

            //spinner Currency
            val spinnerCur = dialogView.findViewById<Spinner>(R.id.spinDialCurrency)
            val adapterSpinCur = AdapterSpinnerRates(
                context, R.layout.layoutspinner,
                currencyCodeList, currencyFlagList, true
            )
            adapterSpinCur.setDropDownViewResource(R.layout.layoutspinner)
            spinnerCur.adapter = adapterSpinCur
            spinnerCur.setSelection(adapterSpinCur.count - 4)

            dialogBuilder.setTitle(R.string.DialogTitleCur)
            dialogBuilder.setIcon(R.mipmap.currencyicon)

            //click SAVE
            dialogBuilder.setPositiveButton(
                getString(R.string.OK)
            ) { _, _ ->
                val selectedCur = spinnerCur.selectedItem.toString()
                val editor = sharedPref.edit()
                editor.putString(CURRENCY_PREF, selectedCur)
                editor.apply()
                ratesViewModel.setLatTableCur(selectedCur)
            }

            dialogBuilder.setNegativeButton(
                getString(R.string.cancel)
            ) { _, _ -> }

            val alertDialog = dialogBuilder.create()
            alertDialog.show()
            customizeAlertDialog(alertDialog, true)
            alertDialog.window?.setBackgroundDrawableResource(R.color.CurrencyPrimary)
        }
    }

    private fun setTableCurrencies(cur: String) {
        val textUSD = "USD / $cur"
        tvCurUSD.text = textUSD
        val textEUR = "EUR / $cur"
        tvCurEUR.text = textEUR
        val textGBP = "GBP / $cur"
        tvCurGBP.text = textGBP
        val textCNY = "CNY / $cur"
        tvCurCNY.text = textCNY
        val textRUR = "RUR / $cur"
        tvCurRUB.text = textRUR
    }

    private fun setTableRates(cur: String, rates: Rates?) {
        rates?.let {
            val map = getMapCurRates(rates)
            val selCurValue = map?.get(cur)
            selCurValue?.let {
                tvRateUSD.text = getRateValuesString("USD", selCurValue, map)
                tvRateEUR.text = getRateValuesString("EUR", selCurValue, map)
                tvRateGBP.text = getRateValuesString("GBP", selCurValue, map)
                tvRateCNY.text = getRateValuesString("CNY", selCurValue, map)
                tvRateRUB.text = getRateValuesString("RUB", selCurValue, map)
            }
        }
    }

    private fun setTableGrowth(cur: String, ratesUi: RatesUi) {
        val latestRates = ratesUi.latRates
        val elderRates = ratesUi.elderRates
        latestRates?.let {
            val mapLatest = getMapCurRates(latestRates)
            val selCurValue1 = mapLatest?.get(cur)
            selCurValue1?.let {

                val latRateUSD = getRateValuesDouble("USD", selCurValue1, mapLatest)
                val latRateEUR = getRateValuesDouble("EUR", selCurValue1, mapLatest)
                val latRateGBP = getRateValuesDouble("GBP", selCurValue1, mapLatest)
                val latRateCNY = getRateValuesDouble("CNY", selCurValue1, mapLatest)
                val latRateRUB = getRateValuesDouble("RUB", selCurValue1, mapLatest)

                if (elderRates != null) {
                    val mapElder = getMapCurRates(elderRates)
                    val selCurValue2 = mapElder?.get(cur)
                    selCurValue2?.let {

                        val oldRateUSD = getRateValuesDouble("USD", selCurValue2, mapElder)
                        val oldRateEUR = getRateValuesDouble("EUR", selCurValue2, mapElder)
                        val oldRateGBP = getRateValuesDouble("GBP", selCurValue2, mapElder)
                        val oldRateCNY = getRateValuesDouble("CNY", selCurValue2, mapElder)
                        val oldRateRUB = getRateValuesDouble("RUB", selCurValue2, mapElder)

                        getGrowthView(tvGrowthUSD, getGrowthCoef(latRateUSD, oldRateUSD))
                        getGrowthView(tvGrowthEUR, getGrowthCoef(latRateEUR, oldRateEUR))
                        getGrowthView(tvGrowthGBP, getGrowthCoef(latRateGBP, oldRateGBP))
                        getGrowthView(tvGrowthCNY, getGrowthCoef(latRateCNY, oldRateCNY))
                        getGrowthView(tvGrowthRUB, getGrowthCoef(latRateRUB, oldRateRUB))
                    }
                } else {
                    getGrowthView(tvGrowthUSD, null)
                    getGrowthView(tvGrowthEUR, null)
                    getGrowthView(tvGrowthGBP, null)
                    getGrowthView(tvGrowthCNY, null)
                    getGrowthView(tvGrowthRUB, null)
                }
            }
        }
    }


    private fun getRateValuesString(
        mainCur: String, selCurVal: Double, map: HashMap<String, Double>
    ): String? {

        val value = getRateValuesDouble(mainCur, selCurVal, map)
        return if (value != null)
            decimalFormatter3p.format(value).replace(',', '.')
        else null
    }

    private fun getRateValuesDouble(
        mainCur: String, selCurVal: Double, map: HashMap<String, Double>
    ): Double? {

        val value = map[mainCur]
        return if (value != null && value != 0.0) selCurVal / value else null
    }

    private fun getGrowthCoef(latValue: Double?, oldValue: Double?): Double? {
        return if (latValue != null && oldValue != null) {
            val dif = latValue - oldValue
            100 * dif / oldValue
        } else null
    }

    private fun getGrowthView(tv: TextView, coef: Double?) {
        coef?.let {

            val text = decimalFormatter3p.format(coef).replace(',', '.') + "%"

            Log.d("hhhu", "text: $text")
            if (coef == 0.0) tv.visibility = View.INVISIBLE
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
        } ?: tv.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_question, 0, 0, 0
        )
    }

}