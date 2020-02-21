package com.example.fincalc.ui.rates.market


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation

import com.example.fincalc.R
import com.example.fincalc.ui.playAnimation
import kotlinx.android.synthetic.main.fragment_market.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * A simple [Fragment] subclass.
 */
class MarketFragment : Fragment(), CoroutineScope {

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Main + job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        job = Job()
        return inflater.inflate(R.layout.fragment_market, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launch {
            textSurfaceMarket.playAnimation(R.string.markets, 5000)
        }
        fabCurMarket.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.navigation_rates))
        fabCryptoMarket.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.navigation_crypto))
        fabMetalsMarket.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.navigation_metals))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
    }
}
