package com.example.fincalc.ui.rates.currency

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fincalc.R
import com.example.fincalc.models.rates.mapRatesNameIcon
import com.example.fincalc.ui.*
import com.example.fincalc.ui.rates.AdapterRecRates
import com.nightonke.boommenu.BoomButtons.BoomButton
import com.nightonke.boommenu.OnBoomListenerAdapter
import kotlinx.android.synthetic.main.activity_rates.*
import kotlinx.android.synthetic.main.fragment_crypto.*
import kotlinx.android.synthetic.main.fragment_currency.*

@Suppress("DEPRECATION")
class RateFragment : Fragment() {

    private lateinit var curViewModel: CurrencyViewModel
    private lateinit var sharedPref: SharedPreferences
    private lateinit var adapter: AdapterRecRates

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        curViewModel = ViewModelProvider(this).get(CurrencyViewModel::class.java)
        return inflater.inflate(R.layout.fragment_currency, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupCur.visibility = View.GONE

        sharedPref = view.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        val base = sharedPref.getString(CURRENCY_PREF, "USD")
        curViewModel.setBaseCur(base!!)
        curViewModel.setDate(null)

        ivTransferCur.setSvgColor(context!!, R.color.PortPrimaryLight)
        bmbCurMenu.initialize(BMBTypes.CURRENCY)

        adapter = AdapterRecRates(view.context, null)
        recyclerCur.setHasFixedSize(true)
        recyclerCur.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        recyclerCur.adapter = adapter
    }

    override fun onStart() {
        super.onStart()

        curViewModel.getConvertRates().observe(viewLifecycleOwner, Observer {

            Log.d("tttt", "IT DATE FRAGMENT: ${it?.date}")

            it?.let {

                groupCur.visibility = View.VISIBLE

                adapter.ratesRows = it.ratesBarList
                adapter.notifyDataSetChanged()

                val date = context?.getString(R.string.Date) + ": " + formatterLong.format(it.date)
                tvCurDateTime.text = date

                val textCurName = "${context?.getString(R.string.Base)}: ${it.baseCur}"
                tvCurBaseCur.text = textCurName

                val base = it.baseCur
                val from = it.curFrom

                setBaseCurToSharedPref(sharedPref, it.baseCur)

                btnCurBase.text = base
                val fromText = from?:"?"
                btnCurFrom.text = fromText
                tvCurResult.text = it.resAmount?.let { d -> "= " + decimalFormatter3p.format(d) +" " +base }

                val flagBase = mapRatesNameIcon[it.baseCur]?.second
                flagBase?.let {
                    ivFlagCur.setImageResource(flagBase)
                    btnCurBase.setCompoundDrawablesWithIntrinsicBounds(flagBase, 0, 0, 0)
                }

                val flagFrom = mapRatesNameIcon[it.curFrom]?.second
                flagFrom?.let { f ->
                    btnCurFrom.setCompoundDrawablesWithIntrinsicBounds(f, 0, 0, 0)
                }

                bmbCurMenu.onBoomListener = object : OnBoomListenerAdapter() {
                    override fun onClicked(index: Int, boomButton: BoomButton) {
                        super.onClicked(index, boomButton)
                        when (index) {
                            0 -> {
                                Log.d("tttt", "0 INDEX BMB")
                                getDialogCurHighOrderFunc(context) { cur ->
                                    curViewModel.setBaseCur(cur)
                                }
                            }
                            1 -> {
                                Log.d("tttt", "1 INDEX BMB")

                                openCalendarHighOrderFunc(
                                    context, bmbCurMenu
                                ) { dateApi ->
                                    curViewModel.setDate(dateApi)
                                }
                            }
                        }
                    }
                }

                btnConvertCur.setOnClickListener {
                    from?.let {
                        curViewModel.setAmount(etCurAmountInput.text.toString().toDouble())
                    } ?: showSnackBar(R.string.invalidCurConvert, btnConvertCur, Options.CURRENCY)
                    hideKeyboard(this.requireActivity())
                }

                btnCurBase.setOnClickListener {
                    getDialogCurHighOrderFunc(context) { selCur ->
                        curViewModel.setBaseCur(selCur)
                    }
                }

                btnCurFrom.setOnClickListener {
                    getDialogCurHighOrderFunc(context) { selCur ->
                        curViewModel.setCurFrom(selCur)
                        curViewModel.setAmount(1.0)
                        etCurAmountInput.setText("1.0")
                    }
                }

                ivTransferCur.setOnClickListener {
                    from?.let {
                        curViewModel.setBaseCur(from)
                        curViewModel.setCurFrom(base)
                    }
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        curViewModel.removeSources()
    }
}

