package com.example.fincalc.data.network

interface Response {
    val rates: Rates
    val success: Boolean
    val timestamp: Int
    val historical: Boolean
    val date: String
}