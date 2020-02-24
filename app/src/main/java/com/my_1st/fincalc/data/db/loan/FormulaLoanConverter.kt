package com.my_1st.fincalc.data.db.loan

import androidx.room.TypeConverter
import com.my_1st.fincalc.models.credit.Formula

class FormulaLoanConverter {
    @TypeConverter
    fun fromEnumToString(formula: Formula): String = formula.name

    @TypeConverter
    fun fromStringToEnum(string: String): Formula {
        for (form in Formula.values())
            if (string == form.name)
                return form
        return Formula.ANNUITY
    }
}