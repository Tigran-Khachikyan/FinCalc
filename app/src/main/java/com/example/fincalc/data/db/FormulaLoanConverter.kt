package com.example.fincalc.data.db

import androidx.room.TypeConverter
import com.example.fincalc.models.loan.FormulaLoan

class FormulaLoanConverter {
    @TypeConverter
    fun fromEnumToString(formula: FormulaLoan): String {
        return formula.name
    }

    @TypeConverter
    fun fromStringToEnum(string: String): FormulaLoan {

        for (form in FormulaLoan.values())
            if (string == form.name)
                return form
        return FormulaLoan.NONE
    }
}