package com.example.fincalc.ui.port.filter

import com.example.fincalc.models.Banking

interface Filtering {

    fun filterCascade(
        filterQueue: MutableSet<SearchOption>,
        banking: List<Banking>?,
        types: MutableSet<*>?,
        curs: MutableSet<String>?,
        sortAsc: Boolean?
    ): List<Banking>?

    fun filterByType(banking: List<Banking>?, types: MutableSet<*>?): List<Banking>?


    fun filterByCurrency(banking: List<Banking>?, cur: MutableSet<String>?): List<Banking>? {
        return banking?.let {
            when {
                cur == null -> it
                cur.isEmpty() -> arrayListOf()
                else -> it.filter { loan -> cur.contains(loan.currency) }
            }
        }
    }

    fun sortByRate(banking: List<Banking>?, ascending: Boolean?): List<Banking>? {
        return banking?.let {
            when (ascending) {
                null -> it.reversed()
                true -> it.sortedByDescending { loan -> loan.rate }
                false -> it.sortedBy { loan -> loan.rate }
            }
        }
    }
}