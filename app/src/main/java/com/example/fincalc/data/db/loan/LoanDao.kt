package com.example.fincalc.data.db.loan

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LoanDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(loan: Loan)

    @Query("DELETE FROM loans WHERE _id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM loans")
    suspend fun deleteAll()

    @Query("SELECT * FROM loans")
    fun getLoans(): LiveData<List<Loan>>

    @Query("SELECT * FROM loans WHERE _id = :id")
    fun getLoanById(id: Int): LiveData<Loan>
}