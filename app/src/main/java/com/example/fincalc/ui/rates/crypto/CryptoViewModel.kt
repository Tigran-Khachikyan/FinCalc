package com.example.fincalc.ui.rates.crypto

import android.app.Application
import androidx.lifecycle.*
import com.example.fincalc.data.Repository
import com.example.fincalc.data.network.api_crypto.CryptoRates
import com.example.fincalc.data.network.api_cur_metal.CurMetRates
import com.example.fincalc.data.network.firebase.RatesFull
import com.example.fincalc.models.rates.*
import kotlinx.coroutines.launch

class CryptoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.getInstance(application)

    private val _convertRates = MediatorLiveData<ResultCrypto>()
    private val _baseCurrency = MutableLiveData<String>()
    private val _orderType = MutableLiveData<Order>()
    private val _data = MutableLiveData<String>()
    private val _ratesCrypto: LiveData<RatesFull> = Transformations.switchMap(_data) {
        if (it == null)
            repository?.getLatestCrypto()
        else
            repository?.getHistoricCrypto(it)
    }
    private val _ratesCur: LiveData<RatesFull> = Transformations.switchMap(_data) {
        if (it == null)
            repository?.getLatestCur()
        else
            repository?.getHistoricCur(it)
    }

    fun setCurrency(cur: String) {
        _baseCurrency.value = cur
    }

    fun setOrder(order: Order) {
        _orderType.value = order
    }

    fun changeOrder() {
        if (_orderType.value == Order.POPULARITY)
            _orderType.value = Order.PRICE
        else _orderType.value = Order.POPULARITY
    }

    fun setDate(date: String?) {
        _data.value = date
    }

    fun getConvertRates(): LiveData<ResultCrypto?> {
        viewModelScope.launch {
            _convertRates.addSource(_baseCurrency) {
                _convertRates.value = combine(_baseCurrency, _orderType, _ratesCrypto, _ratesCur)
            }
            _convertRates.addSource(_orderType) {
                _convertRates.value = combine(_baseCurrency, _orderType, _ratesCrypto, _ratesCur)
            }
            _convertRates.addSource(_ratesCrypto) {
                _convertRates.value = combine(_baseCurrency, _orderType, _ratesCrypto, _ratesCur)
            }
            _convertRates.addSource(_ratesCur) {
                _convertRates.value = combine(_baseCurrency, _orderType, _ratesCrypto, _ratesCur)
            }
        }
        return _convertRates
    }


    private fun combine(
        _selCur: LiveData<String>,
        _order: LiveData<Order>,
        _ratesCrypto: LiveData<RatesFull>,
        _ratesCur: LiveData<RatesFull>
    ): ResultCrypto? {

        val selCur = _selCur.value
        val order = _order.value
        val status = _ratesCrypto.value?.status?:0

        val cryptoRatesLatest = _ratesCrypto.value?.latRates as CryptoRates?
        val cryptoRatesElder = _ratesCrypto.value?.elderRates as CryptoRates?
        val curRatesLatest = _ratesCur.value?.latRates as CurMetRates?

        val curRatesElder = _ratesCur.value?.elderRates as CurMetRates?
        val baseCurForCrypto = _ratesCrypto.value?.base ?: "USD"
        val date = _ratesCrypto.value?.dateTime

        val ratesBarList: ArrayList<RatesBarCrypto>?
        var result: ResultCrypto? = null
        if (cryptoRatesLatest != null && curRatesLatest != null && order != null && selCur != null && date != null) {
            val cryptoMapLatest = getMapFromCryptoRates(cryptoRatesLatest)
            val codeCryptoLatestList = cryptoMapLatest?.keys?.toList()
            val cryptoMapElder = cryptoRatesElder?.let { getMapFromCryptoRates(cryptoRatesElder) }

            val curMapLatest = getMapFromRates(curRatesLatest)
            val curMapElder = curRatesElder?.let { getMapFromRates(curRatesElder) }
            val baseCurCryptoRateLatest = curMapLatest?.get(baseCurForCrypto)
            val selCurRateLatest = curMapLatest?.get(selCur)
            val factorLatestRates =
                if (baseCurCryptoRateLatest != null && selCurRateLatest != null && baseCurCryptoRateLatest != 0.0)
                    selCurRateLatest / baseCurCryptoRateLatest
                else null

            val baseCurCryptoRateElder = curMapElder?.get(baseCurForCrypto)
            val selCurRateElder = curMapElder?.get(selCur)

            val factorElderRates =
                if (baseCurCryptoRateElder != null && selCurRateElder != null && baseCurCryptoRateElder != 0.0)
                    selCurRateElder / baseCurCryptoRateElder
                else null

            ratesBarList = arrayListOf()
            codeCryptoLatestList?.let {
                factorLatestRates?.let {
                    for (code in codeCryptoLatestList) {
                        val name = mapCryptoNameIcon[code]?.first
                        val icon = mapCryptoNameIcon[code]?.second
                        val priceLatest = cryptoMapLatest[code]?.let { it * factorLatestRates }
                        val priceElder = factorElderRates?.let {
                            cryptoMapElder?.get(code)?.let {
                                it * factorElderRates
                            }
                        }
                        val growthRate = getGrowthRate(priceLatest, priceElder)
                        val pop = cryptoPopularMap[code]

                        val rateBar =
                            if (name != null && icon != null && priceLatest != null && pop != null)
                                RatesBarCrypto(
                                    code, name, icon, priceLatest, growthRate, pop
                                ) else null

                        rateBar?.let { ratesBarList.add(rateBar) }
                    }
                }
            }
            if (order == Order.PRICE) ratesBarList.sortByDescending { r -> r.price }
            else ratesBarList.sortBy { r -> r.pop }

            result = ResultCrypto(ratesBarList, selCur, date, status, order)
        }
        return result
    }


    fun removeSources() {
        _convertRates.removeSource(_baseCurrency)
        _convertRates.removeSource(_orderType)
        _convertRates.removeSource(_ratesCrypto)
        _convertRates.removeSource(_ratesCur)
    }
}

