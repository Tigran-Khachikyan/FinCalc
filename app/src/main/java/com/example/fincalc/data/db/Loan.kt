package com.example.fincalc.data.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fincalc.models.loan.FormulaLoan
import com.example.fincalc.models.loan.QueryLoan

@Entity(tableName = "loans")
data class Loan(

    @ColumnInfo(name = "_bank")
    val bank: String,

    val type: LoanType,
    val repayment_program: FormulaLoan,

    @ColumnInfo(name = "_currency")
    val currency: String,
    @Embedded(prefix = "_query_")
    val queryLoan: QueryLoan
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Int = 0
}