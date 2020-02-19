package com.example.fincalc.ui.rates.currency

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.fincalc.data.Repository
import com.example.fincalc.data.network.api_cur_metal.CurMetRates
import com.example.fincalc.data.network.firebase.RatesFull
import com.example.fincalc.models.rates.*
import com.example.fincalc.ui.rates.RatesBar
import com.example.fincalc.ui.rates.metals.ResultMet
import kotlinx.coroutines.launch


class CurrencyViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application
    private val repository = Repository.getInstance(application)

    private val _convertRates = MediatorLiveData<ResultCur?>()
    private val _curBase = MutableLiveData<String>()
    private val _curFrom = MutableLiveData<String?>()
    private val _data = MutableLiveData<String?>()
    private val _amount = MutableLiveData<Double?>()
    private val _rates: LiveData<RatesFull?> = Transformations.switchMap(_data) {
        when (it) {
            null -> repository?.getLatestCur()
            else -> repository?.getHistoricCur(it)
        }
    }

    fun getConvertRates(): LiveData<ResultCur?> {
        viewModelScope.launch {
            _convertRates.addSource(_curBase) {
                _convertRates.value = combineConvert(_curBase, _curFrom, _amount, _rates)
            }
            _convertRates.addSource(_curFrom) {
                _convertRates.value = combineConvert(_curBase, _curFrom, _amount, _rates)
            }
            _convertRates.addSource(_amount) {
                _convertRates.value = combineConvert(_curBase, _curFrom, _amount, _rates)
            }
            _convertRates.addSource(_rates) {
                _convertRates.value = combineConvert(_curBase, _curFrom, _amount, _rates)
            }
        }
        return _convertRates
    }

    fun setCurFrom(curFrom: String?) {
        _curFrom.value = curFrom
    }

    fun setBaseCur(base: String) {
        _curBase.value = base
    }

    fun setAmount(amount: Double?) {
        _amount.value = amount
    }

    fun setDate(date: String?) {
        _data.value = date
    }

    private fun combineConvert(
        curBaseLiveData: LiveData<String>,
        curFromLiveData: LiveData<String?>,
        amountLiveData: LiveData<Double?>,
        ratesLiveData: LiveData<RatesFull?>
    ): ResultCur? {

        val curFrom = curFromLiveData.value
        val curBase = curBaseLiveData.value
        val amount = amountLiveData.value
        val latestRates = ratesLiveData.value?.latRates as CurMetRates?
        val elderRates = ratesLiveData.value?.elderRates as CurMetRates?
        val date = ratesLiveData.value?.dateTime

        var result: ResultCur? = null
        if (curBase != null && latestRates != null && date != null) {

            val mapLatest = getMapFromRates(latestRates)
            val mapElder = elderRates?.let { getMapFromRates(elderRates) }

            //Table--
            val ratesBarList: ArrayList<RatesBar> = arrayListOf()
            val arrayMainCur = arrayListOf("USD", "EUR", "GBP", "CNY", "RUB")
            for (code in arrayMainCur) {
                val nameInt = mapRatesNameIcon[code]?.first
                val name = nameInt?.let { app.getString(nameInt) }
                val icon = mapRatesNameIcon[code]?.second

                val rateLatest = mapLatest?.let {
                    val baseRate = it[curBase]
                    val fromRate = it[code]
                    if (baseRate != null && fromRate != null && fromRate != 0.0)
                        baseRate / fromRate else null
                }
                val rateElder = mapElder?.let {
                    val baseRate = it[curBase]
                    val fromRate = it[code]
                    if (baseRate != null && fromRate != null && fromRate != 0.0)
                        baseRate / fromRate else null
                }
                val growthRate = getGrowthRate(rateLatest, rateElder)
                if (name != null && icon != null && rateLatest != null) {
                    val rateBar = RatesBar(code, name, icon, rateLatest, growthRate)
                    ratesBarList.add(rateBar)
                }
            } //--Table

            //resText
            val resAmount = if (curFrom != null && amount != null) {
                val baseRate = mapLatest?.get(curBase)
                val fromRate = mapLatest?.get(curFrom)
                if (baseRate != null && fromRate != null && fromRate != 0.0)
                    amount * baseRate / fromRate else null
            } else null

            result = ResultCur(ratesBarList, curBase, date, curFrom, resAmount)
        }
        return result
    }

    fun replaceCurrencies() {
        val base = _curBase.value
        val from = _curFrom.value
        _curFrom.value = base
        _curBase.value = from
    }

    fun removeSources() {
        _convertRates.removeSource(_curBase)
        _convertRates.removeSource(_curFrom)
        _convertRates.removeSource(_amount)
        _convertRates.removeSource(_rates)
    }

    fun cancelLoading(){
        repository?.cancelLoading()
    }
}