package com.example.fincalc.ui.rates.metals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.fincalc.R
import com.example.fincalc.ui.BMBTypes
import com.example.fincalc.ui.initialize
import kotlinx.android.synthetic.main.fragment_metals.*

class MetalsFragment : Fragment() {

    private lateinit var metalViewModel: MetalsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        metalViewModel =
            ViewModelProvider(this).get(MetalsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_metals, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bmbMetalsMenu.initialize(BMBTypes.METALS)
    }

}