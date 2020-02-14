package com.example.fincalc.models.deposit

import com.example.fincalc.R

enum class Frequency(val id: Int){

    MONTHLY(R.string.MonthlyPaymentDep),
    QUARTERLY(R.string.QuarterlyPaymentDep),
    AT_THE_END(R.string.AtTheEndPayment),
    OTHER(R.string.OTHER);

}
