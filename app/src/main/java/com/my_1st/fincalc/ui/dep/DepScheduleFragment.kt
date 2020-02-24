package com.my_1st.fincalc.ui.dep

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.my_1st.fincalc.R
import com.my_1st.fincalc.data.db.dep.DepFrequencyConverter
import com.my_1st.fincalc.data.db.dep.Deposit
import com.my_1st.fincalc.models.deposit.TableDep
import com.my_1st.fincalc.models.rates.arrayCurCodes
import com.my_1st.fincalc.ui.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import kotlinx.android.synthetic.main.fragment_dep_schedule.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * A simple [Fragment] subclass.
 */
class DepScheduleFragment : Fragment(), CoroutineScope {

    private lateinit var mInterstitialAd: InterstitialAd
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Main + job
    private lateinit var adapterRec: AdapterRecViewDep
    private lateinit var depViewModel: DepositViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        mInterstitialAd = InterstitialAd(requireContext())
        mInterstitialAd.adUnitId = requireContext().getString(R.string.interstitial_ad_unit_id)
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        depViewModel = ViewModelProvider(this).get(DepositViewModel::class.java)
        return inflater.inflate(R.layout.fragment_dep_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        job = Job()

        val bundle = this.arguments
        bundle?.let {

            val amount = bundle.getLong(KEY_AMOUNT, 0)
            val months = bundle.getInt(KEY_TERM, 0)
            val rate = bundle.getFloat(KEY_RATE, 0.0F)
            val capitalize = bundle.getBoolean(KEY_CAPITALIZATION, false)
            val taxRate = bundle.getFloat(KEY_TAX_RATE, 0F)
            val freqString = bundle.getString(KEY_FREQUENCY, "")
            val frequency = DepFrequencyConverter().fromStringToEnum(freqString)

            val dep = Deposit(amount, months, rate, capitalize, taxRate, frequency)
            val scheduleDep = TableDep(dep)

            fabSaveDep.setSvgColor(requireContext(), android.R.color.white)

            adapterRec = AdapterRecViewDep(scheduleDep)
            recyclerDepReport.setHasFixedSize(true)
            recyclerDepReport.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            recyclerDepReport.adapter = adapterRec

            val effectiveRate = decimalFormatter2p.format(scheduleDep.effectiveRate).replace(',','.') + "%"
            val totalIncome = decimalFormatter1p.format(scheduleDep.totalPerAfterTax)
            tvTotalIncomeResDep.text = totalIncome
            tvEffRateResDep.text = effectiveRate

            fabSaveDep.setOnClickListener {
                showDialogSaveDeposit(requireContext(), it, dep, depViewModel)
                hideKeyboard(requireActivity())
            }
        }
    }

    //Deposit save
    @SuppressLint("InflateParams")
    private fun showDialogSaveDeposit(
        context: Context, view: View, dep: Deposit, depViewModel: DepositViewModel
    ) {
        val dialogBuilder = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_save, null)
        dialogBuilder.setView(dialogView)

        dialogBuilder.setTitle(R.string.savingOptions)
        val etBank: EditText = dialogView.findViewById(R.id.etDialBank)

        //spinner Currency
        val spinnerCur: Spinner = dialogView.findViewById(R.id.spinDialCurrency)
        val adapterSpinCur = AdapterSpinnerRates(
            context, R.layout.spinner_currencies, arrayCurCodes
        )
        adapterSpinCur.setDropDownViewResource(R.layout.spinner_currencies)
        spinnerCur.adapter = adapterSpinCur

        //spinner LoanType
        val spinnerType: Spinner = dialogView.findViewById(R.id.spinnerDialLoanType)
        spinnerType.visibility = View.GONE
        val tvSpinner: TextView = dialogView.findViewById(R.id.tvDialLoanType)
        tvSpinner.visibility = View.GONE

        //click SAVE
        dialogBuilder.setPositiveButton(
            context.getString(R.string.save)
        ) { _, _ ->

            dep.bank = etBank.text.toString()
            dep.currency = spinnerCur.selectedItem.toString()
            dep.date = formatterShort.format(Date())
            depViewModel.addDep(dep)
            showSnackBar(R.string.successSaved, view)

            launch {
                delay(1500)
                if (mInterstitialAd.isLoaded)
                    mInterstitialAd.show()
            }
        }

        //click CANCEL
        dialogBuilder.setNeutralButton(
            getString(R.string.cancel)
        ) { _, _ -> }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        alertDialog.setCustomView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
    }

}
