package com.example.fincalc.data.db.dep

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DepositDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dep: Deposit)

    @Query("DELETE FROM deposits WHERE _id =:id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM deposits")
    suspend fun deleteAll()

    @Query("SELECT * FROM deposits")
    fun getDeposits(): LiveData<List<Deposit>>

    @Query("SELECT * FROM deposits WHERE _id = :id")
    fun getDepositById(id: Int): LiveData<Deposit>
}