package com.example.fincalc.models.loan


import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.math.pow

object CalculatorLoan {


    fun calculateLoan(queryLoan: QueryLoan?): Map<FormulaLoan, ScheduleLoan?> {
        var mapMain: Map<FormulaLoan, ScheduleLoan?> = mapOf()

        runBlocking {

            val scheduleAnnuity =
                async(Dispatchers.Default) { getSchedule(queryLoan, FormulaLoan.ANNUITY) }
            val scheduleDiff =
                async(Dispatchers.Default) { getSchedule(queryLoan, FormulaLoan.DIFFERENTIAL) }
            val scheduleOverd =
                async(Dispatchers.Default) { getSchedule(queryLoan, FormulaLoan.OVERDRAFT) }

            Log.d("tts", "runBlocking: " + Thread.currentThread().name)

            mapMain = mapOf(
                Pair(FormulaLoan.ANNUITY, scheduleAnnuity.await()),
                Pair(FormulaLoan.DIFFERENTIAL, scheduleDiff.await()),
                Pair(FormulaLoan.OVERDRAFT, scheduleOverd.await())
            )
        }
        return mapMain
    }


    fun getSchedule(query: QueryLoan?, formula: FormulaLoan): ScheduleLoan? {
        val result: ScheduleLoan? = when (formula) {
            FormulaLoan.DIFFERENTIAL -> differential(query)
            FormulaLoan.ANNUITY -> annuity(query)
            FormulaLoan.OVERDRAFT -> overdraft(query)
            else -> null
        }
        if (result != null && query != null) {
            result.rowCount = query.months
            result.sumBasic = query.sum.toDouble()
            result.oneTimeOtherCosts = query.oneTimeOtherCosts

            result.oneTimeCommission = if (query.minOneTimeCommissionSumOrRate)
                query.oneTimeCommissionSum.coerceAtLeast((query.oneTimeCommissionRate * query.sum / 100).toDouble())
            else
                query.oneTimeCommissionSum + query.oneTimeCommissionRate * query.sum / 100
            result.totalMonthlyCommissionPayment += result.oneTimeComAndCosts
        }
        return result
    }

    private fun differential(query: QueryLoan?): ScheduleLoan? {
        val schedule: ScheduleLoan?
        if (query != null) {
            schedule = ScheduleLoan(queryLoan = query)
            val sumBasic: Double = query.sum.toDouble()
            for (i in 0 until query.months) {
                val row = ScheduleLoan.Row()
                row.currentRowNumber = i + 1
                row.monthlyLoan = sumBasic / query.months
                row.sumRemain = sumBasic - i * row.monthlyLoan
                row.monthlyPercent = row.sumRemain * query.rate / (12 * 100)

                val annualCommission: Double = when {
                    i % 12 == 0 -> {
                        if (query.minAnnualCommissionSumOrRate)
                            query.annualCommissionSum.coerceAtLeast(query.annualCommissionRate * row.sumRemain / 100)
                        else
                            query.annualCommissionSum + query.annualCommissionRate * row.sumRemain / 100
                    }
                    else -> 0.0
                }

                val monthlyCommission: Double = if (query.minMonthlyCommissionSumOrRate)
                    query.monthlyCommissionSum.coerceAtLeast(query.monthlyCommissionRate * row.sumRemain / 100)
                else
                    query.monthlyCommissionSum + query.monthlyCommissionRate * row.sumRemain / 100

                row.monthlyCommission = annualCommission + monthlyCommission

                row.totalMonthlyPayment =
                    row.monthlyLoan + row.monthlyPercent + row.monthlyCommission

                schedule.totalPercentSum += row.monthlyPercent
                schedule.totalMonthlyCommissionPayment += row.monthlyCommission
                schedule.totalAnnualCommissionPayment += annualCommission
                schedule.rowList.add(row)
            }
        } else {
            schedule = null
        }
        return schedule
    }

    private fun annuityCoefficient(months: Int, rate: Float): Float {
        val coefficientPart = (1 + rate / (12 * 100)).pow(months)
        return ((rate / (12 * 100)) * coefficientPart / (coefficientPart - 1))
    }

    private fun annuity(query: QueryLoan?): ScheduleLoan? {
        val schedule: ScheduleLoan?
        if (query != null) {
            schedule = ScheduleLoan(queryLoan = query)
            val sumBasic: Double = query.sum.toDouble()
            val annuityCoefficient = annuityCoefficient(query.months, query.rate)
            val annuityPayment = sumBasic * annuityCoefficient

            for (i in 0 until query.months) {
                val row = ScheduleLoan.Row()
                row.sumRemain =
                    if (i == 0) sumBasic else schedule.rowList[i - 1].sumRemain - schedule.rowList[i - 1].monthlyLoan
                row.currentRowNumber = i + 1
                row.monthlyPercent = row.sumRemain * query.rate / (12 * 100)
                row.monthlyLoan = annuityPayment - row.monthlyPercent

                val annualCommission: Double = when {
                    i % 12 == 0 -> {
                        if (query.minAnnualCommissionSumOrRate)
                            query.annualCommissionSum.coerceAtLeast(query.annualCommissionRate * row.sumRemain / 100)
                        else
                            query.annualCommissionSum + query.annualCommissionRate * row.sumRemain / 100
                    }
                    else -> 0.0
                }

                val monthlyCommission: Double = if (query.minMonthlyCommissionSumOrRate)
                    query.monthlyCommissionSum.coerceAtLeast(query.monthlyCommissionRate * row.sumRemain / 100)
                else
                    query.monthlyCommissionSum + query.monthlyCommissionRate * row.sumRemain / 100

                row.monthlyCommission = annualCommission + monthlyCommission

                row.totalMonthlyPayment =
                    row.monthlyLoan + row.monthlyPercent + row.monthlyCommission

                schedule.totalPercentSum += row.monthlyPercent
                schedule.totalMonthlyCommissionPayment += row.monthlyCommission
                schedule.totalAnnualCommissionPayment += annualCommission
                schedule.rowList.add(row)
            }
        } else {
            schedule = null
        }
        return schedule
    }

    private fun overdraft(query: QueryLoan?): ScheduleLoan? {
        val schedule: ScheduleLoan?
        if (query != null) {
            schedule = ScheduleLoan(queryLoan = query)
            val sumBasic: Double = query.sum.toDouble()
            for (i in 0 until query.months) {
                val row = ScheduleLoan.Row()
                row.currentRowNumber = i + 1
                row.monthlyLoan = if (i == query.months - 1) sumBasic else 0.0
                row.sumRemain = sumBasic
                row.monthlyPercent = row.sumRemain * query.rate / (12 * 100)

                val annualCommission: Double = when {
                    i % 12 == 0 -> {
                        if (query.minAnnualCommissionSumOrRate)
                            query.annualCommissionSum.coerceAtLeast(query.annualCommissionRate * row.sumRemain / 100)
                        else
                            query.annualCommissionSum + query.annualCommissionRate * row.sumRemain / 100
                    }
                    else -> 0.0
                }

                val monthlyCommission: Double = if (query.minMonthlyCommissionSumOrRate)
                    query.monthlyCommissionSum.coerceAtLeast(query.monthlyCommissionRate * row.sumRemain / 100)
                else
                    query.monthlyCommissionSum + query.monthlyCommissionRate * row.sumRemain / 100

                row.monthlyCommission = annualCommission + monthlyCommission

                row.totalMonthlyPayment =
                    row.monthlyLoan + row.monthlyPercent + row.monthlyCommission
                schedule.totalPercentSum += row.monthlyPercent
                schedule.totalMonthlyCommissionPayment += row.monthlyCommission
                schedule.totalAnnualCommissionPayment += annualCommission
                schedule.rowList.add(row)
            }
        } else {
            schedule = null
        }
        return schedule
    }
}

