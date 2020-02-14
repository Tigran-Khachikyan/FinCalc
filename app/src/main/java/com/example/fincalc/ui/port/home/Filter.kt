package com.example.fincalc.ui.port.home

import com.example.fincalc.models.Banking

 fun filterByCur(products: List<Banking>?, cur: List<String>?): List<Banking>? {
    return if (products != null) {
        return if (cur == null) products else {
            val inner = arrayListOf<Banking>()
            for (lo in products)
                if (lo.currency == cur[0])
                    inner.add(lo)
            inner
        }
    } else null
}

 fun sortByCur(products: List<Banking>?, curs: List<String>?): List<String>? {
    return products?.let {
        if (curs != null) {
            val newCurList = ArrayList<String>()
            newCurList.add(curs[0])
            for (pr in products)
                if (pr.currency != curs[0])
                    newCurList.add(pr.currency)
            newCurList.distinct()
        } else {
            val newCurList = arrayListOf<String>()
            for (pr in products)
                newCurList.add(pr.currency)
            newCurList.distinct()
        }
    }
}

 fun sortByRate(product: List<Banking>?, acc: Boolean?): List<Banking>? {
    return if (product != null)
        return when (acc) {
            null -> product
            true -> product.sortedBy { l -> l.rate }
            else -> product.sortedByDescending { l -> l.rate }
        }
    else null
}