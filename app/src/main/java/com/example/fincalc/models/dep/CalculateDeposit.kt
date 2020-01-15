package com.example.fincalc.models.dep


fun getScheduleDep(query: QueryDep): ScheduleDep {
    val result = ScheduleDep()

    result.sumBasic = query.amount
    result.rowList = getRowList(query)

    for (i in result.rowList.indices){
        result.totalPercent += result.rowList[i].percent
        result.totalPercentAfterTaxing += result.rowList[i].percentAfterTaxing
        result.totalPayment += result.rowList[i].payment
    }

    return result
}

private fun getRowList(query: QueryDep): ArrayList<ScheduleDep.Row> {

    val rowList: ArrayList<ScheduleDep.Row>

    when (query.accrual) {
        PaymentInterval.MONTHLY -> {

            rowList = getRowListByPeriod(query, PaymentInterval.MONTHLY)
          /*  val rowCount = query.months

            if (query.capitalization) {

                for (i in 0 until rowCount - 1) {

                    val newRow = ScheduleDep.Row()
                    newRow.currentRowNumber = i + 1
                    newRow.balance =
                        if (i == 0) query.amount.toDouble()
                        else rowList[i - 1].balance + rowList[i - 1].percentAfterTaxing
                    newRow.percent = newRow.balance * query.rate / 1200
                    newRow.percentAfterTaxing = newRow.percent * (1 - query.taxRate / 100)
                    newRow.payment = 0.0

                    rowList.add(newRow)
                }

                val lastRow = ScheduleDep.Row()
                lastRow.currentRowNumber = rowCount
                lastRow.balance =
                    if (rowCount == 1) query.amount.toDouble()
                    else rowList[rowCount - 2].balance + rowList[rowCount - 2].percentAfterTaxing
                lastRow.percent = lastRow.balance * query.rate / 1200
                lastRow.percentAfterTaxing = lastRow.percent * (1 - query.taxRate / 100)
                lastRow.payment = lastRow.percentAfterTaxing + lastRow.balance

                rowList.add(lastRow)

            } else {

                for (i in 0 until rowCount - 1) {

                    val newRow = ScheduleDep.Row()
                    newRow.currentRowNumber = i + 1
                    newRow.balance = query.amount.toDouble()
                    newRow.percent = newRow.balance * query.rate / 1200
                    newRow.percentAfterTaxing = newRow.percent * (1 - query.taxRate / 100)
                    newRow.payment = newRow.percentAfterTaxing

                    rowList.add(newRow)
                }

                val lastRow = ScheduleDep.Row()
                lastRow.currentRowNumber = rowCount
                lastRow.balance = query.amount.toDouble()
                lastRow.percent = lastRow.balance * query.rate / 1200
                lastRow.percentAfterTaxing = lastRow.percent * (1 - query.taxRate / 100)
                lastRow.payment = lastRow.percentAfterTaxing + lastRow.balance

                rowList.add(lastRow)

            }*/
        }

        PaymentInterval.QUARTERLY -> {

           rowList = getRowListByPeriod(query, PaymentInterval.QUARTERLY)
            /*

            val rowCount = query.months / 3

            if (query.capitalization) {

                for (i in 0 until rowCount - 1) {

                    val newRow = ScheduleDep.Row()
                    newRow.currentRowNumber = i + 1
                    newRow.balance =
                        if (i == 0) query.amount.toDouble()
                        else rowList[i - 1].balance + rowList[i - 1].percentAfterTaxing
                    newRow.percent = newRow.balance * query.rate / 400
                    newRow.percentAfterTaxing = newRow.percent * (1 - query.taxRate / 100)
                    newRow.payment = 0.0

                    rowList.add(newRow)
                }

                val lastRow = ScheduleDep.Row()
                lastRow.currentRowNumber = rowCount
                lastRow.balance =
                    if (rowCount == 1) query.amount.toDouble()
                    else rowList[rowCount - 2].balance + rowList[rowCount - 2].percentAfterTaxing
                lastRow.percent = lastRow.balance * query.rate / 400
                lastRow.percentAfterTaxing = lastRow.percent * (1 - query.taxRate / 100)
                lastRow.payment = lastRow.percentAfterTaxing + lastRow.balance

                rowList.add(lastRow)

            } else {

                for (i in 0 until rowCount - 1) {

                    val newRow = ScheduleDep.Row()
                    newRow.currentRowNumber = i + 1
                    newRow.balance = query.amount.toDouble()
                    newRow.percent = newRow.balance * query.rate / 400
                    newRow.percentAfterTaxing = newRow.percent * (1 - query.taxRate / 100)
                    newRow.payment = newRow.percentAfterTaxing

                    rowList.add(newRow)
                }

                val lastRow = ScheduleDep.Row()
                lastRow.currentRowNumber = rowCount
                lastRow.balance = query.amount.toDouble()
                lastRow.percent = lastRow.balance * query.rate / 400
                lastRow.percentAfterTaxing = lastRow.percent * (1 - query.taxRate / 100)
                lastRow.payment = lastRow.percentAfterTaxing + lastRow.balance

                rowList.add(lastRow)

            }
*/
        }

        PaymentInterval.END_OF_THE_CONTRACT -> {

            rowList = arrayListOf()
            val newRow = ScheduleDep.Row()
            newRow.currentRowNumber = 1
            newRow.balance = query.amount.toDouble()
            newRow.percent = query.months * newRow.balance * query.rate / 1200
            newRow.percentAfterTaxing = newRow.percent * (1 - query.taxRate / 100)
            newRow.payment = newRow.balance + newRow.percentAfterTaxing
            rowList.add(newRow)
        }
    }
    return rowList
}


private fun getRowListByPeriod(query: QueryDep, period: PaymentInterval): ArrayList<ScheduleDep.Row> {

    val rowList = ArrayList<ScheduleDep.Row>()

    var factor = 0
    var rowCount = 0

    when (period) {
        PaymentInterval.MONTHLY -> {
            factor = 1
            rowCount = query.months
        }
        PaymentInterval.QUARTERLY -> {
            rowCount = query.months / 3
            factor = 3
        }
        else -> -999
    }

    if (query.capitalization) {

        for (i in 0 until rowCount - 1) {

            val newRow = ScheduleDep.Row()
            newRow.currentRowNumber = i + 1
            newRow.balance =
                if (i == 0) query.amount.toDouble()
                else rowList[i - 1].balance + rowList[i - 1].percentAfterTaxing
            newRow.percent = factor * newRow.balance * query.rate / 1200
            newRow.percentAfterTaxing = newRow.percent * (1 - query.taxRate / 100)
            newRow.payment = 0.0

            rowList.add(newRow)
        }

        val lastRow = ScheduleDep.Row()
        lastRow.currentRowNumber = rowCount
        if(rowCount!=0) {
            lastRow.balance =
                if (rowCount == 1) query.amount.toDouble()
                else rowList[rowCount - 2].balance + rowList[rowCount - 2].percentAfterTaxing
            lastRow.percent = lastRow.balance * query.rate / 1200
            lastRow.percentAfterTaxing = lastRow.percent * (1 - query.taxRate / 100)
            lastRow.payment = lastRow.percentAfterTaxing + lastRow.balance

            rowList.add(lastRow)
        }

    } else {

        for (i in 0 until rowCount - 1) {

            val newRow = ScheduleDep.Row()
            newRow.currentRowNumber = i + 1
            newRow.balance = query.amount.toDouble()
            newRow.percent = newRow.balance * query.rate / 1200
            newRow.percentAfterTaxing = newRow.percent * (1 - query.taxRate / 100)
            newRow.payment = newRow.percentAfterTaxing

            rowList.add(newRow)
        }

        val lastRow = ScheduleDep.Row()
        lastRow.currentRowNumber = rowCount
        lastRow.balance = query.amount.toDouble()
        lastRow.percent = lastRow.balance * query.rate / 1200
        lastRow.percentAfterTaxing = lastRow.percent * (1 - query.taxRate / 100)
        lastRow.payment = lastRow.percentAfterTaxing + lastRow.balance

        rowList.add(lastRow)

    }
    return rowList
}





