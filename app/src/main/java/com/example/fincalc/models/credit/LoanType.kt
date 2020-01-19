package com.example.fincalc.models.credit

import com.example.fincalc.R

enum class LoanType(val id: Int) {
    MORTGAGE(R.string.MORTGAGE),
    BUSINESS(R.string.BUSINESS),
    CREDIT_LINES(R.string.CREDIT_LINES),
    GOLD_PLEDGE_SECURED(R.string.GOLD_PLEDGE_SECURED),
    DEPOSIT_SECURED(R.string.DEPOSIT_SECURED),
    UNSECURED(R.string.UNSECURED),
    CAR_LOAN(R.string.CAR_LOAN),
    CONSUMER_LOAN(R.string.CONSUMER_LOAN),
    STUDENT_LOAN(R.string.STUDENT_LOAN),
    OTHER(R.string.OTHER)
}
