package com.example.fincalc.data.db.dep

import androidx.room.TypeConverter
import com.example.fincalc.models.deposit.Frequency

class DepFrequencyConverter {
    @TypeConverter
    fun fromEnumToString(type: Frequency): String {
        return type.name
    }

    @TypeConverter
    fun fromStringToEnum(string: String): Frequency {

        for (type in Frequency.values())
            if (string == type.name)
                return type
        return Frequency.OTHER
    }
}