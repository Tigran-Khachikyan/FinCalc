package com.example.fincalc.ui.port

import android.app.Application
import androidx.lifecycle.*
import com.example.fincalc.data.Repository
import com.example.fincalc.data.db.dep.Deposit
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.models.Banking
import com.example.fincalc.models.credit.LoanType
import com.example.fincalc.models.deposit.Frequency
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class PortViewModel(application: Application) : AndroidViewModel(application), Filtering {

    private val repository = Repository.getInstance(application)

    //Loans
    private val _loanList = repository?.getLoans()
    private val _typeListLoan = MutableLiveData<List<LoanType>?>()
    private val _curListLoan = MutableLiveData<List<String>>()
    private val _sortAccLoan = MutableLiveData<Boolean?>()

    fun deleteLoan(loan: Loan) = viewModelScope.launch {
        repository?.deleteLoan(loan)
    }

    fun deleteAllLoans() = viewModelScope.launch {
        repository?.deleteAllLoans()
    }

    fun setSelLoanTypeList(selList: List<LoanType>?) {
        _typeListLoan.value = selList
    }

    fun setSelLoanCurList(curs: List<String>?) {
        _curListLoan.value = curs
    }

    fun setSortByLoanRate(acc: Boolean?) {
        _sortAccLoan.value = acc
    }

    private val mediatorLoan = MediatorLiveData<LoanFilter>()

    fun getLoanList(): LiveData<LoanFilter> {
        viewModelScope.launch {
            mediatorLoan.addSource(_loanList!!) {
                mediatorLoan.value =
                    combFiltersLoan(_loanList, _typeListLoan, _curListLoan, _sortAccLoan)
            }
            mediatorLoan.addSource(_typeListLoan) {
                mediatorLoan.value =
                    combFiltersLoan(_loanList, _typeListLoan, _curListLoan, _sortAccLoan)
            }
            mediatorLoan.addSource(_curListLoan) {
                mediatorLoan.value =
                    combFiltersLoan(_loanList, _typeListLoan, _curListLoan, _sortAccLoan)
            }
            mediatorLoan.addSource(_sortAccLoan) {
                mediatorLoan.value =
                    combFiltersLoan(_loanList, _typeListLoan, _curListLoan, _sortAccLoan)
            }
        }
        return mediatorLoan
    }

    private fun combFiltersLoan(
        loanList: LiveData<List<Loan>>?, filType: LiveData<List<LoanType>?>,
        filCur: LiveData<List<String>>, sortAcc: LiveData<Boolean?>
    ): LoanFilter {

        val loans = loanList?.value?.reversed()
        val types = filType.value
        val cur = filCur.value
        val isAcc = sortAcc.value
        val totalCurs = sortCur(loans, cur)

        val defLoan = filterByTypes(loans, types)
        val defLoan2 = filterByCur(defLoan, cur)
        val res = sortByRate(defLoan2, isAcc)

        return LoanFilter(types, res, totalCurs, isAcc)
    }

    private fun filterByTypes(
        loans: List<Loan>?, types: List<LoanType>?
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


    //Deposits
    private val _depList = repository?.getDep()
    private val _depFreqList = MutableLiveData<List<Frequency>?>()
    private val _curListDep = MutableLiveData<List<String>>()
    private val _sortAccDep = MutableLiveData<Boolean?>()

    fun deleteDep(dep: Deposit) = viewModelScope.launch {
        repository?.deleteDep(dep)
    }

    fun deleteAllDep() = viewModelScope.launch {
        repository?.deleteAllDeps()
    }

    fun setSelDepFreqList(selList: List<Frequency>?) {
        _depFreqList.value = selList
    }

    fun setSelDepCurList(curs: List<String>?) {
        _curListDep.value = curs
    }

    fun setSortByDepRate(acc: Boolean?) {
        _sortAccDep.value = acc
    }

    private val mediatorDep = MediatorLiveData<DepFilter>()

    fun getDepList(): LiveData<DepFilter> {
        viewModelScope.launch {
            mediatorDep.addSource(_depList!!) {
                mediatorDep.value =
                    combFiltersDep(_depList, _depFreqList, _curListDep, _sortAccDep)
            }
            mediatorDep.addSource(_depFreqList) {
                mediatorDep.value =
                    combFiltersDep(_depList, _depFreqList, _curListDep, _sortAccDep)
            }
            mediatorDep.addSource(_curListDep) {
                mediatorDep.value =
                    combFiltersDep(_depList, _depFreqList, _curListDep, _sortAccDep)
            }
            mediatorDep.addSource(_sortAccDep) {
                mediatorDep.value =
                    combFiltersDep(_depList, _depFreqList, _curListDep, _sortAccDep)
            }
        }
        return mediatorDep
    }

    private fun combFiltersDep(
        depList: LiveData<List<Deposit>>?, freq: LiveData<List<Frequency>?>,
        curs: LiveData<List<String>>, sortAcc: LiveData<Boolean?>
    ): DepFilter {

        val dep = depList?.value?.reversed()

        val freqList = freq.value
        val cur = curs.value
        val isAcc = sortAcc.value
        val totalCurs = sortCur(dep, cur)
        val def = filterByFreq(dep, freqList)
        val defLoan2 = filterByCur(def, cur)
        val res = sortByRate(defLoan2, isAcc)

        return DepFilter(freqList, prodList = res, curList = totalCurs, sortByAcc = isAcc)
    }

    private fun filterByFreq(
        depList: List<Deposit>?, freq: List<Frequency>?
    ): List<Deposit>? {

        return if (depList != null)
            return when {
                freq == null -> depList
                freq.isEmpty() -> arrayListOf()
                else -> {
                    val inner = arrayListOf<Deposit>()
                    for (dep in depList)
                        for (fr in freq)
                            if (dep.frequency == fr) {
                                inner.add(dep)
                                break
                            }
                    inner
                }
            }
        else null
    }

    override fun filterByCur(products: List<Banking>?, cur: List<String>?): List<Banking>? {
        return if (products != null) {
            return if (cur == null) products else {
                val inner = arrayListOf<Banking>()
                for (lo in products)
                    if (lo.currency == cur[0])
                        inner.add(lo)
                inner
            }
        } else null
    }

    override fun sortCur(products: List<Banking>?, curs: List<String>?): List<String>? {
        return if (products != null) {
            if (curs != null) {
                val newCurList = ArrayList<String>()
                newCurList.add(curs[0])
                for (pr in products)
                    if (pr.currency != curs[0])
                        newCurList.add(pr.currency)
                newCurList.distinct()
            } else {
                val newCurList = arrayListOf<String>()
                for (pr in products)
                    newCurList.add(pr.currency)
                newCurList.distinct()
            }
        } else null
    }

    override fun sortByRate(product: List<Banking>?, acc: Boolean?): List<Banking>? {
        return if (product != null)
            return when (acc) {
                null -> product
                true -> product.sortedBy { l -> l.rate }
                else -> product.sortedByDescending { l -> l.rate }
            }
        else null
    }

    fun removeSources() {
        mediatorLoan.removeSource(_loanList!!)
        mediatorLoan.removeSource(_curListLoan)
        mediatorLoan.removeSource(_typeListLoan)
        mediatorLoan.removeSource(_sortAccLoan)

        mediatorDep.removeSource(_depList!!)
        mediatorDep.removeSource(_depFreqList)
        mediatorDep.removeSource(_curListDep)
        mediatorDep.removeSource(_sortAccDep)
    }

}






