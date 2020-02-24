package com.my_1st.fincalc.data.db.loan

import androidx.room.TypeConverter
import com.my_1st.fincalc.models.credit.LoanType

class LoanTypeConverter {
    @TypeConverter
    fun fromEnumToString(type: LoanType): String = type.name

    @TypeConverter
    fun fromStringToEnum(string: String): LoanType {
        for (type in LoanType.values())
            if (string == type.name)
                return type
        return LoanType.OTHER
    }
}