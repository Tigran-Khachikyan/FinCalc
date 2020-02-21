package com.example.fincalc.models


import com.example.fincalc.data.db.dep.Deposit
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.models.credit.Formula
import com.example.fincalc.models.credit.RowLoan
import com.example.fincalc.models.credit.TableLoan
import com.example.fincalc.models.deposit.Frequency
import com.example.fincalc.models.deposit.RowDep
import kotlin.math.pow

object Calculator {

    //Loan
    fun getRowsLoan(loan: Loan): ArrayList<RowLoan> = when (loan.formula) {
        Formula.DIFFERENTIAL -> differential(loan)
        Formula.ANNUITY -> annuity(loan)
        Formula.OVERDRAFT -> overdraft(loan)
    }

    fun getTotalPerOrCom(percent: Boolean, rows: ArrayList<RowLoan>): Double {
        var result = 0.0
        if (percent)
            rows.forEach {
                result += it.percent
            }
        else
            rows.forEach {
                result += it.monthCom
            }
        return result
    }

    fun getOneTimeComAndCost(loan: Loan): Double {
        val oneTimeCom: Double = if (loan.minOneTimeComSumOrRate)
            (loan.oneTimeComSum.coerceAtLeast((loan.oneTimeComRate * loan.amount / 100).toInt())).toDouble()
        else
            (loan.oneTimeComSum + loan.oneTimeComRate * loan.amount / 100).toDouble()
        return oneTimeCom + loan.otherCosts
    }

    private fun differential(loan: Loan): ArrayList<RowLoan> {
        val result: ArrayList<RowLoan> = arrayListOf()

        val sumBasic: Double = loan.amount.toDouble()
        for (i in 0 until loan.months) {
            val row = RowLoan()
            row.curRowN = i + 1
            row.monthLoan = sumBasic / loan.months
            row.balance = sumBasic - i * row.monthLoan
            row.percent = row.balance * loan.rate / (12 * 100)

            val annualCom: Double = when {
                i % 12 == 0 -> {
                    if (loan.minAnnComSumOrRate)
                        loan.annComSum.coerceAtLeast((loan.annComRate * row.balance / 100).toInt()).toDouble()
                    else
                        loan.annComSum + loan.annComRate * row.balance / 100
                }
                else -> 0.0
            }

            val monthlyCom: Double = if (loan.minMonthComSumOrRate)
                loan.monthComSum.coerceAtLeast((loan.monthComRate * row.balance / 100).toInt()).toDouble()
            else
                loan.monthComSum + loan.monthComRate * row.balance / 100

            row.monthCom = annualCom + monthlyCom

            row.payment = row.monthLoan + row.percent + row.monthCom
            result.add(row)
        }
        return result
    }

    private fun annuityCoefficient(months: Int, rate: Float): Float {
        val coefficientPart = (1 + rate / (12 * 100)).pow(months)
        return ((rate / (12 * 100)) * coefficientPart / (coefficientPart - 1))
    }

    private fun annuity(loan: Loan): ArrayList<RowLoan> {

        val sumBasic: Double = loan.amount.toDouble()
        val annuityCoefficient =
            annuityCoefficient(
                loan.months,
                loan.rate
            )
        val annuityPayment = sumBasic * annuityCoefficient

        val result = ArrayList<RowLoan>()
        for (i in 0 until loan.months) {
            val row = RowLoan()
            row.balance =
                if (i == 0) sumBasic else result[i - 1].balance - result[i - 1].monthLoan

            row.curRowN = i + 1
            row.percent = row.balance * loan.rate / (12 * 100)
            row.monthLoan = annuityPayment - row.percent

            val annualCom: Double = when {
                i % 12 == 0 -> {
                    if (loan.minAnnComSumOrRate)
                        loan.annComSum.coerceAtLeast((loan.annComRate * row.balance).toInt() / 100).toDouble()
                    else
                        loan.annComSum + loan.annComRate * row.balance / 100
                }
                else -> 0.0
            }

            val monthlyCom: Double = if (loan.minMonthComSumOrRate)
                loan.monthComSum.coerceAtLeast((loan.monthComRate * row.balance / 100).toInt()).toDouble()
            else
                loan.monthComSum + loan.monthComRate * row.balance / 100

            row.monthCom = annualCom + monthlyCom

            row.payment = row.monthLoan + row.percent + row.monthCom

            result.add(row)
        }
        return result
    }

    private fun overdraft(loan: Loan): ArrayList<RowLoan> {

        val result = ArrayList<RowLoan>()
        val sumBasic: Double = loan.amount.toDouble()
        for (i in 0 until loan.months) {
            val row = RowLoan()
            row.curRowN = i + 1
            row.monthLoan = if (i == loan.months - 1) sumBasic else 0.0
            row.balance = sumBasic
            row.percent = row.balance * loan.rate / (12 * 100)

            val annualCom: Double = when {
                i % 12 == 0 -> {
                    if (loan.minAnnComSumOrRate)
                        loan.annComSum.coerceAtLeast((loan.annComRate * row.balance / 100).toInt()).toDouble()
                    else
                        loan.annComSum + loan.annComRate * row.balance / 100
                }
                else -> 0.0
            }

            val monthlyCom: Double = if (loan.minMonthComSumOrRate)
                loan.monthComSum.coerceAtLeast((loan.monthComRate * row.balance / 100).toInt()).toDouble()
            else
                loan.monthComSum + loan.monthComRate * row.balance / 100

            row.monthCom = annualCom + monthlyCom

            row.payment =
                row.monthLoan + row.percent + row.monthCom

            result.add(row)
        }
        return result
    }

    //Deposit
    fun getRowsDep(dep: Deposit): ArrayList<RowDep> = when (dep.frequency) {

        Frequency.MONTHLY, Frequency.QUARTERLY -> getRowsByPeriodDep(dep)

        else -> {
            val result = ArrayList<RowDep>()
            val newRow = RowDep()
            newRow.curRowN = 1
            newRow.balance = dep.amount.toDouble()
            newRow.percent = dep.months * newRow.balance * dep.rate / 1200
            newRow.perAfterTax = newRow.percent * (1 - dep.taxRate / 100)
            newRow.payment = newRow.balance + newRow.perAfterTax
            result.add(newRow)
            result
        }
    }

    private fun getRowsByPeriodDep(dep: Deposit): ArrayList<RowDep> {

        val result = ArrayList<RowDep>()

        var factor = 0
        var rowCount = 0

        if (dep.frequency == Frequency.MONTHLY) {
            factor = 1
            rowCount = dep.months
        } else if (dep.frequency == Frequency.QUARTERLY) {
            rowCount = dep.months / 3
            factor = 3
        }

        if (dep.capitalize) {

            for (i in 0 until rowCount - 1) {
                val newRow = RowDep()
                newRow.curRowN = i + 1
                newRow.balance =
                    if (i == 0) dep.amount.toDouble()
                    else result[i - 1].balance + result[i - 1].perAfterTax
                newRow.percent = factor * newRow.balance * dep.rate / 1200
                newRow.perAfterTax = newRow.percent * (1 - dep.taxRate / 100)
                newRow.payment = 0.0
                result.add(newRow)
            }

            val lastRow = RowDep()
            lastRow.curRowN = rowCount
            if (rowCount != 0) {
                lastRow.balance =
                    if (rowCount == 1) dep.amount.toDouble()
                    else result[rowCount - 2].balance + result[rowCount - 2].perAfterTax
                lastRow.percent = lastRow.balance * dep.rate / 1200
                lastRow.perAfterTax = lastRow.percent * (1 - dep.taxRate / 100)
                lastRow.payment = lastRow.perAfterTax + lastRow.balance
                result.add(lastRow)
            }

        } else {
            for (i in 0 until rowCount - 1) {
                val newRow = RowDep()
                newRow.curRowN = i + 1
                newRow.balance = dep.amount.toDouble()
                newRow.percent = newRow.balance * dep.rate / 1200
                newRow.perAfterTax = newRow.percent * (1 - dep.taxRate / 100)
                newRow.payment = newRow.perAfterTax
                result.add(newRow)
            }

            val lastRow = RowDep()
            lastRow.curRowN = rowCount
            lastRow.balance = dep.amount.toDouble()
            lastRow.percent = lastRow.balance * dep.rate / 1200
            lastRow.perAfterTax = lastRow.percent * (1 - dep.taxRate / 100)
            lastRow.payment = lastRow.perAfterTax + lastRow.balance
            result.add(lastRow)
        }
        return result
    }

    fun getTotalPerDep(rows: ArrayList<RowDep>): Double {
        var result = 0.0
        for (r in rows)
            result += r.percent
        return result
    }

    fun getEffectiveRate(dep: Deposit): Float {
        val rate = dep.rate / 100
        val months = dep.months
        val perCoef: Float = when (dep.frequency) {
            Frequency.MONTHLY -> 12F
            Frequency.QUARTERLY -> 4F
            Frequency.AT_THE_END, Frequency.OTHER -> (12 / months.toFloat())
        }
        return ((1 + rate / perCoef).pow(perCoef) - 1) * 100
    }

    fun getRealRate(table: TableLoan): Float {
        val rate = table.loan.rate / 100
        val months = table.loan.months
        val sumBasic = table.sumBasic
        val annualCom = (table.totalComDuring + table.oneTimeComAndCosts) * 12 / months
        val annComRate = annualCom * 100 / sumBasic

        val perCoef: Float = when (table.loan.formula) {
            Formula.ANNUITY, Formula.DIFFERENTIAL -> 12F
            Formula.OVERDRAFT -> (12 / months.toFloat())
        }
        val effRate = ((1 + rate / perCoef).pow(perCoef) - 1) * 100
        return (effRate + annComRate).toFloat()
    }
}

