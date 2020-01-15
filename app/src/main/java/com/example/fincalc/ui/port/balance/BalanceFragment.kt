package com.example.fincalc.ui.port.balance

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fincalc.R
import com.example.fincalc.ui.port.NaviViewModel
import com.example.fincalc.ui.port.OnViewHolderClick
import kotlinx.android.synthetic.main.fragment_balance.*

class BalanceFragment : Fragment() {

    private lateinit var balanceViewModel: BalanceViewModel
    private lateinit var adapterRecLoan: AdapterRecLoanBalance

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        balanceViewModel = ViewModelProvider(this).get(BalanceViewModel::class.java)
        return inflater.inflate(R.layout.fragment_balance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//holder click
        val onViewHolderClick = object : OnViewHolderClick {
            override fun openLoan(position: Int) {
            }
        }

        adapterRecLoan = AdapterRecLoanBalance(arrayListOf(), balanceViewModel, onViewHolderClick)
        recyclerLoanBalanceFr.setHasFixedSize(true)
        recyclerLoanBalanceFr.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
        recyclerLoanBalanceFr.adapter = adapterRecLoan

        balanceViewModel.getLoans()?.observe(viewLifecycleOwner, Observer {
            it?.let {

                adapterRecLoan.loanList = it
                adapterRecLoan.notifyDataSetChanged()

                adapterRecLoan.onViewHolderClick = object : OnViewHolderClick {
                    override fun openLoan(position: Int) {
                        val selectedLoan = it[position]
                        NaviViewModel.Container.setNavi(true, selectedLoan)
                    }
                }

            }
        })
    }


}