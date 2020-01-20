package com.example.fincalc.data.db.dep

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fincalc.models.Banking
import com.example.fincalc.models.deposit.Frequency

@Entity(tableName = "deposits")
data class Deposit(

    override val amount: Long,
    override val months: Int,
    override val rate: Float,
    val capitalize: Boolean,
    val taxRate: Float,
    val frequency: Frequency
) : Banking {

    override var bank: String = ""
    override var currency: String = ""

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    override var id: Int = 0
}