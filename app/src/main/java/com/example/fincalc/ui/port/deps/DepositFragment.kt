package com.example.fincalc.ui.port.deps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.fincalc.R
import kotlinx.android.synthetic.main.fragment_deps.*

class DepositFragment : Fragment() {

    private lateinit var depViewModel: DepViewModel
    private lateinit var adapter: AdapterRecDepDetail


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        depViewModel =
            ViewModelProvider(this).get(DepViewModel::class.java)
        return inflater.inflate(R.layout.fragment_deps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AdapterRecDepDetail(arrayListOf(), context)
        recDepPager.setHasFixedSize(true)
        recDepPager.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        recDepPager.adapter = adapter
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recDepPager)

        depViewModel.getDepList()?.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.depList = it
                adapter.notifyDataSetChanged()
            }
        })
    }
}