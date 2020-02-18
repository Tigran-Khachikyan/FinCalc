package com.example.fincalc.ui.port.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fincalc.data.db.dep.Deposit
import com.example.fincalc.models.Banking
import com.example.fincalc.models.deposit.Frequency
import com.example.fincalc.ui.port.DepFilter
import com.example.fincalc.ui.port.filter.FilterQuery
import com.example.fincalc.ui.port.filter.Filtering
import com.example.fincalc.ui.port.filter.SearchOption
import com.example.fincalc.ui.port.filter.SearchOption.*
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class DepFilterViewModel(application: Application) : BaseViewModel(application),
    FilterQuery, Filtering {

    private val _deposits = repository?.getDep()
    private val _frequencies = MutableLiveData<MutableSet<Frequency>>()
    private val _currencies = MutableLiveData<MutableSet<String>>()
    private val _sort = MutableLiveData<Boolean>()
    private val _queue = MutableLiveData<MutableSet<SearchOption>>()

    fun deleteDepById(id: Int) = viewModelScope.launch {
        repository?.deleteDepById(id)
    }

    fun deleteAllDep() = viewModelScope.launch {
        repository?.deleteAllDeps()
    }

    private val _mediatorDep = MediatorLiveData<DepFilter>()

    fun getDepList(): LiveData<DepFilter> {
        viewModelScope.launch {
            _deposits?.let {
                _mediatorDep.addSource(_deposits) {
                    _mediatorDep.value =
                        combine(_deposits, _frequencies, _currencies, _sort, _queue)
                }

                _mediatorDep.addSource(_frequencies) {
                    _mediatorDep.value =
                        combine(_deposits, _frequencies, _currencies, _sort, _queue)
                }
                _mediatorDep.addSource(_currencies) {
                    _mediatorDep.value =
                        combine(_deposits, _frequencies, _currencies, _sort, _queue)
                }
                _mediatorDep.addSource(_sort) {
                    _mediatorDep.value =
                        combine(_deposits, _frequencies, _currencies, _sort, _queue)
                }
                _mediatorDep.addSource(_queue) {
                    _mediatorDep.value =
                        combine(_deposits, _frequencies, _currencies, _sort, _queue)
                }
            }
        }
        return _mediatorDep
    }

    private fun combine(
        depLD: LiveData<List<Deposit>>, freqLD: LiveData<MutableSet<Frequency>>,
        curLD: LiveData<MutableSet<String>>, sortLD: LiveData<Boolean>,
        queueLD: MutableLiveData<MutableSet<SearchOption>>
    ): DepFilter {

        val depList = depLD.value
        val frequencies = freqLD.value ?: this.getExistTypes()
        val curList = curLD.value ?: getExistCur()
        val sort = sortLD.value
        val filterQueue = queueLD.value ?: mutableSetOf()

        val result = filterCascade(filterQueue, depList, frequencies, curList, sort)
        return DepFilter(frequencies, result, curList, sort, filterQueue)
    }


    //FilterQuery
    override fun getExistCur(): MutableSet<String> {
        val result = mutableSetOf<String>()
        _deposits?.value?.forEach { dep -> result.add(dep.currency) }
        return result
    }

    override fun getSelCur(): MutableSet<String> = _currencies.value ?: getExistCur()

    override fun setCur(curList: MutableSet<String>) {
        val query = _queue.value ?: mutableSetOf()
        query.add(FILTER_CURRENCY)
        _queue.value = query
        _currencies.value = curList
    }

    override fun getExistTypes(): MutableSet<Frequency> {
        val allPossTypes = mutableSetOf<Frequency>()
        _deposits?.value?.forEach { dep -> allPossTypes.add(dep.frequency) }
        return allPossTypes
    }

    override fun getSelTypes(): MutableSet<Frequency> = _frequencies.value ?: getExistTypes()

    override fun setType(typeList: MutableSet<*>) {
        val query = _queue.value ?: mutableSetOf()
        query.add(FILTER_TYPE)
        _queue.value = query
        _frequencies.value = typeList as MutableSet<Frequency>
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
            FILTER_TYPE -> _frequencies.value = null
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

        types as MutableSet<Frequency>?
        val deposits = banking as List<Deposit>?
        return if (deposits != null) {
            val result1 = if (filterQueue.size > 0) {
                when (filterQueue.elementAt(0)) {
                    FILTER_CURRENCY -> filterByCurrency(deposits, curs) as List<Deposit>?
                    FILTER_TYPE -> filterByType(deposits, types) as List<Deposit>?
                    SORT -> sortByRate(deposits, sortAsc) as List<Deposit>?
                }
            } else deposits
            val result2 = if (filterQueue.size > 1) {
                when (filterQueue.elementAt(1)) {
                    FILTER_CURRENCY -> filterByCurrency(result1, curs) as List<Deposit>?
                    FILTER_TYPE -> filterByType(result1, types) as List<Deposit>?
                    SORT -> sortByRate(result1, sortAsc) as List<Deposit>?
                }
            } else result1
            val result3 = if (filterQueue.size > 2) {
                when (filterQueue.elementAt(2)) {
                    FILTER_CURRENCY -> filterByCurrency(result2, curs) as List<Deposit>?
                    FILTER_TYPE -> filterByType(result2, types) as List<Deposit>?
                    SORT -> sortByRate(result2, sortAsc) as List<Deposit>?
                }
            } else result2
            result3
        } else null
    }

    override fun filterByType(banking: List<Banking>?, types: MutableSet<*>?): List<Banking>? {
        val deposits = banking as List<Deposit>?
        types as MutableSet<Frequency>?
        return deposits?.let {
            when {
                types == null -> it
                types.isEmpty() -> arrayListOf()
                else -> it.filter { dep -> types.contains(dep.frequency) }
            }
        }
    }

    fun removeSources() {
        _mediatorDep.removeSource(_deposits!!)
        _mediatorDep.removeSource(_currencies)
        _mediatorDep.removeSource(_frequencies)
        _mediatorDep.removeSource(_sort)
        _mediatorDep.removeSource(_queue)

    }
}