package com.example.fincalc.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@androidx.room.Database(entities = [Loan::class], version = 1,  exportSchema = false)
@TypeConverters(FormulaLoanConverter::class, LoanTypeConverter::class)
abstract class Database : RoomDatabase() {

    abstract fun getLoanDao(): LoanDao

    companion object {
        @Volatile
        private var INSTANCE: Database? = null

        operator fun invoke(context: Context): Database {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Database::class.java,
                    "finance_db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}