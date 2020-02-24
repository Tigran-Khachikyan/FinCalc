package com.my_1st.fincalc.ui.port.deps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.my_1st.fincalc.R
import com.my_1st.fincalc.models.deposit.TableDep
import com.my_1st.fincalc.models.rates.mapRatesNameIcon
import com.my_1st.fincalc.ui.decimalFormatter1p
import com.my_1st.fincalc.ui.dep.AdapterRecViewDep
import com.my_1st.fincalc.ui.port.home.DEPOSIT_ID_KEY
import com.my_1st.fincalc.ui.port.home.DepFilterViewModel
import com.my_1st.fincalc.ui.showDialogRemoveBanking
import kotlinx.android.synthetic.main.fragment_deps.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class DepositFragment : Fragment(), CoroutineScope {

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Main + job
    private lateinit var depViewModel: DepViewModel
    private lateinit var depFilterViewModel: DepFilterViewModel
    private lateinit var adapter: AdapterRecViewDep

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        job = Job()
        depViewModel = ViewModelProvider(this).get(DepViewModel::class.java)
        depFilterViewModel = ViewModelProvider(this).get(DepFilterViewModel::class.java)
        return inflater.inflate(R.layout.fragment_deps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AdapterRecViewDep(null)
        recScheduleDepFr.setHasFixedSize(true)
        recScheduleDepFr.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recScheduleDepFr.adapter = adapter

        val depId = arguments?.getInt(DEPOSIT_ID_KEY)

        fabRemoveDepFr.setOnClickListener {
            depId?.let {
                showDialogRemoveBanking(fabRemoveDepFr, depFilterViewModel, false, id = it) {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }

        val loanId = arguments?.getInt(DEPOSIT_ID_KEY, 0)

        loanId?.let {
            depViewModel.getDeposit(loanId)?.observe(viewLifecycleOwner, Observer { curDep ->

                if (curDep.bank != "") {
                    val bank = requireContext().getString(R.string.bank) + ": ${curDep.bank}"
                    tvBankDepFr.text = bank
                    tvBankDepFr.visibility = View.VISIBLE
                } else
                    tvBankDepFr.visibility = View.GONE

                val sum =
                    requireContext().getString(R.string.Amount) + ": ${decimalFormatter1p.format(
                        curDep.amount
                    )} ${curDep.currency}"
                tvAmountDepFr.text = sum

                val text = requireContext().getString(curDep.frequency.id)
                val typeText = requireContext().getString(R.string.Frequency) + ": $text"
                tvFreqDepFr.text = typeText

                val term = requireContext().getString(R.string.Term_months) + ": ${curDep.months}"
                tvTermDepFr.text = term

                val rate = requireContext().getString(R.string.Interest_rate) + ": ${curDep.rate}%"
                tvRateDepFr.text = rate

                val flag = mapRatesNameIcon[curDep.currency]?.second
                flag?.let { ivCurrencyDepFr.setImageResource(flag) }

                val capitalize = curDep.capitalize

                if (!capitalize)
                    tvCapitalizedDepFr.visibility = View.GONE
                else {
                    tvCapitalizedDepFr.text = requireContext().getString(R.string.withCapitalizing)
                    tvCapitalizedDepFr.visibility = View.VISIBLE
                }
                //Tax
                val tax = requireContext().getString(R.string.Tax) + ": ${curDep.taxRate}" + "%"
                tvTaxDepFr.text = tax

                //Recycler
                val depTable = TableDep(curDep)
                tvDepFrTotalIncome.text = decimalFormatter1p.format(depTable.totalPerAfterTax)

                val effectRate =
                    decimalFormatter1p.format(depTable.effectiveRate).replace(',', '.') + "%"
                tvDepEffRateResFr.text = effectRate

                adapter.scheduleDep = depTable
                adapter.notifyDataSetChanged()

                launch {
                    delay(500)
                    progressBarDepFrag.visibility = View.GONE
                    appBarLayoutDepFrag.visibility = View.VISIBLE
                    layDepResultFrag.visibility = View.VISIBLE
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
    }
}