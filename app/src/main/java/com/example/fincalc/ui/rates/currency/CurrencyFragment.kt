package com.example.fincalc.ui.rates.currency

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.fincalc.R
import com.example.fincalc.data.network.api_rates.CurRates
import com.example.fincalc.data.network.firebase.RatesFull
import com.example.fincalc.models.rates.currencyMapFlags
import com.example.fincalc.models.rates.getMapFromCurRates
import com.example.fincalc.ui.*
import kotlinx.android.synthetic.main.fragment_rate.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue


private const val PRIVATE_MODE = 0
private const val PREF_NAME = "Currency_Pref"
private const val CURRENCY_PREF = "Selected-currency"
private const val FORMAT = "dd MMM yyyy HH:mm:ss"
private const val CURRENCY_FROM = "Currency from"
private const val CURRENCY_TO = "Currency To"
private const val CURRENCY_TABLE = "Currency Table"


@Suppress("DEPRECATION")
class RateFragment : Fragment() {

    private lateinit var ratesViewModel: CurrencyViewModel
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        ratesViewModel = ViewModelProvider(this).get(CurrencyViewModel::class.java)
        return inflater.inflate(R.layout.fragment_rate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = context!!.getSharedPreferences(PREF_NAME, PRIVATE_MODE)

        //initializing Table part (Calendar)
        val selectedCur = sharedPref.getString(CURRENCY_PREF, "USD")
        selectedCur?.let { ratesViewModel.setLatTableCur(selectedCur) }

        //initializing convert part (Calendar)
        ivTransferCur.setSvgColor(context!!, R.color.CurrencyPrimaryLight)
        selectBtnSpinner("EUR", CURRENCY_FROM)
        selectBtnSpinner("USD", CURRENCY_TO)
        ratesViewModel.setCurrencies("EUR", "USD")
        etAmountInput.setText("1.0")
        ratesViewModel.setDate(null)

    }

    override fun onStart() {
        super.onStart()

        ratesViewModel.getLatTableRates().observe(viewLifecycleOwner, Observer {

            btnResetBaseCurrency.setOnClickListener {
                getDialog(context, CURRENCY_TABLE)
            }
            it?.let {
                setTableCurrencies(it.baseCurrency)
                tvTableDateTime.text = getDate(it.rates.dateTime)
                setTableRates(it.baseCurrency, it.rates.latRates as CurRates?)
                setTableGrowth(it.baseCurrency, it.rates)
            }
        })

        ratesViewModel.getConvertRates().observe(viewLifecycleOwner, Observer {

            btnCalendarDate.setOnClickListener {
                openCalendar(context)
            }

            btnCalendarNow.setOnClickListener {
                clickLatest()
            }

            btnSpinner1.setOnClickListener {
                getDialog(context, CURRENCY_FROM)
            }

            btnSpinner2.setOnClickListener {
                getDialog(context, CURRENCY_TO)
            }

            btnConvertCur.setOnClickListener {

                hideKeyboard(this.activity!!)
                val amount: Double? = if (etAmountInput.text.toString() == "") null else
                    etAmountInput.text.toString().toDouble()
                ratesViewModel.setAmount(amount)
            }

            ivTransferCur.setOnClickListener {
                val spin1Text = btnSpinner1.text.toString()
                val spin2Text = btnSpinner2.text.toString()
                selectBtnSpinner(spin1Text, CURRENCY_TO)
                selectBtnSpinner(spin2Text, CURRENCY_FROM)
                ratesViewModel.setCurrencies(spin2Text, spin1Text)
            }

            val result = it?.let {
                " = ${decimalFormatter2p.format(it.resultAmount)} ${btnSpinner2.text}"
            } ?: ""
            tvCurResult.text = result

        })

    }


    @SuppressLint("InflateParams")
    private fun getDialog(context: Context?, cur: String) {
        if (context != null) {
            val dialogBuilder = AlertDialog.Builder(context)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_filter_currency, null)
            dialogBuilder.setView(dialogView)

            val curList = currencyMapFlags.keys.toTypedArray()
            val flagList = currencyMapFlags.values.toTypedArray()

            //spinner Currency
            val spinnerCur = dialogView.findViewById<Spinner>(R.id.spinDialFilCurr)
            val adapterSpinCur = AdapterSpinnerRates(
                context, R.layout.layoutspinner,
                curList, flagList
            )
            adapterSpinCur.setDropDownViewResource(R.layout.layoutspinner)
            spinnerCur.adapter = adapterSpinCur
            spinnerCur.setHasTransientState(true)

            dialogBuilder.setTitle(R.string.DialogTitleCur)
            dialogBuilder.setIcon(R.mipmap.currencyicon)

            //click SAVE
            dialogBuilder.setPositiveButton(
                getString(R.string.OK)
            ) { _, _ ->
                val selectedCur = spinnerCur.selectedItem.toString()
                when (cur) {
                    CURRENCY_TABLE -> {
                        val editor = sharedPref.edit()
                        editor.putString(CURRENCY_PREF, selectedCur)
                        editor.apply()
                        ratesViewModel.setLatTableCur(selectedCur)
                    }
                    CURRENCY_FROM -> {
                        val curTo = btnSpinner2.text.toString()
                        selectBtnSpinner(selectedCur, CURRENCY_FROM)
                        ratesViewModel.setCurrencies(selectedCur, curTo)
                    }
                    CURRENCY_TO -> {
                        val curFrom = btnSpinner1.text.toString()
                        btnSpinner2.text = selectedCur
                        selectBtnSpinner(selectedCur, CURRENCY_TO)
                        ratesViewModel.setCurrencies(curFrom, selectedCur)
                    }
                }
            }

            dialogBuilder.setNegativeButton(
                getString(R.string.cancel)
            ) { _, _ -> }

            val alertDialog = dialogBuilder.create()
            alertDialog.show()
            customizeAlertDialog(alertDialog, true)
            alertDialog.window?.setBackgroundDrawableResource(R.color.CurrencyPrimaryLight)
        }
    }

    private fun setTableCurrencies(cur: String) {
        val textUSD = "USD / $cur"
        tvGold.text = textUSD
        val textEUR = "EUR / $cur"
        tvSilver.text = textEUR
        val textGBP = "GBP / $cur"
        tvPlatinum.text = textGBP
        val textCNY = "CNY / $cur"
        tvPalladium.text = textCNY
        val textRUR = "RUR / $cur"
        tvCurRUB.text = textRUR
    }

    private fun setTableRates(cur: String, rates: CurRates?) {
        rates?.let {
            val map = getMapFromCurRates(rates)
            val selCurValue = map?.get(cur)
            selCurValue?.let {
                tvGoldRate.text = getRateValuesString("USD", selCurValue, map)
                tvSilverRate.text = getRateValuesString("EUR", selCurValue, map)
                tvPlatRate.text = getRateValuesString("GBP", selCurValue, map)
                tvPalladRate.text = getRateValuesString("CNY", selCurValue, map)
                tvRateRUB.text = getRateValuesString("RUB", selCurValue, map)
            }
        }
    }

    private fun setTableGrowth(cur: String, ratesUi: RatesFull) {
        val latestRates = ratesUi.latRates
        val elderRates = ratesUi.elderRates
        latestRates?.let {
            val mapLatest = getMapFromCurRates(latestRates as CurRates)
            val selCurValue1 = mapLatest?.get(cur)
            selCurValue1?.let {

                val latRateUSD = getRateValuesDouble("USD", selCurValue1, mapLatest)
                val latRateEUR = getRateValuesDouble("EUR", selCurValue1, mapLatest)
                val latRateGBP = getRateValuesDouble("GBP", selCurValue1, mapLatest)
                val latRateCNY = getRateValuesDouble("CNY", selCurValue1, mapLatest)
                val latRateRUB = getRateValuesDouble("RUB", selCurValue1, mapLatest)

                if (elderRates != null) {
                    val mapElder = getMapFromCurRates(elderRates as CurRates)
                    val selCurValue2 = mapElder?.get(cur)
                    selCurValue2?.let {

                        val oldRateUSD = getRateValuesDouble("USD", selCurValue2, mapElder)
                        val oldRateEUR = getRateValuesDouble("EUR", selCurValue2, mapElder)
                        val oldRateGBP = getRateValuesDouble("GBP", selCurValue2, mapElder)
                        val oldRateCNY = getRateValuesDouble("CNY", selCurValue2, mapElder)
                        val oldRateRUB = getRateValuesDouble("RUB", selCurValue2, mapElder)

                        getGrowthView(tvGoldGrowth, getGrowthCoef(latRateUSD, oldRateUSD))
                        getGrowthView(tvSilverGrowth, getGrowthCoef(latRateEUR, oldRateEUR))
                        getGrowthView(tvPlatGrowth, getGrowthCoef(latRateGBP, oldRateGBP))
                        getGrowthView(tvPalladGrowth, getGrowthCoef(latRateCNY, oldRateCNY))
                        getGrowthView(tvGrowthRUB, getGrowthCoef(latRateRUB, oldRateRUB))
                    }
                } else {
                    getGrowthView(tvGoldGrowth, null)
                    getGrowthView(tvSilverGrowth, null)
                    getGrowthView(tvPlatGrowth, null)
                    getGrowthView(tvPalladGrowth, null)
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
            val res = 100 * dif / oldValue
            when {
                res == 0.0 || res.absoluteValue < 0.0001 -> 0.0
                res.absoluteValue > 0.0001 && res.absoluteValue < 0.001 -> if (res > 0.0) 0.001 else -0.001
                else -> res
            }
        } else null
    }

    private fun getGrowthView(tv: TextView, coef: Double?) {
        coef?.let {

            var text: String = decimalFormatter3p.format(coef).replace(',', '.') + "%"
            if (text[0] == '-')
                text = text.subSequence(1, text.length).toString()
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

    @SuppressLint("SimpleDateFormat")
    private fun getDate(longDate: Date?): String? {
        return longDate?.let {
            val formatter = SimpleDateFormat(FORMAT)
            val dateString = formatter.format(longDate)
            context?.getString(R.string.updated) + " " + dateString
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun openCalendar(context: Context?) {
        context?.let {
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]
            val dialog: Dialog =
                DatePickerDialog(
                    context,
                    DatePickerDialog.OnDateSetListener { _, y, m, d ->
                        val yr: String = y.toString()
                        val mnt = if (m + 1 < 10) "0${m + 1}" else "${m + 1}"
                        val dy = if (d < 10) "0$d" else d.toString()
                        val dateForApiRequest = "$yr-$mnt-$dy"

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                        val selectedDate = try {
                            dateFormat.parse(dateForApiRequest)
                        } catch (e: ParseException) {
                            null
                        }
                        selectedDate?.let {
                            val isValidDate = Date().after(selectedDate)

                            if (!isValidDate) {
                                showSnackbar(
                                    context.getString(R.string.InvalidInputCalendar),
                                    btnCalendarDate,
                                    false
                                )
                                return@OnDateSetListener
                            }

                            btnCalendarDate.text = selectedDate.toString()
                            ratesViewModel.setDate(dateForApiRequest)

                            btnCalendarDate.background =
                                resources.getDrawable(R.drawable.final_btnreset_currency)
                            btnCalendarNow.background =
                                resources.getDrawable(R.drawable.final_cur_latest_unselected)
                            btnCalendarDate.text = selectedDate.toString()
                            btnCalendarNow.text = ""
                        }

                    }, year, month, day
                )
            dialog.setCancelable(true)
            dialog.setOnCancelListener {
            }
            dialog.show()
        }
    }

    private fun clickLatest() {
        ratesViewModel.setDate(null)
        btnCalendarDate.background = resources.getDrawable(R.drawable.final_cur_latest_unselected)
        btnCalendarNow.background = resources.getDrawable(R.drawable.final_btnreset_currency)
        btnCalendarDate.text = ""
        btnCalendarNow.text = resources.getString(R.string.latest)
    }

    private fun selectBtnSpinner(cur: String, btnSpinner: String) {
        val flag = currencyMapFlags[cur]

        if (btnSpinner == CURRENCY_FROM) {
            btnSpinner1.text = cur
            flag?.let {
                btnSpinner1.setCompoundDrawablesWithIntrinsicBounds(flag, 0, 0, 0)
                etAmountInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, flag, 0)
            }
        } else if (btnSpinner == CURRENCY_TO) {
            btnSpinner2.text = cur
            flag?.let {
                btnSpinner2.setCompoundDrawablesWithIntrinsicBounds(flag, 0, 0, 0)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        ratesViewModel.removeSources()
    }
}

