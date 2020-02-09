@file:Suppress("DEPRECATION")

package com.example.fincalc.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
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
import com.example.fincalc.models.rates.arrayCurCodes
import com.google.android.material.snackbar.Snackbar
import su.levenetc.android.textsurface.TextBuilder
import su.levenetc.android.textsurface.TextSurface
import su.levenetc.android.textsurface.animations.*
import su.levenetc.android.textsurface.contants.Align
import su.levenetc.android.textsurface.contants.Side
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun iconTrigger(view: View) {

    val anim1 = AnimationUtils.loadAnimation(view.context, R.anim.icontriggerleft)

    view.startAnimation(anim1)
}

enum class Options {
    LOAN, DEPOSIT, CURRENCY
}

fun showSnackBar(text: Int, view: View, option: Options) {
    val textString = view.context.getString(text)
    val snackbar = Snackbar.make(
        view, textString, Snackbar.LENGTH_LONG
    ).setAction("Action", null)
    val sbView: View = snackbar.view
    val color: Int = when (option) {
        Options.LOAN -> R.color.PortPrimaryDark
        Options.DEPOSIT -> R.color.DepPrimaryDark
        Options.CURRENCY -> R.color.CurrencyPrimaryDark
    }
    sbView.setBackgroundColor(view.context.resources.getColor(color))
    Log.d("yyyyop", "SNACKBAR TRIGGERED")

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
@SuppressLint("SimpleDateFormat")
val formatterCalendar = SimpleDateFormat("yyyy-MM-dd")
@SuppressLint("SimpleDateFormat")
val formatterLong = SimpleDateFormat("dd MMM yyyy HH:mm:ss")
@SuppressLint("SimpleDateFormat")
val formatterShort = SimpleDateFormat("dd MMM yyyy")

fun ImageView.setSvgColor(context: Context, color: Int) =
    this.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_IN)

fun openCalendarHighOrderFunc(
    context: Context?,
    view: View,
    func: (String?) -> Unit
) {
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
                    var dateForApiRequest: String? = "$yr-$mnt-$dy"

                    var selectedDate = try {
                        dateForApiRequest?.let { formatterCalendar.parse(dateForApiRequest!!) }
                    } catch (e: ParseException) {
                        Log.d("qqqqqqq", "SELECTED: ${e.message}")
                        null
                    }
                    Log.d("qqqqqqq", "SELECTED: $selectedDate")

                    selectedDate?.let {
                        if (!Date().after(selectedDate)) {
                            Log.d("qqqqqqq", "BREAK:")

                            showSnackBar(
                                R.string.InvalidInputCalendar,
                                view,
                                Options.CURRENCY
                            )
                            return@OnDateSetListener
                        }

                        if (formatterCalendar.format(Date()) == dateForApiRequest) {
                            dateForApiRequest = null
                            selectedDate = null
                        }
                        Log.d("qqqqqqq", "In Cal: dateForApiRequest: $dateForApiRequest")

                        func(dateForApiRequest)// high order function
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
        val spinnerCur: Spinner = dialogView.findViewById(R.id.spinDialFilCurr)
        val adapterSpinCur = AdapterSpinnerRates(
            context, R.layout.spinner_currencies, arrayCurCodes
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
        alertDialog.window?.setBackgroundDrawableResource(R.color.colorSpinner)
        spinnerCur.performClick()
    }
}

fun setBaseCurToSharedPref(sharedPref: SharedPreferences, baseCur: String) {
    val editor = sharedPref.edit()
    editor.putString(CURRENCY_PREF, baseCur)
    editor.apply()
}

const val PRIVATE_MODE = 0
const val PREF_NAME = "Currency_Pref"
const val CURRENCY_PREF = "Currency"

private const val FONT_PATH = "fonts/splash.ttf"
fun TextSurface.playSplash(context: Context) {
    reset()
    val robotoBlack = Typeface.createFromAsset(context.assets, FONT_PATH)
    val paint = Paint()
    paint.isAntiAlias = true
    paint.typeface = robotoBlack
    val text = TextBuilder
        .create(context.getString(R.string.FinCalcSplash))
        .setPaint(paint)
        .setSize(46f)
        .setColor(context.resources.getColor(android.R.color.white))
        .setPosition(Align.SURFACE_CENTER).build()
    play(
        Sequential(
            ShapeReveal.create(
                text, 3000, SideCut.show(Side.LEFT), false
            ),
            Parallel(
                ShapeReveal.create(
                    text, 1000, SideCut.hide(Side.LEFT), false
                )
            )
        )
    )
}


















