package com.my_1st.fincalc.ui.loan

import android.app.Application
import androidx.lifecycle.*
import com.my_1st.fincalc.data.Repository
import com.my_1st.fincalc.data.db.loan.Loan
import com.my_1st.fincalc.models.credit.TableLoan
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