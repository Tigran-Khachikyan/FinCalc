package com.my_1st.fincalc.ui.rates.metals

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
import com.my_1st.fincalc.R
import com.my_1st.fincalc.data.network.firebase.NO_NETWORK
import com.my_1st.fincalc.models.rates.mapRatesNameIcon
import com.my_1st.fincalc.ui.*
import com.my_1st.fincalc.ui.rates.AdapterRecRates
import kotlinx.android.synthetic.main.fragment_metals.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MetalsFragment : Fragment(), CoroutineScope {

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Main + job
    private lateinit var metalViewModel: MetalsViewModel
    private lateinit var adapter: AdapterRecRates
    private lateinit var sharedPref: SharedPreferences
    private var noNetworkWarningShown: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        metalViewModel = ViewModelProvider(this).get(MetalsViewModel::class.java)
        return inflater.inflate(R.layout.fragment_metals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvStatusMetals.setFont(FONT_PATH)

        layMetalsHider.visibility = View.INVISIBLE

        btnDateMetals.setCustomSizeVector(
            context, resTop = R.drawable.ic_calendar, sizeTopdp = 24
        )
        btnBaseMetals.setCustomSizeVector(
            context, resTop = R.drawable.ic_base_cur, sizeTopdp = 24
        )
        btnUnitMetals.setCustomSizeVector(
            context, resTop = R.drawable.ic_scale, sizeTopdp = 24
        )

        adapter = AdapterRecRates(requireContext(), null)
        recyclerMetals.setHasFixedSize(true)
        recyclerMetals.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerMetals.adapter = adapter

        sharedPref = view.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        val base = sharedPref.getString(CURRENCY_PREF, "USD")
        metalViewModel.setCurrency(base!!)

        metalViewModel.changeUnit()
        metalViewModel.setDate(null)

        btnDateMetals.setOnClickListener { v ->
            openCalendarHighOrderFunc(
                requireContext(), v
            ) { dateApi -> metalViewModel.setDate(dateApi) }
        }

        btnBaseMetals.setOnClickListener {
            getDialogCurHighOrderFunc(requireContext()) { cur ->
                metalViewModel.setCurrency(cur)
            }
        }

        btnUnitMetals.setOnClickListener {
            metalViewModel.changeUnit()
        }
    }

    override fun onStart() {
        super.onStart()

        job = Job()
        metalViewModel.getConvertRates().observe(viewLifecycleOwner, Observer {

            progressBarMetalsFr.visibility = View.VISIBLE
            tvProgLoadMetals.text = requireContext().getString(R.string.loading)

            it?.let {
                launch {
                    if (it.status == NO_NETWORK && !noNetworkWarningShown) {
                        noNetworkWarningShown = true
                        tvProgLoadMetals.text = requireContext().getString(R.string.noNetwork)
                        delay(3000)
                        tvProgLoadMetals.text = requireContext().getString(R.string.loadingFromCache)
                        delay(2000)
                    }

                    progressBarMetalsFr.visibility = View.GONE
                    layMetalsHider.visibility = View.VISIBLE

                    adapter.ratesRows = it.ratesBarList
                    adapter.notifyDataSetChanged()

                    setBaseCurToSharedPref(sharedPref, it.baseCur)

                    val date = formatterLong.format(it.date)
                    btnDateMetals.text = date

                    val textUnit =
                        if (it.unit == MetalsUnit.TROY_OUNCE) context?.getString(R.string.ounce) else context?.getString(
                            R.string.gram
                        )
                    btnUnitMetals.text = textUnit

                    val textCurName = it.baseCur
                    btnBaseMetals.text = textCurName
                    val res = mapRatesNameIcon[it.baseCur]?.second
                    res?.let { icon ->
                        btnBaseMetals.setCustomSizeVector(
                            context, resTop = R.drawable.ic_base_cur, sizeTopdp = 24,
                            resRight = icon, sizeRightdp = 32
                        )
                    }
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        job.cancel()
        metalViewModel.removeSources()
    }

}