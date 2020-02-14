package com.example.fincalc.ui.port.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.models.credit.LoanType
import com.example.fincalc.ui.port.LoanFilter
import com.example.fincalc.ui.port.filter.Sort
import kotlinx.coroutines.launch
import java.lang.Exception

@Suppress("UNCHECKED_CAST")
class LoansFilterViewModel(application: Application) : BaseViewModel(application), FilterPref {


    //Loans
    private val _loans = repository?.getLoans()
    private val _types = MutableLiveData<MutableSet<LoanType>>()
    private val _currencies = MutableLiveData<MutableSet<String>>()
    private val _sort = MutableLiveData<Sort>()

    fun deleteLoan(loan: Loan) = viewModelScope.launch {
        repository?.deleteLoan(loan)
    }

    fun deleteAllLoans() = viewModelScope.launch {
        repository?.deleteAllLoans()
    }

    fun setSort(sort: Sort) {
        _sort.value = sort
    }

    fun changeOrder() {
        if (_sort.value == Sort.BY_RATE)
            _sort.value = Sort.BY_DATE
        else _sort.value = Sort.BY_RATE
    }


    private val _mediatorLoan = MediatorLiveData<LoanFilter>()

    fun getLoanList(): LiveData<LoanFilter> {
        viewModelScope.launch {
            _loans?.let {
                _mediatorLoan.addSource(_loans) {
                    _mediatorLoan.value =
                        combine(_loans, _types, _currencies, _sort)
                }

                _mediatorLoan.addSource(_types) {
                    _mediatorLoan.value =
                        combine(_loans, _types, _currencies, _sort)
                }
                _mediatorLoan.addSource(_currencies) {
                    _mediatorLoan.value =
                        combine(_loans, _types, _currencies, _sort)
                }
                _mediatorLoan.addSource(_sort) {
                    _mediatorLoan.value =
                        combine(_loans, _types, _currencies, _sort)
                }
            }
        }
        return _mediatorLoan
    }

    private fun combine(
        loanLD: LiveData<List<Loan>>,
        typeLD: LiveData<MutableSet<LoanType>>,
        curLD: LiveData<MutableSet<String>>,
        sortLD: LiveData<Sort>
    ): LoanFilter {

        val loans = loanLD.value
        var types = typeLD.value
        var curList = curLD.value
        val sort = sortLD.value


        //filter by type
        val filteredByType = loans?.let {
            when {
                types == null -> {
                    types = mutableSetOf()
                    it.forEach { loan -> (types as MutableSet<LoanType>).add(loan.type) }
                    it
                }
                (types as MutableSet<LoanType>).isEmpty() -> arrayListOf()
                else -> {
                    Log.d(
                        "uuuuurrib",
                        "it.filter { loan -> (types as ArrayList<LoanType>).contains(loan.type) }: ${it.filter { loan ->
                            (types as MutableSet<LoanType>).contains(loan.type)
                        }}"
                    )
                    it.filter { loan -> (types as MutableSet<LoanType>).contains(loan.type) }
                }
            }
        }

        Log.d("uuuuurrib", "filteredByType VIEW_MODEL: ${filteredByType?.size}")
//filter by Currency
        val filteredByCur = filteredByType?.let {
            when {
                curList == null -> {
                    curList = mutableSetOf()
                    it.forEach { loan -> (curList as MutableSet<String>).add(loan.currency) }
                    it
                }

                (curList as MutableSet<String>).isEmpty() -> arrayListOf()

                else -> {
                    Log.d(
                        "uuuuurrib",
                        "it.filter { loan -> (curList as MutableSet<String>).contains(loan.currency) }: ${it.filter { loan ->
                            (curList as MutableSet<String>).contains(loan.currency)
                        }}}"
                    )
                    it.filter { loan -> (curList as MutableSet<String>).contains(loan.currency) }
                }
            }
        }

        Log.d("uuuuurrib", "filteredByCur VIEW_MODEL: ${filteredByCur?.size}")

        //sort by Date
        val sorted = if (filteredByCur != null && sort != null) {
            when (sort) {
                Sort.BY_DATE -> filteredByCur.reversed()
                Sort.BY_RATE -> filteredByCur.sortedByDescending { loan -> loan.rate }
            }
        } else null


        return LoanFilter(types, sorted, curList, sort!!)
    }


    override fun getAllCur(): MutableSet<String> {
        val result = mutableSetOf<String>()
        _loans?.value?.forEach { loan -> result.add(loan.currency) }
        return result
    }

    override fun getSelCur(): MutableSet<String> {
        return _currencies.value ?: _mediatorLoan.value?.currencies!!
    }

    override fun setCur(curList: MutableSet<String>?) {
        try {
            _currencies.value = curList
        } catch (ex: Exception) {
        }
    }

    override fun getAllTypes(): MutableSet<LoanType> {
        val allPossTypes = mutableSetOf<LoanType>()
        _loans?.value?.forEach { lo -> allPossTypes.add(lo.type) }
        return allPossTypes
    }

    override fun getSelTypes(): MutableSet<LoanType> {
        return _types.value ?: _mediatorLoan.value?.loanTypeList!!
    }

    override fun setType(typeList: MutableSet<*>?) {
        Log.d("uuuuurrib", "Triggered : ${_types.value?.size}")

        // try {
        val a = typeList as MutableSet<LoanType>?
        val b = _types.value
        Log.d("uuuuurrib", "setType,  _types.value.size : ${_types.value?.size}")
        Log.d("uuuuurrib", "a : ${a}")
        Log.d("uuuuurrib", "a : ${b}")
        _types.value = typeList
    }


    fun removeSources() {
        _mediatorLoan.removeSource(_loans!!)
        _mediatorLoan.removeSource(_currencies)
        _mediatorLoan.removeSource(_types)
        _mediatorLoan.removeSource(_sort)
    }
}