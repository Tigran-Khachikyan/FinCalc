package com.example.fincalc.ui.port.balance

import com.example.fincalc.models.Banking

interface Filtering {

    fun filterByCur(products: ArrayList<in Banking>?, cur: List<String>?): ArrayList<in Banking>?

    fun sortCur(products: ArrayList<in Banking>?, curs: List<String>?): ArrayList<String>?

    fun sortByRate(product: ArrayList<in Banking>?, acc: Boolean?): ArrayList<in Banking>?

}