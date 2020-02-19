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
import com.example.fincalc.data.network.firebase.NO_NETWORK
import com.example.fincalc.models.rates.mapRatesNameIcon
import com.example.fincalc.ui.*
import com.example.fincalc.ui.rates.AdapterRecRates
import kotlinx.android.synthetic.main.fragment_crypto.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * A simple [Fragment] subclass.
 */
class CryptoFragment : Fragment(), CoroutineScope {

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Main + job
    private lateinit var cryptoViewModel: CryptoViewModel
    private lateinit var adapter: AdapterRecRates
    private lateinit var sharedPref: SharedPreferences
    private var noNetworkWarningShown: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        job = Job()
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
        cryptoViewModel.setOrder(Order.POPULARITY)
        cryptoViewModel.setDate(null)

        btnDateCrypto.setOnClickListener { btn ->
            openCalendarHighOrderFunc(
                requireContext(), btn
            ) { dateApi ->
                cryptoViewModel.setDate(dateApi)
            }
        }

        btnBaseCrypto.setOnClickListener {
            getDialogCurHighOrderFunc(requireContext()) { cur ->
                cryptoViewModel.setCurrency(cur)
            }
        }

        btnOrderCrypto.setOnClickListener {
            cryptoViewModel.changeOrder()
        }
    }

    override fun onStart() {
        super.onStart()
        cryptoViewModel.getConvertRates().observe(viewLifecycleOwner, Observer {

            progressBarCryptoFr.visibility = View.VISIBLE
            tvProgLoadCrypto.text = requireContext().getString(R.string.loading)

            it?.let {
                launch {
                    if (it.status == NO_NETWORK && !noNetworkWarningShown) {
                        noNetworkWarningShown = true
                        tvProgLoadCrypto.text = requireContext().getString(R.string.noNetwork)
                        delay(3000)
                        tvProgLoadCrypto.text = requireContext().getString(R.string.loadingFromCache)
                        delay(2000)
                    }
                    progressBarCryptoFr.visibility = View.GONE
                    layCryptoIntro.visibility = View.VISIBLE
                    layCryptoOptions.visibility = View.VISIBLE
                    adapter.ratesRows = it.ratesBarList
                    adapter.notifyDataSetChanged()

                    val date = formatterLong.format(it.date)
                    btnDateCrypto.text = date

                    val orderType =
                        if (it.order == Order.PRICE) context?.getString(R.string.sortByPrice)
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
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
    }

    override fun onStop() {
        super.onStop()
        cryptoViewModel.removeSources()
    }

}
