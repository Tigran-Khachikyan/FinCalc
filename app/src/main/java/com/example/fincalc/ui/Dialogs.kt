@file:Suppress("UNCHECKED_CAST")

package com.example.fincalc.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fincalc.R
import com.example.fincalc.data.db.dep.Deposit
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.models.Banking
import com.example.fincalc.ui.port.filter.AdapterRecyclerMultiChoice
import com.example.fincalc.ui.port.filter.FilterQuery
import com.example.fincalc.ui.port.filter.SearchOption
import com.example.fincalc.ui.port.home.DepFilterViewModel
import com.example.fincalc.ui.port.home.LoansFilterViewModel


@SuppressLint("InflateParams")
fun showDialogTypeFilter(context: Context, filterPref: FilterQuery) {

    val dialBuilder = AlertDialog.Builder(context)
    val dialogView = LayoutInflater.from(context).inflate(R.layout.test_multichoice_dialog, null)
    dialBuilder.setView(dialogView)
    dialBuilder.setTitle(R.string.Filtering)
    dialBuilder.setIcon(R.drawable.ic_filter)
    val message =
        if (filterPref is LoansFilterViewModel) context.getString(R.string.youWantToFilterWithLoanType)
        else context.getString(R.string.youWantToFilterWithFreq)
    dialBuilder.setMessage(message)

    val recycler: RecyclerView = dialogView.findViewById(R.id.recyclerMultiChoice)
    val adp =
        AdapterRecyclerMultiChoice(context, filterPref.getExistTypes(), filterPref.getSelTypes())
    recycler.setHasFixedSize(true)
    recycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    recycler.adapter = adp

    //click SAVE
    dialBuilder.setPositiveButton(context.getString(R.string.OK)) { _, _ ->
        val resultList = adp.selOptions
        filterPref.setType(resultList)
    }
    //click CANCEL
    dialBuilder.setNegativeButton(context.getString(R.string.cancel)) { _, _ -> }

    dialBuilder.setNeutralButton(
        if (adp.selOptions.size != 0) context.getString(R.string.CLEAR)
        else context.getString(R.string.SELECT_ALL)
    ) { _, _ -> }

    val alertDialog = dialBuilder.create()
    alertDialog.show()
    alertDialog.setCustomView()
    var isClear = adp.selOptions.size != 0
    alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
        .setOnClickListener {
            if (isClear) {
                adp.selOptions.clear()
                isClear = false
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).text =
                    context.getString(R.string.SELECT_ALL)
            } else {
                adp.selOptions = filterPref.getExistTypes()
                isClear = true
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).text =
                    context.getString(R.string.CLEAR)
            }
            adp.notifyDataSetChanged()
        }

}

@SuppressLint("InflateParams")
fun showDialogCurrencyFilter(context: Context, filterPref: FilterQuery) {

    val dialBuilder = AlertDialog.Builder(context)
    val dialogView = LayoutInflater.from(context).inflate(R.layout.test_multichoice_dialog, null)
    dialBuilder.setView(dialogView)
    dialBuilder.setTitle(R.string.Filtering)
    dialBuilder.setIcon(R.drawable.ic_filter)
    val message = context.getString(R.string.youWantToFilterWithCur)
    dialBuilder.setMessage(message)

    val recycler: RecyclerView = dialogView.findViewById(R.id.recyclerMultiChoice)
    val adp = AdapterRecyclerMultiChoice(context, filterPref.getExistCur(), filterPref.getSelCur())
    recycler.setHasFixedSize(true)
    recycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    recycler.adapter = adp

    //click SAVE
    dialBuilder.setPositiveButton(context.getString(R.string.OK)) { _, _ ->
        val resultList = adp.selOptions as MutableSet<String>
        filterPref.setCur(resultList)
    }
    //click CANCEL
    dialBuilder.setNegativeButton(context.getString(R.string.cancel)) { _, _ -> }

    dialBuilder.setNeutralButton(
        if (adp.selOptions.size != 0) context.getString(R.string.CLEAR)
        else context.getString(R.string.SELECT_ALL)
    ) { _, _ -> }

    val alertDialog = dialBuilder.create()
    alertDialog.show()
    alertDialog.setCustomView()
    var isClear = adp.selOptions.size != 0
    alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
        .setOnClickListener {

            if (isClear) {
                adp.selOptions.clear()
                isClear = false
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).text =
                    context.getString(R.string.SELECT_ALL)
            } else {
                adp.selOptions = filterPref.getExistCur()
                isClear = true
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).text =
                    context.getString(R.string.CLEAR)
            }
            adp.notifyDataSetChanged()
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

    val byDefault = "${context.getString(R.string.latest)} ${context.getString(R.string.byDefault)}"
    btnLatest.text = byDefault

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
    dialBuilder.setPositiveButton(context.getString(R.string.OK)) { _, _ ->
        filterPref.setSortPref(sort)
    }
    //click CANCEL
    dialBuilder.setNegativeButton(context.getString(R.string.cancel)) { _, _ -> }

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
    dialBuilder.setPositiveButton(context.getString(R.string.edit)) { _, _ ->
        when (option) {
            SearchOption.FILTER_CURRENCY -> showDialogCurrencyFilter(context, filterPref)
            SearchOption.FILTER_TYPE -> showDialogTypeFilter(context, filterPref)
            SearchOption.SORT -> showDialogSort(context, filterPref)
        }
    }
    //click CANCEL
    dialBuilder.setNegativeButton(context.getString(R.string.cancel)) { _, _ -> }

    dialBuilder.setNeutralButton(context.getString(R.string.remove)) { _, _ ->
        filterPref.removePref(option)
    }
    val alertDialog = dialBuilder.create()
    alertDialog.show()
    alertDialog.setCustomView()
}

fun showDialogRemoveBanking(
    view: View,
    filterPref: FilterQuery,
    isLoan: Boolean? = null,
    id: Int = 0,
    allLoans: Boolean? = null,
    func: () -> Unit
) {
    if (isLoan == null && allLoans == null)
        return

    val context = view.context
    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
    builder.setTitle(R.string.warning)
    builder.setIcon(R.drawable.ic_alert)
    val alertText = when {
        isLoan != null -> {
            val text =
                if (isLoan) context.getString(R.string.loan) else context.getString(R.string.deposit)
            context.getString(R.string.AreYouSureRemove) + " " + text + "?"
        }
        allLoans != null -> {
            val text =
                if (allLoans) context.getString(R.string.Loans) else context.getString(R.string.deposits)
            context.getString(R.string.AreYouSureRemoveAll) + " " + text + "?"
        }
        else -> ""
    }
    builder.setMessage(alertText)
    builder.setPositiveButton(R.string.OK)
    { _, _ ->
        when {
            isLoan != null -> {
                if (isLoan) (filterPref as LoansFilterViewModel).deleteLoanById(id)
                else (filterPref as DepFilterViewModel).deleteDepById(id)
            }
            allLoans != null -> {
                if (allLoans) (filterPref as LoansFilterViewModel).deleteAllLoans()
                else (filterPref as DepFilterViewModel).deleteAllDep()
            }
            else -> {
            }
        }
        func()
    }
    builder.setNegativeButton(R.string.cancel)
    { _, _ -> }

    val alertDialog = builder.create()
    alertDialog.show()
    alertDialog.setCustomView()
}