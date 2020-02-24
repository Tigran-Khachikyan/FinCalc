package com.my_1st.fincalc.ui.rates

open class RatesBar(
   open val code: String,
   open val name: String,
   open val icon: Int,
   open var price: Double,
   open var growth: Float?
)