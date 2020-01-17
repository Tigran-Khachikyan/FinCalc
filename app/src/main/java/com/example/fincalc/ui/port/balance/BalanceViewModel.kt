package com.example.fincalc.ui.port.balance

import android.app.Application
import androidx.lifecycle.*
import com.example.fincalc.data.db.Loan
import com.example.fincalc.data.db.LoanType
import com.example.fincalc.data.repository.Repository
import kotlinx.coroutines.*

class BalanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.getInstance(application)
    private val _loanList = repository?.getLoans()
    private val _filTypeList = RepoLoansFilter._typeList
    private val _filCurrency = RepoLoansFilter._cur
    private val _sortedAcc = RepoLoansFilter._sortByRate
    fun getLoans() = repository?.getLoans()

    fun getLoansByType(type: LoanType) = repository?.getLoansByType(type)

    fun getLoanById(id: Int) = repository?.getLoanById(id)

    fun addLoan(loan: Loan) = viewModelScope.launch {
        repository?.insertLoan(loan)
    }

    fun deleteLoan(loan: Loan) = viewModelScope.launch {
        repository?.deleteLoan(loan)
    }

    fun deleteAllLoans() = viewModelScope.launch {
        repository?.deleteAllLoans()
    }

    fun setLTypeSelec(selList: List<LoanType>?) {
        RepoLoansFilter._typeList.value = selList
    }

    fun setCurrencySelec(curs: List<String>?) {
        RepoLoansFilter._cur.value = curs
    }

    fun setSortedByRateAcc(acc: Boolean?) {
        RepoLoansFilter._sortByRate.value = acc
    }


    private val medLiveData = MediatorLiveData<LoanFilter>()

    fun getLoanList(): LiveData<LoanFilter> {
        viewModelScope.launch {
            medLiveData.addSource(_loanList!!) {
                medLiveData.value =
                    combFiltersLoan(_loanList, _filTypeList, _filCurrency, _sortedAcc)
            }
            medLiveData.addSource(_filTypeList) {
                medLiveData.value =
                    combFiltersLoan(_loanList, _filTypeList, _filCurrency, _sortedAcc)
            }
            medLiveData.addSource(_filCurrency) {
                medLiveData.value =
                    combFiltersLoan(_loanList, _filTypeList, _filCurrency, _sortedAcc)
            }
            medLiveData.addSource(_sortedAcc) {
                medLiveData.value =
                    combFiltersLoan(_loanList, _filTypeList, _filCurrency, _sortedAcc)
            }
        }
        return medLiveData
    }

    private fun combFiltersLoan(
        loanList: LiveData<List<Loan>>?, filtType: LiveData<List<LoanType>?>,
        filCur: LiveData<List<String>>, sortAcc: LiveData<Boolean?>
    ): LoanFilter {

        val loans = loanList?.value as ArrayList<Loan>?
        val types = filtType.value as ArrayList<LoanType>?
        val cur = filCur.value
        val isAcc = sortAcc.value
        val totalCurs = sortCur(loans, cur)


        val defLoan = filterByTypes(loans, types) as ArrayList?
        val defLoan2 = filterByCur(defLoan, cur) as ArrayList?
        val res = sortByRate(defLoan2, isAcc)

        return LoanFilter(res, types, totalCurs, isAcc)
    }


    private fun filterByTypes(
        loans: ArrayList<Loan>?, types: ArrayList<LoanType>?
    ): List<Loan>? {

        return if (loans != null)
            return when {
                types == null -> loans
                types.isEmpty() -> arrayListOf()
                else -> {
                    val inner = arrayListOf<Loan>()
                    for (lo in loans)
                        for (t in types)
                            if (lo.type == t) {
                                inner.add(lo)
                                break
                            }
                    inner
                }
            }
        else null
    }

    private fun filterByCur(loans: ArrayList<Loan>?, cur: List<String>?): List<Loan>? {

        return if (loans != null) {
            return if (cur == null) loans else {
                val inner = arrayListOf<Loan>()
                for (lo in loans)
                    if (lo.currency == cur[0])
                        inner.add(lo)
                inner
            }
        } else null
    }

    private fun sortCur(loans: ArrayList<Loan>?, curs: List<String>?): List<String>? {

        return if (loans != null) {
            if (curs != null) {
                val newCurList = arrayListOf<String>()
                newCurList.add(curs[0])
                for (lo in loans)
                    if (lo.currency != curs[0])
                        newCurList.add(lo.currency)
                newCurList.distinct()
            } else {
                val newCurList = arrayListOf<String>()
                for (lo in loans)
                    newCurList.add(lo.currency)
                newCurList.distinct()
            }
        } else null
    }

    private fun sortByRate(loans: ArrayList<Loan>?, acc: Boolean?): List<Loan>? {
        return if (loans != null)
            return when (acc) {
                null -> loans
                true -> loans.sortedBy { l -> l.queryLoan.rate }
                else -> loans.sortedByDescending { l -> l.queryLoan.rate }
            }
        else null
    }


    fun removeSources() {
        medLiveData.removeSource(_loanList!!)
        medLiveData.removeSource(_filCurrency)
        medLiveData.removeSource(_filTypeList)
        medLiveData.removeSource(_sortedAcc)
    }

}






