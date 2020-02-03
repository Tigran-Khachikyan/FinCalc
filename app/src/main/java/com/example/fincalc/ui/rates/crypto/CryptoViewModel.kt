package com.example.fincalc.ui.rates.crypto

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.fincalc.data.Repository
import com.example.fincalc.data.network.api_crypto.CryptoRates
import com.example.fincalc.data.network.api_currency.CurRates
import com.example.fincalc.data.network.firebase.RatesFull
import com.example.fincalc.models.rates.*
import com.example.fincalc.ui.rates.RatesBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CryptoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.getInstance(application)

    private val _convertRates = MediatorLiveData<List<RatesBar>?>()
    private val _latestCurRates = repository?.getLatestCur()
    private val _latestCryptoRates = repository?.getLatestCrypto()
    private val _baseCurrency = MutableLiveData<String>()
    private val _orderType = MutableLiveData<Boolean>()
    private val _data = MutableLiveData<String>()
    private val _ratesCrypto: LiveData<RatesFull> = Transformations.switchMap(_data) {
        if (it == null)
            _latestCryptoRates
        else
            repository?.getHistoricCrypto(it)
    }
    private val _ratesCur: LiveData<RatesFull> = Transformations.switchMap(_data) {
        if (it == null)
            _latestCurRates
        else
            repository?.getHistoricCur(it)
    }

    fun setCurrency(cur: String) {
        _baseCurrency.value = cur
    }

    fun setOrder(byPrice: Boolean) {
        _orderType.value = byPrice
    }

    fun setDate(date: String?) {
        _data.value = date
    }

    fun getConvertRates(): LiveData<List<RatesBar>?> {
        CoroutineScope(Dispatchers.Main).launch {
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
        _orderBy: LiveData<Boolean>,
        _ratesCrypto: LiveData<RatesFull>,
        _ratesCur: LiveData<RatesFull>
    ): List<RatesBar>? {

        val selCur = _selCur.value
        val orderByPrice = _orderBy.value

        val cryptoRatesLatest = _ratesCrypto.value?.latRates as CryptoRates?
        val cryptoRatesElder = _ratesCrypto.value?.elderRates as CryptoRates?
        val curRatesLatest = _ratesCur.value?.latRates as CurRates?
        Log.d("derdd", "_ratesCur.value?.elderRates: ${_ratesCur.value?.elderRates}")

        val curRatesElder = _ratesCur.value?.elderRates as CurRates?
        val baseCurForCrypto = _ratesCrypto.value?.base ?: "USD"

        var result: ArrayList<RatesBar>? = null

        if (cryptoRatesLatest != null && curRatesLatest != null && orderByPrice != null && selCur != null) {
            val cryptoMapLatest = getMapFromCryptoRates(cryptoRatesLatest)
            val codeCryptoLatestList = cryptoMapLatest?.keys?.toList()
            val cryptoMapElder = cryptoRatesElder?.let { getMapFromCryptoRates(cryptoRatesElder) }

            val curMapLatest = getMapFromCurRates(curRatesLatest)
            Log.d("derdd", "curRatesElder: $curRatesElder")

            val curMapElder = curRatesElder?.let { getMapFromCurRates(curRatesElder) }

            val baseCurCryptoRateLatest = curMapLatest?.get(baseCurForCrypto)
            val selCurRateLatest = curMapLatest?.get(selCur)
            val factorLatestRates =
                if (baseCurCryptoRateLatest != null && selCurRateLatest != null && baseCurCryptoRateLatest != 0.0)
                    selCurRateLatest / baseCurCryptoRateLatest
                else null

            Log.d("derdd", "curMapElder: $curMapElder")
            Log.d(
                "derdd",
                "curMapElder?.get(baseCurForCrypto): ${curMapElder?.get(baseCurForCrypto)}"
            )


            val baseCurCryptoRateElder = curMapElder?.get(baseCurForCrypto)
            val selCurRateElder = curMapElder?.get(selCur)
            Log.d("derdd", "baseCurCryptoRateElder: $baseCurCryptoRateElder")
            Log.d("derdd", "selCurRateElder: $selCurRateElder")

            val factorElderRates =
                if (baseCurCryptoRateElder != null && selCurRateElder != null && baseCurCryptoRateElder != 0.0)
                    selCurRateElder / baseCurCryptoRateElder
                else null

            result = arrayListOf()
            codeCryptoLatestList?.let {
                factorLatestRates?.let {
                    for (code in codeCryptoLatestList) {
                        val name = cryptoNameMap[code]
                        val icon = cryptoIconMap[code]
                        val priceLatest = cryptoMapLatest[code]?.let { it * factorLatestRates }
                        Log.d("derdd", "factorElderRates: $factorElderRates")

                        val priceElder = factorElderRates?.let {
                            Log.d("derdd", "cryptoMapElder: $cryptoMapElder")
                            Log.d(
                                "derdd",
                                "cryptoMapElder?.get(code): ${cryptoMapElder?.get(code)}"
                            )
                            Log.d(
                                "derdd",
                                "cryptoMapElder?.get(code): ${cryptoMapElder?.get(code)}"
                            )

                            cryptoMapElder?.get(code)?.let {
                                it * factorElderRates
                            }
                        }
                        val growthRate = getGrowthRate(priceLatest, priceElder)
                        val pop = cryptoPopularMap[code]

                        val rateBar =
                            if (name != null && icon != null && priceLatest != null && pop != null)
                                RatesBar(code, name, icon, priceLatest, growthRate, pop) else null

                        rateBar?.let { result.add(rateBar) }
                    }
                }
            }
            if (orderByPrice)
                result.sortByDescending { r -> r.price }
            else //by popularity
                result.sortBy { r -> r.popularity }
        }
        return result
    }
    /*val mapCur = getMapFromCurRates(curRatesLatest)
    val valueBaseApi = mapCur?.get(baseFromApi)
    val valueSelCur = mapCur?.get(base)
    if (valueSelCur != null && valueBaseApi != null && valueBaseApi != 0.0) {

        val factor = valueSelCur / valueBaseApi //478 AMD /USD

        if (codeCryptoLatestList != null) {


            for (code in codeCryptoLatestList) {*/

    /*  val name = cryptoNameMap[code]
      val icon = cryptoIconMap[code]
      val price = mapCrypto[co] * factor
      val growth = 0.5F
      if (name != null && icon != null) {
          val ratesRow = RatesRow(RateIntro(code, name, icon), price, growth)
          result.add(ratesRow)
      }*/
}

