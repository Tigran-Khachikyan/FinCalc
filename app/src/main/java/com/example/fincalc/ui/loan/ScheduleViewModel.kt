package com.example.fincalc.ui.loan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fincalc.models.credit.TableLoan

class ScheduleViewModel : ViewModel() {


    object RepoSchedule {
        val scheduleAnnLiveData: MutableLiveData<TableLoan> = MutableLiveData()
        val scheduleDiffLiveData: MutableLiveData<TableLoan> = MutableLiveData()
        val scheduleOverLiveData: MutableLiveData<TableLoan> = MutableLiveData()

        fun setScheduleAnn(schedule: TableLoan) {
            scheduleAnnLiveData.value = schedule
        }

        fun setScheduleDiff(schedule: TableLoan) {
            scheduleDiffLiveData.value = schedule
        }

        fun setScheduleOver(schedule: TableLoan) {
            scheduleOverLiveData.value = schedule
        }

        fun clear(){
            scheduleAnnLiveData.value = null
            scheduleDiffLiveData.value = null
            scheduleOverLiveData.value = null
        }
    }

    fun getScheduleAnnuity(): LiveData<TableLoan> = RepoSchedule.scheduleAnnLiveData
    fun getScheduleDifferential(): LiveData<TableLoan> = RepoSchedule.scheduleDiffLiveData
    fun getScheduleOverdraft(): LiveData<TableLoan> = RepoSchedule.scheduleOverLiveData
}