package com.example.fincalc.ui.cur.rates

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.fincalc.R

class RateFragment : Fragment() {

    private lateinit var ratesViewModel: RateViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ratesViewModel =
            ViewModelProvider(this).get(RateViewModel::class.java)

        return inflater.inflate(R.layout.fragment_rate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ratesViewModel.getLatestRates().observe(viewLifecycleOwner, Observer {

            it?.let {
                Log.d("ggg", " result AMD in Fragment: ${it.rates.AMD}")
            }
        })

    }

}