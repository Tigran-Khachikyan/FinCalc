package com.example.fincalc.data.db.dep

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DepositDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dep: Deposit)

    @Delete
    suspend fun delete(dep: Deposit)

    @Query("DELETE FROM deposits")
    suspend fun deleteAll()

    @Query("SELECT * FROM deposits")
    fun getDeposits(): LiveData<List<Deposit>>
}