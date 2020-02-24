package com.my_1st.fincalc.data.db.dep

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.my_1st.fincalc.models.Banking
import com.my_1st.fincalc.models.deposit.Frequency

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
    override var date: String = ""

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    override var id: Int = 0
}