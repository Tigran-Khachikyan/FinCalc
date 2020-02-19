package com.example.fincalc.ui.rates.metals

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.fincalc.data.Repository
import com.example.fincalc.data.network.api_cur_metal.CurMetRates
import com.example.fincalc.data.network.firebase.RatesFull
import com.example.fincalc.models.rates.*
import com.example.fincalc.ui.rates.RatesBar
import kotlinx.coroutines.launch


class MetalsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application
    private val repository = Repository.getInstance(application)

    private val _convertRates = MediatorLiveData<ResultMet>()
    private val _baseCurrency = MutableLiveData<String>()
    private val _unitType = MutableLiveData<MetalsUnit>()
    private val _data = MutableLiveData<String?>()
    private val _rates: LiveData<RatesFull> = Transformations.switchMap(_data) {
        Log.d("qqqqqqq", "IT TRIGGERED SWITCHED MAP $it")

        if (it == null)
            repository?.getLatestCur()
        else
            repository?.getHistoricCur(it)
    }

    fun setCurrency(cur: String) {
        _baseCurrency.value = cur
    }

    fun changeUnit() {
        if (_unitType.value == MetalsUnit.GRAM)
            _unitType.value = MetalsUnit.TROY_OUNCE
        else _unitType.value = MetalsUnit.GRAM
    }

    fun setDate(date: String?) {
        _data.value = date
    }

    fun getConvertRates(): LiveData<ResultMet> {
        viewModelScope.launch {
            _convertRates.addSource(_baseCurrency) {
                _convertRates.value = combine(_baseCurrency, _unitType, _rates)
            }
            _convertRates.addSource(_unitType) {
                _convertRates.value = combine(_baseCurrency, _unitType, _rates)
            }
            _convertRates.addSource(_rates) {
                _convertRates.value = combine(_baseCurrency, _unitType, _rates)
            }
        }
        return _convertRates
    }


    private fun combine(
        _selCur: LiveData<String>,
        _unit: LiveData<MetalsUnit>,
        _rates: LiveData<RatesFull>
    ): ResultMet? {

        Log.d("qqqqqqq", "COMBINE")


        val selCur = _selCur.value
        val unit = _unit.value

        val ratesLatest = _rates.value?.latRates as CurMetRates?
        val date = _rates.value?.dateTime
        Log.d("qqqqqqq", "COMBINE  $date")

        Log.d("derdd", "_ratesCur.value?.elderRates: ${_rates.value?.elderRates}")

        val ratesElder = _rates.value?.elderRates as CurMetRates?
        val baseCurForRates = _rates.value?.base ?: "USD"

        var result: ResultMet? = null
        if (ratesLatest != null && unit != null && selCur != null && date != null) {

            val mapLatest = getMapFromRates(ratesLatest)
            val mapElder = ratesElder?.let { getMapFromRates(ratesElder) }
            val codeRatesLatestList = mapLatest?.keys?.toList()

            Log.d("derdd", "curRatesElder: $ratesElder")

            val baseCurRateLatest = mapLatest?.get(baseCurForRates)
            val selCurRateLatest = mapLatest?.get(selCur)
            val factorLatestRates =
                if (baseCurRateLatest != null && selCurRateLatest != null && baseCurRateLatest != 0.0)
                    selCurRateLatest / baseCurRateLatest
                else null

            val baseCurRateElder = mapElder?.get(baseCurForRates)
            val selCurRateElder = mapElder?.get(selCur)


            val factorElderRates =
                if (baseCurRateElder != null && selCurRateElder != null && baseCurRateElder != 0.0)
                    selCurRateElder / baseCurRateElder
                else null

            val ratesBarList: ArrayList<RatesBar> = arrayListOf()
            codeRatesLatestList?.let {
                factorLatestRates?.let {
                    for (code in codeRatesLatestList) {
                        if (code != "XAU" && code != "XAG" && code != "XPD" && code != "XPT")
                            continue
                        val nameInt = mapRatesNameIcon[code]?.first
                        val name = nameInt?.let { app.getString(nameInt) }
                        val icon = mapRatesNameIcon[code]?.second
                        val priceLatest = mapLatest[code]?.let { factorLatestRates / it }
                        val priceElder = factorElderRates?.let {
                            mapElder?.get(code)?.let { factorElderRates / it }
                        }
                        val growthRate = getGrowthRate(priceLatest, priceElder)
                        val rateBar =
                            if (name != null && icon != null && priceLatest != null)
                                RatesBar(
                                    code,
                                    name,
                                    icon,
                                    priceLatest,
                                    growthRate
                                )
                            else null

                        rateBar?.let { ratesBarList.add(rateBar) }
                    }
                }
            }
            Log.d("derdd", "isUnitOunce VM: $unit")
            Log.d("derdd", "dare VM: $date")

            if (unit == MetalsUnit.GRAM)
                ratesBarList.forEach { r -> r.price = r.price / MetalsUnit.TROY_OUNCE.weight }
            result = ResultMet(ratesBarList, selCur, date, unit)
            Log.d("qqqqqqq", "DATE HAS BEEN CHANGED $date")

        }
        return result
    }

    fun removeSources() {
        _convertRates.removeSource(_baseCurrency)
        _convertRates.removeSource(_unitType)
        _convertRates.removeSource(_rates)
    }

    fun cancelLoading(){
        repository?.cancelLoading()
    }
}

