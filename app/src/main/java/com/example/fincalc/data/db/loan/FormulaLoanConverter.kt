package com.example.fincalc.data.db.loan

import androidx.room.TypeConverter
import com.example.fincalc.models.credit.Formula

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