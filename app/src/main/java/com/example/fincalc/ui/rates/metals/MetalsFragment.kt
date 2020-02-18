package com.example.fincalc.ui.rates.metals

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
import kotlinx.android.synthetic.main.fragment_metals.*

class MetalsFragment : Fragment() {

    private lateinit var metalViewModel: MetalsViewModel
    private lateinit var adapter: AdapterRecRates
    private lateinit var sharedPref: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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

        metalViewModel.getConvertRates().observe(viewLifecycleOwner, Observer {

            progressBarMetalsFr.visibility = View.VISIBLE

            it?.let {
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
        })
    }

    override fun onStop() {
        super.onStop()
        metalViewModel.removeSources()
    }

}