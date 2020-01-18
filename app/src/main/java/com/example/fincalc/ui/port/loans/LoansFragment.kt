package com.example.fincalc.ui.port.loans

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
import com.example.fincalc.ui.port.NavViewModel
import kotlinx.android.synthetic.main.fragment_loans.*

class LoansFragment : Fragment() {

    private lateinit var loansViewModel: LoansViewModel
    private lateinit var adapter: AdapterRecLoansDetail

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        loansViewModel = ViewModelProvider(this).get(LoansViewModel::class.java)
        return inflater.inflate(R.layout.fragment_loans, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AdapterRecLoansDetail(arrayListOf(), context)
        recLoansPager.setHasFixedSize(true)
        recLoansPager.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        recLoansPager.adapter = adapter
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recLoansPager)

        loansViewModel.getLoanList()?.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.loanList = it
                adapter.notifyDataSetChanged()
            }
        })
    }

    override fun onStop() {
        super.onStop()
        NavViewModel.Container.setNav(null,null)
    }
}