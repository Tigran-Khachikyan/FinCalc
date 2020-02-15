package com.example.fincalc.ui.port.filter

import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.models.credit.LoanType
import com.example.fincalc.ui.port.filter.SearchOption.*

object FilterBanking {

    private val filterQueue = mutableSetOf<SearchOption>()

    fun addNewOption(option: SearchOption) {
        filterQueue.add(option)
    }

    fun addRemoveOption(option: SearchOption) {
        filterQueue.remove(option)
    }

    fun getQueue() = filterQueue

    fun filterCascade(
        loans: List<Loan>?,
        types: MutableSet<LoanType>?,
        curs: MutableSet<String>?,
        sortAsc: Boolean?
    ): List<Loan>? {

        return if (loans != null) {
            val result1 = if (filterQueue.size > 0) {
                when (filterQueue.elementAt(0)) {
                    FILTER_CURRENCY -> filterByCurrency(loans, curs)
                    FILTER_TYPE -> filterByType(loans, types)
                    SORT -> sortByRate(loans, sortAsc)
                }
            } else loans
            val result2 = if (filterQueue.size > 1) {
                when (filterQueue.elementAt(1)) {
                    FILTER_CURRENCY -> filterByCurrency(result1, curs)
                    FILTER_TYPE -> filterByType(result1, types)
                    SORT -> sortByRate(result1, sortAsc)
                }
            } else result1
            val result3 = if (filterQueue.size > 2) {
                when (filterQueue.elementAt(2)) {
                    FILTER_CURRENCY -> filterByCurrency(result2, curs)
                    FILTER_TYPE -> filterByType(result2, types)
                    SORT -> sortByRate(result2, sortAsc)
                }
            } else result2
            result3
        } else null
    }

    private fun filterByType(loans: List<Loan>?, types: MutableSet<LoanType>?): List<Loan>? {
        return loans?.let {
            when {
                types == null -> it
                types.isEmpty() -> arrayListOf()
                else -> it.filter { loan -> types.contains(loan.type) }
            }
        }
    }

    private fun filterByCurrency(loans: List<Loan>?, cur: MutableSet<String>?): List<Loan>? {
        return loans?.let {
            when {
                cur == null -> it
                cur.isEmpty() -> arrayListOf()
                else -> it.filter { loan -> cur.contains(loan.currency) }
            }
        }
    }

    private fun sortByRate(loans: List<Loan>?, ascending: Boolean?): List<Loan>? {
        return loans?.let {
            when (ascending) {
                null -> it.reversed()
                true -> it.sortedByDescending { loan -> loan.rate }
                false -> it.sortedBy { loan -> loan.rate }
            }
        }
    }
}
