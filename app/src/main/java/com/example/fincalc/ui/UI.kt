@file:Suppress("DEPRECATION")

package com.example.fincalc.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.fincalc.R
import com.example.fincalc.models.rates.arrayCurCode
import com.google.android.material.snackbar.Snackbar
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun iconTrigger(view: View) {

    val anim1 = AnimationUtils.loadAnimation(view.context, R.anim.icontriggerleft)

    view.startAnimation(anim1)
}

fun showSnackbar(text: String, view: View, loan: Boolean) {
    val snackbar = Snackbar.make(
        view, text, Snackbar.LENGTH_LONG
    ).setAction("Action", null)
    val sbView: View = snackbar.view
    val color: Int = if (loan) R.color.LoansPrimaryDark else R.color.DepPrimaryDark
    sbView.setBackgroundColor(view.context.resources.getColor(color))
    snackbar.show()
}

fun toggle(hide: Boolean, viewChild: View, viewGroup: ViewGroup) {

    val transition = Slide(Gravity.BOTTOM)
    transition.duration = 500
    transition.addTarget(viewChild)
    TransitionManager.beginDelayedTransition(viewGroup, transition)
    viewChild.visibility = if (hide) View.GONE else View.VISIBLE
}

fun hideKeyboard(activity: Activity) {
    val imm =
        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun customizeAlertDialog(alertDialog: AlertDialog, positiv: Boolean) {
    alertDialog.window?.setBackgroundDrawableResource(R.color.PortPrimary)
    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).gravity = Gravity.END
    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).gravity = Gravity.START
    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).textSize = 18F
    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).textSize = 18F
    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)

    if (positiv)
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN)
    else
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)

}

val decimalFormatter1p = DecimalFormat("#,###.#")
val decimalFormatter2p = DecimalFormat("#,###.##")
val decimalFormatter3p = DecimalFormat("#,###.###")

fun ImageView.setSvgColor(context: Context, color: Int) =
    this.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_IN)

@SuppressLint("SimpleDateFormat")
fun openCalendarHighOrderFunc(context: Context?, view: View, func: (String, Date) -> Unit) {
    context?.let {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        val dialog: Dialog =
            DatePickerDialog(
                context,
                DatePickerDialog.OnDateSetListener { _, y, m, d ->
                    val yr: String = y.toString()
                    val mnt = if (m + 1 < 10) "0${m + 1}" else "${m + 1}"
                    val dy = if (d < 10) "0$d" else d.toString()
                    val dateForApiRequest = "$yr-$mnt-$dy"

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                    val selectedDate = try {
                        dateFormat.parse(dateForApiRequest)
                    } catch (e: ParseException) {
                        null
                    }
                    selectedDate?.let {
                        val isValidDate = Date().after(selectedDate)

                        if (!isValidDate) {
                            showSnackbar(
                                context.getString(R.string.InvalidInputCalendar),
                                view,
                                false
                            )
                            return@OnDateSetListener
                        }

                        func(dateForApiRequest, selectedDate)// high order function
                    }

                }, year, month, day
            )
        dialog.setCancelable(true)
        dialog.setOnCancelListener {
        }
        dialog.show()
    }
}

@SuppressLint("InflateParams")
fun getDialogCurHighOrderFunc(context: Context?, func: (String) -> Unit) {
    if (context != null) {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_filter_currency, null)
        dialogBuilder.setView(dialogView)

        //spinner Currency
        val spinnerCur = dialogView.findViewById<Spinner>(R.id.spinDialFilCurr)
        val adapterSpinCur = AdapterSpinnerRates(
            context, R.layout.spinner_currencies,arrayCurCode
        )
        adapterSpinCur.setDropDownViewResource(R.layout.spinner_currencies)
        spinnerCur.adapter = adapterSpinCur
        spinnerCur.setHasTransientState(true)

        dialogBuilder.setTitle(R.string.DialogTitleCur)
        dialogBuilder.setIcon(R.mipmap.currencyicon)

        //click SAVE
        dialogBuilder.setPositiveButton(
            context.getString(R.string.OK)
        ) { _, _ ->
            val selectedCur = spinnerCur.selectedItem.toString()
            func(selectedCur)
        }

        dialogBuilder.setNegativeButton(
            context.getString(R.string.cancel)
        ) { _, _ -> }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        customizeAlertDialog(alertDialog, true)
        alertDialog.window?.setBackgroundDrawableResource(R.color.CurrencyPrimaryLight)
    }
}