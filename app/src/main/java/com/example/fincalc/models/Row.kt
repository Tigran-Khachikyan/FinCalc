package com.example.fincalc.models

interface Row {
    val curRowN: Int
    val balance: Double
    val percent: Double
    val payment: Double
}