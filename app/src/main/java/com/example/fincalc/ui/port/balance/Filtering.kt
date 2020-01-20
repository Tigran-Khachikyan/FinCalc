package com.example.fincalc.ui.port.balance

import com.example.fincalc.models.Banking

interface Filtering {

    fun filterByCur(products: List<Banking>?, cur: List<String>?): List<Banking>?

    fun sortCur(products: List<Banking>?, curs: List<String>?): List<String>?

    fun sortByRate(product: List<Banking>?, acc: Boolean?): List<Banking>?

}
