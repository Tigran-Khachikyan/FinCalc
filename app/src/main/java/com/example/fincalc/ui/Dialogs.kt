@file:Suppress("UNCHECKED_CAST")

package com.example.fincalc.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fincalc.R
import com.example.fincalc.ui.port.filter.AdapterRecyclerMultiChoice
import com.example.fincalc.ui.port.filter.FilterQuery
import com.example.fincalc.ui.port.filter.SearchOption
import com.example.fincalc.ui.port.home.LoansFilterViewModel


/*
showDialogLoansTypeFilter(homeViewModel)
showDialogCurrencyFilter(homeViewModel)
showDialogOrderPref(homeViewModel)
showDialogRemoveLoans(homeViewModel)*/





@SuppressLint("InflateParams")
fun showDialogTypeFilter(context: Context, filterPref: FilterQuery) {

    val dialBuilder = AlertDialog.Builder(context)
    val dialogView = LayoutInflater.from(context).inflate(R.layout.test_multichoice_dialog, null)
    dialBuilder.setView(dialogView)
    dialBuilder.setTitle(R.string.Filtering)
    dialBuilder.setIcon(R.drawable.ic_filter)
    dialBuilder.setMessage(R.string.selectTheCriteria)

    val recycler: RecyclerView = dialogView.findViewById(R.id.recyclerMultiChoice)
    val adapter = when (filterPref) {
        is LoansFilterViewModel -> {
            val possiblePrefs = filterPref.getExistTypes()
            val selPrefs = filterPref.getSelTypes()
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
        filterPref.setType(resultList)
    }
    //click CANCEL
    dialBuilder.setNegativeButton(
        context.getString(R.string.cancel)
    ) { _, _ -> }


    dialBuilder.setNeutralButton(
        if (adapter.selOptions.size != 0)
            context.getString(R.string.CLEAR)
        else context.getString(R.string.SELECT_ALL)
    ) { _, _ ->
    }

    val alertDialog = dialBuilder.create()
    alertDialog.show()
    alertDialog.setCustomView()
    var isClear = adapter.selOptions.size != 0
    alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
        .setOnClickListener {

            if (isClear) {
                adapter.selOptions.clear()
                isClear = false
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).text =
                    context.getString(R.string.SELECT_ALL)
            } else {
                adapter.selOptions = filterPref.getExistTypes()
                isClear = true
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).text =
                    context.getString(R.string.CLEAR)
            }
            adapter.notifyDataSetChanged()
        }

}

@SuppressLint("InflateParams")
fun showDialogCurrencyFilter(context: Context, filterPref: FilterQuery) {


    val dialBuilder = AlertDialog.Builder(context)
    val dialogView = LayoutInflater.from(context).inflate(R.layout.test_multichoice_dialog, null)
    dialBuilder.setView(dialogView)
    dialBuilder.setTitle(R.string.Filtering)
    dialBuilder.setIcon(R.drawable.ic_filter)
    dialBuilder.setMessage(R.string.filteredByCur)

    val recycler: RecyclerView = dialogView.findViewById(R.id.recyclerMultiChoice)
    val adapter = when (filterPref) {
        is LoansFilterViewModel -> {
            val possiblePrefs = filterPref.getExistCur()
            val selPrefs = filterPref.getSelCur()
            Log.d("uuuuurrib", "possiblePrefs: $possiblePrefs")
            Log.d("uuuuurrib", "selPrefs: $selPrefs")
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

        Log.d("uuuuurrib", "resultList: ${resultList.size}")

        filterPref.setCur(resultList)
    }
    //click CANCEL
    dialBuilder.setNegativeButton(
        context.getString(R.string.cancel)
    ) { d, _ ->  }

    dialBuilder.setNeutralButton(
        if (adapter.selOptions.size != 0)
            context.getString(R.string.CLEAR)
        else context.getString(R.string.SELECT_ALL)
    ) { _, _ ->
    }

    val alertDialog = dialBuilder.create()
    alertDialog.show()
    alertDialog.setCustomView()
    var isClear = adapter.selOptions.size != 0
    alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
        .setOnClickListener {

            if (isClear) {
                adapter.selOptions.clear()
                isClear = false
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).text =
                    context.getString(R.string.SELECT_ALL)
            } else {
                adapter.selOptions = filterPref.getExistCur()
                isClear = true
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).text =
                    context.getString(R.string.CLEAR)
            }
            adapter.notifyDataSetChanged()
        }

}

@SuppressLint("InflateParams")
fun showDialogSort(context: Context, filterPref: FilterQuery) {


    val dialBuilder = AlertDialog.Builder(context)
    val dialogView = LayoutInflater.from(context).inflate(R.layout.test_dialog_sort, null)
    dialBuilder.setView(dialogView)
    dialBuilder.setTitle(R.string.sortingOptions)
    dialBuilder.setIcon(R.drawable.ic_sort)

    val btnLatest: Button = dialogView.findViewById(R.id.btnSortByLatest)
    val btnHighestRates: Button = dialogView.findViewById(R.id.btnSortByRateHighest)
    val btnLowestRates: Button = dialogView.findViewById(R.id.btnSortByRateLowest)

    fun Button.setCheckedOtherUnchecked(icon: Int) {
        btnLatest.setViewChecked(false, R.drawable.ic_calendar)
        btnHighestRates.setViewChecked(false, R.drawable.ic_interest)
        btnLowestRates.setViewChecked(false, R.drawable.ic_interest)
        setViewChecked(true, icon)
    }

    var sort = filterPref.getSortPref()
    when (sort) {
        null -> btnLatest.setCheckedOtherUnchecked(R.drawable.ic_calendar)
        true -> btnHighestRates.setCheckedOtherUnchecked(R.drawable.ic_interest)
        false -> btnLowestRates.setCheckedOtherUnchecked(R.drawable.ic_interest)
    }

    btnLatest.setOnClickListener {
        btnLatest.setCheckedOtherUnchecked(R.drawable.ic_calendar)
        sort = null
    }

    btnHighestRates.setOnClickListener {
        btnHighestRates.setCheckedOtherUnchecked(R.drawable.ic_interest)
        sort = true
    }

    btnLowestRates.setOnClickListener {
        btnLowestRates.setCheckedOtherUnchecked(R.drawable.ic_interest)
        sort = false
    }


    //click SAVE
    dialBuilder.setPositiveButton(
        context.getString(R.string.OK)
    ) { _, _ ->
        filterPref.setSortPref(sort)
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
fun showDialogRemoveOrEditFilter(context: Context, filterPref: FilterQuery, option: SearchOption) {


    val dialBuilder = AlertDialog.Builder(context)
    dialBuilder.setTitle(R.string.editFilter)
    dialBuilder.setIcon(R.drawable.ic_alert)
    dialBuilder.setMessage(R.string.filterRemoveEditText)

    //click SAVE
    dialBuilder.setPositiveButton(
        context.getString(R.string.edit)
    ) { _, _ ->
        when (option) {
            SearchOption.FILTER_CURRENCY -> showDialogCurrencyFilter(context, filterPref)
            SearchOption.FILTER_TYPE -> showDialogTypeFilter(context, filterPref)
            SearchOption.SORT -> showDialogSort(context, filterPref)
        }
    }
    //click CANCEL
    dialBuilder.setNegativeButton(
        context.getString(R.string.cancel)
    ) { _, _ -> }

    dialBuilder.setNeutralButton(context.getString(R.string.remove)) { _, _ ->
        filterPref.removePref(option)
    }
    val alertDialog = dialBuilder.create()
    alertDialog.show()
    alertDialog.setCustomView()

}