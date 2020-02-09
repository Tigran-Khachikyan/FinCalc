package com.example.fincalc.data.db.loan

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fincalc.models.Banking
import com.example.fincalc.models.credit.Formula
import com.example.fincalc.models.credit.LoanType

@Entity(tableName = "loans")
class Loan(
    override val amount: Long,
    override val months: Int,
    override val rate: Float,
    val oneTimeComSum: Int,
    val oneTimeComRate: Float,
    val annComSum: Int,
    val annComRate: Float,
    val monthComSum: Int,
    val monthComRate: Float,
    val minOneTimeComSumOrRate: Boolean,
    val minMonthComSumOrRate: Boolean,
    val minAnnComSumOrRate: Boolean,
    val otherCosts: Int
) : Banking {

    override var bank: String = ""
    override var currency: String = ""
    override var date: String = ""
    lateinit var type: LoanType
    lateinit var formula: Formula

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    override var id: Int = 0
}