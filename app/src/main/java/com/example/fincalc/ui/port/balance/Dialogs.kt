package com.example.fincalc.ui.port.balance

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import com.example.fincalc.R
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.models.credit.Formula
import com.example.fincalc.models.credit.LoanType
import com.example.fincalc.models.credit.getEnumFromSelection
import com.example.fincalc.models.credit.getLoanTypeListName
import com.example.fincalc.models.cur_met.currencyCodeList
import com.example.fincalc.models.cur_met.currencyFlagList
import com.example.fincalc.models.deposit.Frequency
import com.example.fincalc.models.deposit.getFreqFromSelec
import com.example.fincalc.ui.AdapterSpinnerRates


private const val BUTTON_DIALOG_SIZE_PRESSED = 20F
private const val BUTTON_DIALOG_SIZE_UNPRESSED = 18F

/*@SuppressLint("InflateParams")
private fun getDialFilByDepType(context: Context?, type: List<CommonType>?) {

    if (context != null) {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_filter_type, null)
        dialogBuilder.setView(dialogView)

        //Buttons

        val frequencies = arrayListOf<Frequency>()

        fun btnCheck(btn: Button) {
            btn.background = context.getDrawable(R.drawable.btncalculate)
            btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bag, 0, 0, 0)
            btn.textSize = BUTTON_DIALOG_SIZE_PRESSED
            val curEnum = getFreqFromSelec(btn.text.toString(), context)
            frequencies.add(curEnum)
        }

        fun btnUncheck(btn: Button) {
            btn.background = context.getDrawable(R.drawable.btnexpand)
            btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            btn.textSize = BUTTON_DIALOG_SIZE_UNPRESSED
            val curEnum = getFreqFromSelec(btn.text.toString(), context)
            if (frequencies.contains(curEnum))
                frequencies.remove(curEnum)
        }

        fun isBtnChecked(btn: Button): Boolean = btn.textSize == BUTTON_DIALOG_SIZE_PRESSED * 2

        val btnDialClickList = View.OnClickListener {
            val curBut = it as Button

            if (isBtnChecked(curBut))
                btnUncheck(curBut)
            else
                btnCheck(curBut)
        }

        val btnDialLoanTypeMort : Button = dialogView.findViewById(R.id.btnDialLoanTypeMort)
        val btnDialLoanTypeCar : Button = dialogView.findViewById(R.id.btnDialLoanTypeCar)
        val btnDialLoanTypeBus : Button = dialogView.findViewById(R.id.btnDialLoanTypeBus)
        val btnDialLoanTypeCons : Button = dialogView.findViewById(R.id.btnDialLoanTypeCons)
        val btnDialLoanTypeCrLines : Button = dialogView.findViewById(R.id.btnDialLoanTypeCrLines)
        val btnDialLoanTypeDepSec : Button = dialogView.findViewById(R.id.btnDialLoanTypeDepSec)
        val btnDialLoanTypeGold : Button = dialogView.findViewById(R.id.btnDialLoanTypeGold)
        val btnDialLoanTypeStud : Button = dialogView.findViewById(R.id.btnDialLoanTypeStud)
        val btnDialLoanTypeUnsecured : Button = dialogView.findViewById(R.id.btnDialLoanTypeUnsecured)
        val btnDialSelectOrClear : Button = dialogView.findViewById(R.id.btnDialSelectOrClear)

        btnDialLoanTypeMort.setOnClickListener(btnDialClickList)
        btnDialLoanTypeCar.setOnClickListener(btnDialClickList)
        btnDialLoanTypeBus.setOnClickListener(btnDialClickList)
        btnDialLoanTypeCons.setOnClickListener(btnDialClickList)
        btnDialLoanTypeCrLines.setOnClickListener(btnDialClickList)
        btnDialLoanTypeDepSec.setOnClickListener(btnDialClickList)
        btnDialLoanTypeGold.setOnClickListener(btnDialClickList)
        btnDialLoanTypeStud.setOnClickListener(btnDialClickList)
        btnDialLoanTypeUnsecured.setOnClickListener(btnDialClickList)

        fun selectAll() {
            btnCheck(btnDialLoanTypeMort)
            btnCheck(btnDialLoanTypeCons)
            btnCheck(btnDialLoanTypeCar)
            btnCheck(btnDialLoanTypeStud)
            btnCheck(btnDialLoanTypeCrLines)
            btnCheck(btnDialLoanTypeUnsecured)
            btnCheck(btnDialLoanTypeDepSec)
            btnCheck(btnDialLoanTypeGold)
            btnCheck(btnDialLoanTypeBus)
            btnCheck(btnDialSelectOrClear)
        }

        fun clear() {
            btnUncheck(btnDialLoanTypeMort)
            btnUncheck(btnDialLoanTypeCons)
            btnUncheck(btnDialLoanTypeCar)
            btnUncheck(btnDialLoanTypeStud)
            btnUncheck(btnDialLoanTypeCrLines)
            btnUncheck(btnDialLoanTypeUnsecured)
            btnUncheck(btnDialLoanTypeDepSec)
            btnUncheck(btnDialLoanTypeGold)
            btnUncheck(btnDialLoanTypeBus)
            btnUncheck(btnDialSelectOrClear)
        }

        btnDialSelectOrClear.setOnClickListener {
            val curButton = it as Button
            if (!isBtnChecked(curButton)) {
                selectAll()
                curButton.text = context.getString(R.string.CLEAR)
                curButton.background = context.getDrawable(R.drawable.btnclear)
            } else {
                clear()
                curButton.text = context.getString(R.string.SELECT_ALL)
                curButton.background = context.getDrawable(R.drawable.btnclear)
            }
        }

        //initialize
        when {
            type == null -> btnDialSelectOrClear.performClick()
            type.isEmpty() -> clear()
            else -> {
                if (type.contains(LoanType.MORTGAGE)) btnCheck(btnDialLoanTypeMort)
                else btnUncheck(btnDialLoanTypeMort)
                if (type.contains(LoanType.CONSUMER_LOAN)) btnCheck(btnDialLoanTypeCons)
                else btnUncheck(btnDialLoanTypeCons)
                if (type.contains(LoanType.CAR_LOAN)) btnCheck(btnDialLoanTypeCar)
                else btnUncheck(btnDialLoanTypeCar)
                if (type.contains(LoanType.STUDENT_LOAN)) btnCheck(btnDialLoanTypeStud)
                else btnUncheck(btnDialLoanTypeStud)
                if (type.contains(LoanType.CREDIT_LINES)) btnCheck(btnDialLoanTypeCrLines)
                else btnUncheck(btnDialLoanTypeCrLines)
                if (type.contains(LoanType.UNSECURED)) btnCheck(btnDialLoanTypeUnsecured)
                else btnUncheck(btnDialLoanTypeUnsecured)
                if (type.contains(LoanType.DEPOSIT_SECURED)) btnCheck(btnDialLoanTypeDepSec)
                else btnUncheck(btnDialLoanTypeDepSec)
                if (type.contains(LoanType.GOLD_PLEDGE_SECURED)) btnCheck(btnDialLoanTypeGold)
                else btnUncheck(btnDialLoanTypeGold)
                if (type.contains(LoanType.BUSINESS)) btnCheck(btnDialLoanTypeBus)
                else btnUncheck(btnDialLoanTypeBus)
                if (type.size < 9) btnUncheck(btnDialSelectOrClear)
                else btnCheck(btnDialSelectOrClear)
            }
        }

        //click SAVE
        dialogBuilder.setPositiveButton(
            context.getString(R.string.save)
        ) { _, _ ->

            if (frequencies.size < 9)
                tvLoanTypeFilterBalFr.visibility = View.VISIBLE
            else
                tvLoanTypeFilterBalFr.visibility = View.GONE

            balanceViewModel.setSelLoanTypeList(frequencies)
        }
        //click CANCEL
        dialogBuilder.setNegativeButton(
            getString(R.string.cancel)
        ) { _, _ -> }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }
}

@SuppressLint("InflateParams")
private fun getDialFilByLoanCur(context: Context?, curs: List<String>) {
    if (context != null) {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_filter_currency, null)
        dialogBuilder.setView(dialogView)

        val spinner: Spinner = dialogView.findViewById(R.id.spinDialFilCurr)
        spinner.adapter =
            ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, curs)
        spinner.setSelection(0)

        //click SAVE
        dialogBuilder.setPositiveButton(
            getString(R.string.save)
        ) { _, _ ->

            val selection = spinner.selectedItem.toString()
            val list = arrayListOf<String>()
            list.add(selection)
            balanceViewModel.setSelLoanCurList(list)
            tvLoanCurFilterBalFr.visibility = View.VISIBLE
        }

        //click CANCEL
        dialogBuilder.setNegativeButton(
            getString(R.string.cancel)
        ) { _, _ -> }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }
}

fun setSortByAcc(acc: Boolean?, loan: Boolean) {

    if (loan) {
        when (acc) {
            null -> tvLoanSortFilterBalFr.visibility = View.GONE
            true, false -> tvLoanSortFilterBalFr.visibility = View.VISIBLE
        }
        balanceViewModel.setSortByLoanRate(acc)
    } else {
        when (acc) {
            null -> tvDepSortFilterBalFr.visibility = View.GONE
            true, false -> tvDepSortFilterBalFr.visibility = View.VISIBLE
        }
        balanceViewModel.setSortByDepRate(acc)
    }*/

