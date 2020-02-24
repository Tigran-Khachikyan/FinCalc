package com.my_1st.fincalc.models

interface Banking {
    val amount: Long
    val months: Int
    val rate: Float
    val bank: String
    val currency: String
    val id: Int
    val date: String
}