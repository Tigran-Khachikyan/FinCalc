package com.example.fincalc.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fincalc.data.db.dep.DepFrequencyConverter
import com.example.fincalc.data.db.dep.Deposit
import com.example.fincalc.data.db.dep.DepositDao
import com.example.fincalc.data.db.loan.FormulaLoanConverter
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.data.db.loan.LoanDao
import com.example.fincalc.data.db.loan.LoanTypeConverter

@androidx.room.Database(entities = [Loan::class, Deposit::class], version = 1, exportSchema = false)
@TypeConverters(
    FormulaLoanConverter::class,
    LoanTypeConverter::class,
    DepFrequencyConverter::class
)
abstract class Database : RoomDatabase() {

    abstract fun getLoanDao(): LoanDao
    abstract fun getDepDao(): DepositDao

    companion object {
        @Volatile
        private var INSTANCE: Database? = null

        operator fun invoke(context: Context): Database {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Database::class.java,
                    "financial_forces_db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}