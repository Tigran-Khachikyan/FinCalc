package com.example.fincalc.ui.cur.rates

import android.app.Application
import androidx.lifecycle.*
import com.example.fincalc.data.Repository
import com.example.fincalc.data.network.firebase.RatesFull
import com.example.fincalc.models.cur_met_crypto.ConvertRates
import com.example.fincalc.models.cur_met_crypto.TableRates
import com.example.fincalc.models.cur_met_crypto.getMapFromCurRates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RatesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.getInstance(application)
    private val latCurRates = repository?.getLatestCurRates()

    private fun getLatCurRates(): LiveData<RatesFull>? = latCurRates


    //Converter
    private val _convertRates = MediatorLiveData<ConvertRates?>()
    private val _curSpin1 = MutableLiveData<String?>()
    private val _curSpin2 = MutableLiveData<String?>()
    private val _data = MutableLiveData<String?>()
    private val _amount = MutableLiveData<Double?>()
    private val _rates: LiveData<RatesFull?> = Transformations.switchMap(_data) {
        if (it == null)
            repository?.getLatestCurRates()
        else
            repository?.getHisCurRates(it)
    }

    fun getConvertRates(): LiveData<ConvertRates?> {
        CoroutineScope(Dispatchers.Default).launch {
            _convertRates.addSource(_curSpin1) {
                _convertRates.value = combineConvert(_curSpin1, _curSpin2, _amount, _rates)
            }
            _convertRates.addSource(_curSpin2) {
                _convertRates.value = combineConvert(_curSpin1, _curSpin2, _amount, _rates)
            }
            _convertRates.addSource(_amount) {
                _convertRates.value = combineConvert(_curSpin1, _curSpin2, _amount, _rates)
            }
            _convertRates.addSource(_rates) {
                _convertRates.value = combineConvert(_curSpin1, _curSpin2, _amount, _rates)
            }
        }
        return _convertRates
    }


    fun setCurrencies(curSpin1: String?, curSpin2: String?) {
        _curSpin1.value = curSpin1
        _curSpin2.value = curSpin2
    }

    fun setAmount(amount: Double?) {
        _amount.value = amount
    }

    fun setDate(date: String?) {
        _data.value = date
    }

    private fun combineConvert(
        _curSpin1: LiveData<String?>,
        _curSpin2: LiveData<String?>,
        _amount: LiveData<Double?>,
        _rates: LiveData<RatesFull?>
    ): ConvertRates? {

        val curSpin1 = _curSpin1.value
        val curSpin2 = _curSpin2.value
        val amount = _amount.value
        val rates = _rates.value?.latRates

        return if (curSpin1 != null && curSpin2 != null && amount != null && rates != null) {
            val map = getMapFromCurRates(rates)
            val curValue1 = map?.getValue(curSpin1)
            val curValue2 = map?.getValue(curSpin2)
            val resultAmount =
                if (curValue1 != null && curValue2 != null && curValue2 != 0.0)
                    amount * curValue2 / curValue1
                else null
            ConvertRates(resultAmount, rates)
        } else null
    }

    //Table
    private val latTableRates = MediatorLiveData<TableRates>()
    private val _latTableCur = MutableLiveData<String>()

    fun setLatTableCur(cur: String) {
        _latTableCur.value = cur
    }

    fun getLatTableRates(): LiveData<TableRates?> {
        viewModelScope.launch {
            latCurRates?.let {
                latTableRates.addSource(latCurRates) {
                    latTableRates.value = combineLiveData(latCurRates, _latTableCur)
                }
                latTableRates.addSource(_latTableCur) {
                    latTableRates.value = combineLiveData(latCurRates, _latTableCur)
                }
            }
        }
        return latTableRates
    }

    private fun combineLiveData(ratesUi: LiveData<RatesFull>, cur: LiveData<String>?): TableRates? {
        val rates = ratesUi.value
        val currency = cur?.value
        return if (rates != null && currency != null) TableRates(currency, rates) else null
    }

    fun removeSources() {
        _convertRates.removeSource(_curSpin1)
        _convertRates.removeSource(_curSpin2)
        _convertRates.removeSource(_amount)
        _convertRates.removeSource(_rates)

        latTableRates.removeSource(latCurRates!!)
        latTableRates.removeSource(_latTableCur)
    }


}