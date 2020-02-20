package com.example.fincalc.ui.rates.currency

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fincalc.R
import com.example.fincalc.data.network.firebase.NO_NETWORK
import com.example.fincalc.models.rates.mapRatesNameIcon
import com.example.fincalc.ui.*
import com.example.fincalc.ui.rates.AdapterRecRates
import kotlinx.android.synthetic.main.fragment_crypto.*
import kotlinx.android.synthetic.main.fragment_currency.*
import kotlinx.android.synthetic.main.fragment_metals.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

@Suppress("DEPRECATION")
class RateFragment : Fragment(), CoroutineScope {

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Main + job
    private lateinit var curViewModel: CurrencyViewModel
    private lateinit var sharedPref: SharedPreferences
    private lateinit var adapter: AdapterRecRates
    private var noNetworkWarningShown: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        curViewModel = ViewModelProvider(this).get(CurrencyViewModel::class.java)
        return inflater.inflate(R.layout.fragment_currency, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvStatusCur.setFont(FONT_PATH)

        layTableCur.visibility = View.INVISIBLE
        layoutInputCur.visibility = View.INVISIBLE

        btnDateCur.setCustomSizeVector(
            context, resTop = R.drawable.ic_calendar, sizeTopdp = 24
        )
        btnBaseCur.setCustomSizeVector(
            context, resTop = R.drawable.ic_base_cur, sizeTopdp = 24
        )

        sharedPref = view.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        val base = sharedPref.getString(CURRENCY_PREF, "USD")
        curViewModel.setBaseCur(base!!)
        curViewModel.setDate(null)

        ivTransferCur.setSvgColor(context!!, R.color.PortPrimaryLight)

        adapter = AdapterRecRates(view.context, null)
        recyclerCur.setHasFixedSize(true)
        recyclerCur.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        recyclerCur.adapter = adapter

        btnDateCur.setOnClickListener { btn ->
            openCalendarHighOrderFunc(
                requireContext(), btn
            ) { dateApi -> curViewModel.setDate(dateApi) }
        }

        btnBaseCur.setOnClickListener {
            getDialogCurHighOrderFunc(requireContext()) { cur ->
                curViewModel.setBaseCur(cur)
            }
        }

        btnConvertCur.setOnClickListener {
            if (btnCurFrom.text != "") {
                val input = etCurAmountInput.text.toString()
                if (input != "")
                    curViewModel.setAmount(input.toDouble())
                else {
                    curViewModel.setAmount(null)
                    showSnackBar(R.string.invalidCurInput, btnConvertCur)
                }
            } else
                showSnackBar(R.string.invalidCurSelection, btnConvertCur)
            hideKeyboard(this.requireActivity())
        }

        btnCurBase.setOnClickListener {
            getDialogCurHighOrderFunc(requireContext()) { selCur ->
                curViewModel.setBaseCur(selCur)
            }
        }

        btnCurFrom.setOnClickListener {
            getDialogCurHighOrderFunc(requireContext()) { selCur ->
                curViewModel.setCurFrom(selCur)
                curViewModel.setAmount(1.0)
                etCurAmountInput.setText("1.0")
            }
        }

        ivTransferCur.setOnClickListener {
            if (btnCurFrom.text != "") {
                curViewModel.replaceCurrencies()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        job = Job()

        curViewModel.getConvertRates().observe(viewLifecycleOwner, Observer {

            progressBarCurFr.visibility = View.VISIBLE
            tvProgLoadCur.text = requireContext().getString(R.string.loading)

            it?.let {
                launch {
                    if (it.status == NO_NETWORK && !noNetworkWarningShown) {
                        noNetworkWarningShown = true
                        tvProgLoadCur.text = requireContext().getString(R.string.noNetwork)
                        delay(3000)
                        tvProgLoadCur.text = requireContext().getString(R.string.loadingFromCache)
                        delay(2000)
                    }

                    progressBarCurFr.visibility = View.GONE
                    layTableCur.visibility = View.VISIBLE
                    layoutInputCur.visibility = View.VISIBLE

                    adapter.ratesRows = it.ratesBarList
                    adapter.notifyDataSetChanged()

                    val date = formatterLong.format(it.date)
                    btnDateCur.text = date

                    val base = it.baseCur
                    val from = it.curFrom

                    setBaseCurToSharedPref(sharedPref, base)

                    val textCurName = "${context?.getString(R.string.base)} $base"
                    btnBaseCur.text = textCurName
                    val res = mapRatesNameIcon[base]?.second
                    res?.let { icon ->
                        btnBaseCur.setCustomSizeVector(
                            context,
                            resTop = R.drawable.ic_base_cur, sizeTopdp = 24,
                            resRight = icon, sizeRightdp = 32
                        )
                    }

                    btnCurBase.text = base
                    btnCurFrom.text = from
                    tvCurResult.text =
                        it.resAmount?.let { d -> "= " + decimalFormatter3p.format(d) + " " + base }

                    val flagBase = mapRatesNameIcon[it.baseCur]?.second
                    flagBase?.let { f ->
                        btnCurBase.setCustomSizeVector(context, resLeft = f, sizeLeftdp = 32)
                    }

                    val flagFrom = mapRatesNameIcon[it.curFrom]?.second
                    flagFrom?.let { f ->
                        btnCurFrom.setCustomSizeVector(context, resLeft = f, sizeLeftdp = 32)
                    }
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        job.cancel()
        curViewModel.removeSources()
    }
}

