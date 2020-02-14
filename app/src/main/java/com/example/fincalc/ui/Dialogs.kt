@file:Suppress("UNCHECKED_CAST")

package com.example.fincalc.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fincalc.R
import com.example.fincalc.models.credit.LoanType
import com.example.fincalc.ui.port.filter.AdapterRecyclerMultiChoice
import com.example.fincalc.ui.port.home.FilterPref
import com.example.fincalc.ui.port.home.LoansFilterViewModel

/*
showDialogLoansTypeFilter(homeViewModel)
showDialogCurrencyFilter(homeViewModel)
showDialogOrderPref(homeViewModel)
showDialogRemoveLoans(homeViewModel)*/





@SuppressLint("InflateParams")
fun showDialogLoanFilter(context: Context, filterPref: FilterPref) {


    val dialBuilder = AlertDialog.Builder(context)
    val dialogView = LayoutInflater.from(context).inflate(R.layout.test_multichoice_dialog, null)
    dialBuilder.setView(dialogView)
    dialBuilder.setTitle(R.string.Filtering)
    dialBuilder.setIcon(R.drawable.ic_filter)
    dialBuilder.setMessage(R.string.selectTheCriteria)

    val recycler: RecyclerView = dialogView.findViewById(R.id.recyclerMultiChoice)
    val adapter = when (filterPref) {
        is LoansFilterViewModel -> {
            val possiblePrefs = filterPref.getAllTypes()
            val selPrefs = filterPref.getSelTypes()
            Log.d("uuuuurrib","possiblePrefs: $possiblePrefs" )
            Log.d("uuuuurrib","selPrefs: $selPrefs" )
            AdapterRecyclerMultiChoice(context, possiblePrefs, selPrefs)
        }
        else -> TODO()
    }
    recycler.setHasFixedSize(true)
    recycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    recycler.adapter = adapter

    //click SAVE
    dialBuilder.setPositiveButton(
        context.getString(R.string.OK)
    ) { _, _ ->

        val resultList = adapter.selOptions

        Log.d("uuuuurrib","resultList: ${resultList.size}" )

        filterPref.setType(resultList)
    }
    //click CANCEL
    dialBuilder.setNegativeButton(
        context.getString(R.string.cancel)
    ) { _, _ -> }

    val alertDialog = dialBuilder.create()
    alertDialog.show()
    alertDialog.setCustomView()

}

@SuppressLint("InflateParams")
fun showDialogCurrencyFilter(context: Context, filterPref: FilterPref) {


    val dialBuilder = AlertDialog.Builder(context)
    val dialogView = LayoutInflater.from(context).inflate(R.layout.test_multichoice_dialog, null)
    dialBuilder.setView(dialogView)
    dialBuilder.setTitle(R.string.Filtering)
    dialBuilder.setIcon(R.drawable.ic_filter)
    dialBuilder.setMessage(R.string.selectedCurrency)

    val recycler: RecyclerView = dialogView.findViewById(R.id.recyclerMultiChoice)
    val adapter = when (filterPref) {
        is LoansFilterViewModel -> {
            val possiblePrefs = filterPref.getAllCur()
            val selPrefs = filterPref.getSelCur()
            Log.d("uuuuurrib","possiblePrefs: $possiblePrefs" )
            Log.d("uuuuurrib","selPrefs: $selPrefs" )
            AdapterRecyclerMultiChoice(context, possiblePrefs, selPrefs)
        }
        else -> TODO()
    }
    recycler.setHasFixedSize(true)
    recycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    recycler.adapter = adapter

    //click SAVE
    dialBuilder.setPositiveButton(
        context.getString(R.string.OK)
    ) { _, _ ->

        val resultList = adapter.selOptions as MutableSet<String>

        Log.d("uuuuurrib","resultList: ${resultList.size}" )

        filterPref.setCur(resultList)
    }
    //click CANCEL
    dialBuilder.setNegativeButton(
        context.getString(R.string.cancel)
    ) { _, _ -> }

    val alertDialog = dialBuilder.create()
    alertDialog.show()
    alertDialog.setCustomView()

}
