package com.example.fincalc.models.dep

data class QueryDep (
    val amount: Long,
    val months: Int,
    val rate: Float,
    val accrual: PaymentInterval,
    val capitalization:Boolean,
    val taxRate: Float
)

enum class PaymentInterval{
    MONTHLY,
    QUARTERLY,
    END_OF_THE_CONTRACT
}