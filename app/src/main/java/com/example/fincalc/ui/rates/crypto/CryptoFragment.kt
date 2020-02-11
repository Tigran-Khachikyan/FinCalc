package com.example.fincalc.ui.rates.crypto


import android.content.SharedPreferences
import android.os.Bundle
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
import kotlinx.android.synthetic.main.fragment_crypto.*

/**
 * A simple [Fragment] subclass.
 */
class CryptoFragment : Fragment() {

    private lateinit var cryptoViewModel: CryptoViewModel
    private lateinit var adapter: AdapterRecRates
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cryptoViewModel = ViewModelProvider(this).get(CryptoViewModel::class.java)
        return inflater.inflate(R.layout.fragment_crypto, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvStatusCrypto.setFont(FONT_PATH)

        layCryptoIntro.visibility = View.INVISIBLE
        layCryptoOptions.visibility = View.INVISIBLE

        btnDateCrypto.setCustomSizeVector(
            context, resTop = R.drawable.ic_calendar, sizeTopdp = 24
        )
        btnBaseCrypto.setCustomSizeVector(
            context, resTop = R.drawable.ic_base_cur, sizeTopdp = 24
        )
        btnOrderCrypto.setCustomSizeVector(
            context, resTop = R.drawable.ic_sort, sizeTopdp = 24
        )

        adapter = AdapterRecRates(context!!, null)
        recyclerCrypto.setHasFixedSize(true)
        recyclerCrypto.layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
        recyclerCrypto.adapter = adapter

        sharedPref = view.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        val base = sharedPref.getString(CURRENCY_PREF, "USD")
        cryptoViewModel.setCurrency(base!!)

        cryptoViewModel.setOrder(true)
        cryptoViewModel.setDate(null)
    }

    override fun onStart() {
        super.onStart()
        cryptoViewModel.getConvertRates().observe(viewLifecycleOwner, Observer {

            it?.let {
                layCryptoIntro.visibility = View.VISIBLE
                layCryptoOptions.visibility = View.VISIBLE
                adapter.ratesRows = it.ratesBarList
                adapter.notifyDataSetChanged()

                val date =formatterLong.format(it.date)
                btnDateCrypto.text = date

                val orderType =
                    if (it.sortedByPrice) context?.getString(R.string.sortByPrice)
                    else context?.getString(R.string.sortByPop)
                btnOrderCrypto.text = orderType

                val res = mapRatesNameIcon[it.baseCur]?.second
                res?.let { icon ->
                    btnBaseCrypto.setCustomSizeVector(
                        context,
                        resTop = R.drawable.ic_base_cur, sizeTopdp = 24,
                        resRight = icon, sizeRightdp = 32
                    )
                }
                btnBaseCrypto.text = it.baseCur
                setBaseCurToSharedPref(sharedPref, it.baseCur)

                btnDateCrypto.setOnClickListener { btn ->
                    openCalendarHighOrderFunc(
                        context, btn
                    ) { dateApi ->
                        cryptoViewModel.setDate(dateApi)
                    }
                }

                btnBaseCrypto.setOnClickListener {
                    getDialogCurHighOrderFunc(context) { cur ->
                        cryptoViewModel.setCurrency(cur)
                    }
                }

                btnOrderCrypto.setOnClickListener { _ ->
                    if (it.sortedByPrice)
                        cryptoViewModel.setOrder(false)
                    else
                        cryptoViewModel.setOrder(true)
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        cryptoViewModel.removeSources()
    }

}
