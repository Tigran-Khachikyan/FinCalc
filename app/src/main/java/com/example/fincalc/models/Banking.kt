package com.example.fincalc.models

interface Banking {
    val amount: Long
    val months: Int
    val rate: Float
    val bank: String
    val currency: String
    val id: Int
}