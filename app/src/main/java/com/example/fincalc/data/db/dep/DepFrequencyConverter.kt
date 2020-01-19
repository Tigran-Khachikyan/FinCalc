package com.example.fincalc.data.db.dep

import androidx.room.TypeConverter
import com.example.fincalc.models.deposit.RepayFrequency

class DepFrequencyConverter {
    @TypeConverter
    fun fromEnumToString(type: RepayFrequency): String {
        return type.name
    }

    @TypeConverter
    fun fromStringToEnum(string: String): RepayFrequency {

        for (type in RepayFrequency.values())
            if (string == type.name)
                return type
        return RepayFrequency.OTHER
    }
}