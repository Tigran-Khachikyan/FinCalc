package com.my_1st.fincalc.data.db.dep

import androidx.room.TypeConverter
import com.my_1st.fincalc.models.deposit.Frequency

class DepFrequencyConverter {
    @TypeConverter
    fun fromEnumToString(type: Frequency): String = type.name

    @TypeConverter
    fun fromStringToEnum(string: String): Frequency {

        for (type in Frequency.values())
            if (string == type.name)
                return type
        return Frequency.OTHER
    }
}