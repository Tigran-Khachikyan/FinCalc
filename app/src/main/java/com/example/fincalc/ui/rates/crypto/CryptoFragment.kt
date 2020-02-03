package com.example.fincalc.ui.rates.crypto


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.fincalc.R
import com.example.fincalc.ui.rates.AdapterRecRates
import kotlinx.android.synthetic.main.fragment_crypto.*

/**
 * A simple [Fragment] subclass.
 */
class CryptoFragment : Fragment() {

    private lateinit var cryptoViewModel: CryptoViewModel
    private lateinit var adapter: AdapterRecRates

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

        adapter = AdapterRecRates(context!!, null)
        recyclerCrypto.setHasFixedSize(true)
        recyclerCrypto.layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
        recyclerCrypto.adapter = adapter

        cryptoViewModel.setCurrency("USD")
        cryptoViewModel.setOrder(true)
        cryptoViewModel.setDate(null)
    }

    override fun onStart() {
        super.onStart()
        cryptoViewModel.getConvertRates().observe(viewLifecycleOwner, Observer {

            Log.d("derdd", "FRAGMENT: it.count: ${it?.size}")

            it?.let {
                adapter.ratesRows = it
                adapter.notifyDataSetChanged()
            }

        })
    }

}
