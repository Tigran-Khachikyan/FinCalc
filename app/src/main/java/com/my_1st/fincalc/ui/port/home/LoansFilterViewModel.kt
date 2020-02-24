package com.my_1st.fincalc.ui.port.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.my_1st.fincalc.data.db.loan.Loan
import com.my_1st.fincalc.models.Banking
import com.my_1st.fincalc.models.credit.LoanType
import com.my_1st.fincalc.ui.port.LoanFilter
import com.my_1st.fincalc.ui.port.filter.FilterQuery
import com.my_1st.fincalc.ui.port.filter.Filtering
import com.my_1st.fincalc.ui.port.filter.SearchOption
import com.my_1st.fincalc.ui.port.filter.SearchOption.*
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class LoansFilterViewModel(application: Application) : BaseViewModel(application),
    FilterQuery, Filtering {

    //Loans
    private val _loans = repository?.getLoans()
    private val _types = MutableLiveData<MutableSet<LoanType>>()
    private val _currencies = MutableLiveData<MutableSet<String>>()
    private val _sort = MutableLiveData<Boolean>()
    private val _queue = MutableLiveData<MutableSet<SearchOption>>()

    fun deleteLoanById(id: Int) = viewModelScope.launch {
        repository?.deleteLoanById(id)
    }

    fun deleteAllLoans() = viewModelScope.launch {
        repository?.deleteAllLoans()
    }

    private val _mediatorLoan = MediatorLiveData<LoanFilter>()

    fun getLoanList(): LiveData<LoanFilter> {
        viewModelScope.launch {
            _loans?.let {
                _mediatorLoan.addSource(_loans) {
                    _mediatorLoan.value =
                        combine(_loans, _types, _currencies, _sort, _queue)
                }

                _mediatorLoan.addSource(_types) {
                    _mediatorLoan.value =
                        combine(_loans, _types, _currencies, _sort, _queue)
                }
                _mediatorLoan.addSource(_currencies) {
                    _mediatorLoan.value =
                        combine(_loans, _types, _currencies, _sort, _queue)
                }
                _mediatorLoan.addSource(_sort) {
                    _mediatorLoan.value =
                        combine(_loans, _types, _currencies, _sort, _queue)
                }
                _mediatorLoan.addSource(_queue) {
                    _mediatorLoan.value =
                        combine(_loans, _types, _currencies, _sort, _queue)
                }
            }
        }
        return _mediatorLoan
    }

    private fun combine(
        loanLD: LiveData<List<Loan>>, typeLD: LiveData<MutableSet<LoanType>>,
        curLD: LiveData<MutableSet<String>>, sortLD: LiveData<Boolean>,
        queueLD: MutableLiveData<MutableSet<SearchOption>>
    ): LoanFilter {

        val loans = loanLD.value
        val types = typeLD.value ?: this.getExistTypes()
        val curList = curLD.value ?: this.getExistCur()
        val sort = sortLD.value
        val filterQueue = queueLD.value ?: mutableSetOf()

        val result = filterCascade(filterQueue, loans, types, curList, sort)
        return LoanFilter(result, sort, filterQueue)
    }

    //FilterQuery
    override fun getExistCur(): MutableSet<String> {
        val result = mutableSetOf<String>()
        _loans?.value?.forEach { loan -> result.add(loan.currency) }
        return result
    }

    override fun getSelCur(): MutableSet<String> = _currencies.value ?: getExistCur()

    override fun setCur(curList: MutableSet<String>) {
        val query = _queue.value ?: mutableSetOf()
        query.add(FILTER_CURRENCY)
        _queue.value = query
        _currencies.value = curList
    }

    override fun getExistTypes(): MutableSet<LoanType> {
        val allPossTypes = mutableSetOf<LoanType>()
        _loans?.value?.forEach { lo -> allPossTypes.add(lo.type) }
        return allPossTypes
    }

    override fun getSelTypes(): MutableSet<LoanType> = _types.value ?: getExistTypes()

    override fun setType(typeList: MutableSet<*>) {
        val query = _queue.value ?: mutableSetOf()
        query.add(FILTER_TYPE)
        _queue.value = query
        _types.value = typeList as MutableSet<LoanType>
    }

    override fun getSortPref(): Boolean? = _sort.value

    override fun setSortPref(asc: Boolean?) {
        val query = _queue.value ?: mutableSetOf()
        query.add(SORT)
        _queue.value = query
        _sort.value = asc
    }

    override fun removePref(option: SearchOption) {
        _queue.value?.remove(option)
        when (option) {
            FILTER_TYPE -> _types.value = null
            FILTER_CURRENCY -> _currencies.value = null
            SORT -> _sort.value = null
        }
    }
    //Filtering

    override fun filterCascade(
        filterQueue: MutableSet<SearchOption>,
        banking: List<Banking>?, types: MutableSet<*>?,
        curs: MutableSet<String>?, sortAsc: Boolean?
    ): List<Banking>? {

        types as MutableSet<LoanType>?
        val loans = banking as List<Loan>?
        return if (loans != null) {
            val result1 = if (filterQueue.size > 0) {
                when (filterQueue.elementAt(0)) {
                    FILTER_CURRENCY -> filterByCurrency(loans, curs) as List<Loan>?
                    FILTER_TYPE -> filterByType(loans, types) as List<Loan>?
                    SORT -> sortByRate(loans, sortAsc) as List<Loan>?
                }
            } else loans
            val result2 = if (filterQueue.size > 1) {
                when (filterQueue.elementAt(1)) {
                    FILTER_CURRENCY -> filterByCurrency(result1, curs) as List<Loan>?
                    FILTER_TYPE -> filterByType(result1, types) as List<Loan>?
                    SORT -> sortByRate(result1, sortAsc) as List<Loan>?
                }
            } else result1
            val result3 = if (filterQueue.size > 2) {
                when (filterQueue.elementAt(2)) {
                    FILTER_CURRENCY -> filterByCurrency(result2, curs) as List<Loan>?
                    FILTER_TYPE -> filterByType(result2, types) as List<Loan>?
                    SORT -> sortByRate(result2, sortAsc) as List<Loan>?
                }
            } else result2
            result3
        } else null
    }

    override fun filterByType(banking: List<Banking>?, types: MutableSet<*>?): List<Banking>? {
        val loans = banking as List<Loan>?
        types as MutableSet<LoanType>?
        return loans?.let {
            when {
                types == null -> it
                types.isEmpty() -> arrayListOf()
                else -> it.filter { loan -> types.contains(loan.type) }
            }
        }
    }

    fun removeSources() {
        _mediatorLoan.removeSource(_loans!!)
        _mediatorLoan.removeSource(_currencies)
        _mediatorLoan.removeSource(_types)
        _mediatorLoan.removeSource(_sort)
        _mediatorLoan.removeSource(_queue)
    }
}