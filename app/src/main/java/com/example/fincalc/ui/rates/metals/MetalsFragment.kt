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
import com.nightonke.boommenu.BoomButtons.BoomButton
import com.nightonke.boommenu.OnBoomListenerAdapter
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

        groupMetals.visibility = View.GONE
        bmbMetalsMenu.initialize(BMBTypes.METALS)

        adapter = AdapterRecRates(context!!, null)
        recyclerMetals.setHasFixedSize(true)
        recyclerMetals.layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
        recyclerMetals.adapter = adapter

        sharedPref = view.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        val base = sharedPref.getString(CURRENCY_PREF, "USD")
        metalViewModel.setCurrency(base!!)

        metalViewModel.setUnit(true)
        metalViewModel.setDate(null)
    }

    override fun onStart() {
        super.onStart()

        metalViewModel.getConvertRates().observe(viewLifecycleOwner, Observer {

            it?.let {
                groupMetals.visibility = View.VISIBLE

                adapter.ratesRows = it.ratesBarList
                adapter.notifyDataSetChanged()

                val date = context?.getString(R.string.Date) + ": " + formatterLong.format(it.date)
                tvMetalsDateTime.text = date

                val textUnit = context?.getString(R.string.Unit) + ": " + if (it.isUnitOunce)
                    context?.getString(R.string.ounce) else context?.getString(R.string.gram)
                tvMetalsSortedBy.text = textUnit

                val textCurName = "${context?.getString(R.string.Base)}: ${it.baseCur}"
                tvMetalsBaseCur.text = textCurName

                setBaseCurToSharedPref(sharedPref, it.baseCur)

                val flag = mapRatesNameIcon[it.baseCur]?.second
                flag?.let { ivFlagMetal.setImageResource(flag) }

                bmbMetalsMenu.onBoomListener =
                    object : OnBoomListenerAdapter() {
                        override fun onClicked(index: Int, boomButton: BoomButton) {
                            super.onClicked(index, boomButton)
                            when (index) {
                                //CALENDAR
                                0 -> getDialogCurHighOrderFunc(context) { cur ->
                                    metalViewModel.setCurrency(cur)
                                }
                                //BASE CURRENCY
                                1 -> openCalendarHighOrderFunc(
                                    context, bmbMetalsMenu
                                ) { dateApi ->
                                    metalViewModel.setDate(dateApi)
                                }
                                //UNIT /Ounce or gram
                                2 -> if (it.isUnitOunce)
                                    metalViewModel.setUnit(false)
                                else
                                    metalViewModel.setUnit(true)

                            }
                        }
                    }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        metalViewModel.removeSources()
    }

}