package com.example.fincalc.ui.port.filter

interface FilterQuery {
    fun getExistCur(): MutableSet<String>
    fun getSelCur(): MutableSet<String>
    fun setCur(curList: MutableSet<String>)

    fun getExistTypes(): MutableSet<*>
    fun getSelTypes(): MutableSet<*>
    fun setType(typeList:  MutableSet<*>)

    fun getSortPref(): Boolean?
    fun setSortPref(asc: Boolean?)

    fun removePref(option: SearchOption)
}