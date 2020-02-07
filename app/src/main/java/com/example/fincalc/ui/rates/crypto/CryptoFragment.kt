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
import com.nightonke.boommenu.BoomButtons.BoomButton
import com.nightonke.boommenu.OnBoomListenerAdapter
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
        // Inflate the layout for this fragment
        cryptoViewModel = ViewModelProvider(this).get(CryptoViewModel::class.java)
        return inflater.inflate(R.layout.fragment_crypto, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupCrypto.visibility = View.GONE

        bmbCryptoMenu.initialize(BMBTypes.CRYPTO)

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
                groupCrypto.visibility = View.VISIBLE

                adapter.ratesRows = it.ratesBarList
                adapter.notifyDataSetChanged()

                val date = context?.getString(R.string.Date) + ": " + formatterLong.format(it.date)
                tvCryptoDateTime.text = date

                val orderType =
                    if (it.sortedByPrice) context?.getString(R.string.orderByPrice)
                    else context?.getString(R.string.orderByPop)
                tvCryptoSortedBy.text = orderType

                val textCurName = "${context?.getString(R.string.Base)}: ${it.baseCur}"
                tvCryptoBaseCur.text = textCurName

                setBaseCurToSharedPref(sharedPref, it.baseCur)

                val flag = mapRatesNameIcon[it.baseCur]?.second
                flag?.let { ivFlagCrypto.setImageResource(flag) }

                bmbCryptoMenu.onBoomListener = object : OnBoomListenerAdapter() {
                    override fun onClicked(index: Int, boomButton: BoomButton) {
                        super.onClicked(index, boomButton)
                        when (index) {
                            0 -> getDialogCurHighOrderFunc(context) { cur ->
                                cryptoViewModel.setCurrency(cur)
                            }
                            1 -> openCalendarHighOrderFunc(
                                context, bmbCryptoMenu
                            ) { dateApi ->
                                cryptoViewModel.setDate(dateApi)
                            }
                            //UNIT /Ounce or gram
                            2 -> if (it.sortedByPrice)
                                cryptoViewModel.setOrder(false)
                            else
                                cryptoViewModel.setOrder(true)
                        }
                    }
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        cryptoViewModel.removeSources()
    }

}
