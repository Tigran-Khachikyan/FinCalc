package com.example.fincalc.ui.loan

import android.app.Application
import androidx.lifecycle.*
import com.example.fincalc.data.Repository
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.models.credit.TableLoan
import kotlinx.coroutines.launch

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.getInstance(application)

    object Container {
        val schedules = MutableLiveData<Array<TableLoan>>()

        fun setSchedule(schedules: Array<TableLoan>) {
            this.schedules.value = schedules
        }

        fun clear() {
            schedules.value = null
        }
    }

    fun getSchedules(): LiveData<Array<TableLoan>?> {
        return Container.schedules
    }

    fun addLoan(loan: Loan) = viewModelScope.launch {
        repository?.insertLoan(loan)
    }

}