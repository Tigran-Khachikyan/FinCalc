package com.example.fincalc.ui.cur.rates

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.fincalc.R
import com.example.fincalc.data.network.api_rates.ApiCurrency
import com.example.fincalc.data.network.api_rates.Rates
import com.example.fincalc.data.network.api_rates.ResponseCurApi
import com.example.fincalc.data.network.firebase.FirestoreApi
import com.example.fincalc.data.network.firebase.duration
import com.example.fincalc.data.network.firebase.getTime
import com.example.fincalc.data.network.hasNetwork
import com.example.fincalc.models.cur_met.getRatesFromMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.*
import kotlin.collections.HashMap

class RateFragment : Fragment() {

    private val SERVER_TIME_ZONE = "Etc/GMT-4"
    private val FORMAT = "yyyy-MM-dd HH:mm:ss"

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


    }

    override fun onResume() {
        super.onResume()
         ratesViewModel.getLatCurRates()?.observe(viewLifecycleOwner, Observer {

              val date = it.dateTime
              val ratesAMD = it.rates?.AMD

              Log.d("ksaks", "FRAGMENT DATE: $date")
              Log.d("ksaks", "FRAGMENT AMD: $ratesAMD")

          })

      /*  val date = "2020-01-27"
        ratesViewModel.getHisCurRates(date)?.observe(viewLifecycleOwner, Observer {

            Log.d("ksaks", "FRAGMENT DATE: $date")                 //NO_NETWORK = 123
            Log.d("ksaks", "FRAGMENT AMD: ${it.rates?.AMD}")       //API_SOURCE_PROBLEM = 255
            Log.d("ksaks", "FRAGMENT AMD: ${it.status}")           // OK = 777


        })*/
    }
}