package com.example.fincalc.ui.loan

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fincalc.models.loan.FormulaLoan
import com.example.fincalc.models.loan.ScheduleLoan

class ScheduleViewModel : ViewModel() {

    object RepoSchedule {
        val _mLDAnnuity: MutableLiveData<ScheduleLoan?> = MutableLiveData()
        val _mLDDiff: MutableLiveData<ScheduleLoan?> = MutableLiveData()
        val _mLDOver: MutableLiveData<ScheduleLoan?> = MutableLiveData()

        fun setScheduleMap(map: Map<FormulaLoan, ScheduleLoan?>?) {
            map?.let {
                _mLDAnnuity.value = map[FormulaLoan.ANNUITY]
                _mLDDiff.value = map[FormulaLoan.DIFFERENTIAL]
                _mLDOver.value = map[FormulaLoan.OVERDRAFT]
                Log.d("sww", "set  _mLDAnnuity.value: ${_mLDAnnuity.value}")
            }
        }
    }
    fun getAnnuity() = RepoSchedule._mLDAnnuity
    fun getDiff() = RepoSchedule._mLDDiff
    fun getOver() = RepoSchedule._mLDOver

}