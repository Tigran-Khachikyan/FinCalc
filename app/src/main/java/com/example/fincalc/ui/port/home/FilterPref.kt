package com.example.fincalc.ui.port.home

import com.example.fincalc.models.deposit.Frequency

interface FilterPref {
    fun getAllCur(): MutableSet<String>
    fun getSelCur(): MutableSet<String>
    fun setCur(curList: MutableSet<String>?)

    fun getAllTypes(): MutableSet<*>
    fun getSelTypes(): MutableSet<*>
    fun setType(typeList:  MutableSet<*>?)
}