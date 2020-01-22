package com.example.fincalc.ui.loan

import android.app.Application
import androidx.lifecycle.*
import com.example.fincalc.data.Repository
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.models.credit.TableLoan
import kotlinx.coroutines.launch

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.getInstance(application)

    object RepoSchedule {
        val scheduleAnnLiveData: MutableLiveData<TableLoan?> = MutableLiveData()
        val scheduleDiffLiveData: MutableLiveData<TableLoan?> = MutableLiveData()
        val scheduleOverLiveData: MutableLiveData<TableLoan?> = MutableLiveData()

        fun setScheduleAnn(schedule: TableLoan) {
            scheduleAnnLiveData.value = schedule
        }

        fun setScheduleDiff(schedule: TableLoan) {
            scheduleDiffLiveData.value = schedule
        }

        fun setScheduleOver(schedule: TableLoan) {
            scheduleOverLiveData.value = schedule
        }

        fun clear() {
            scheduleAnnLiveData.value = null
            scheduleDiffLiveData.value = null
            scheduleOverLiveData.value = null
        }
    }

    fun addLoan(loan: Loan) = viewModelScope.launch {
        repository?.insertLoan(loan)
    }

    fun getScheduleAnnuity(): LiveData<TableLoan?> = RepoSchedule.scheduleAnnLiveData
    fun getScheduleDifferential(): LiveData<TableLoan?> = RepoSchedule.scheduleDiffLiveData
    fun getScheduleOverdraft(): LiveData<TableLoan?> = RepoSchedule.scheduleOverLiveData
}