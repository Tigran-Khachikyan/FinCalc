@file:Suppress("DEPRECATION")

package com.example.fincalc.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface.*
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
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

val decimalFormatter1p = DecimalFormat("#,###.#")
val decimalFormatter2p = DecimalFormat("#,###.##")
val decimalFormatter3p = DecimalFormat("#,###.###")
@SuppressLint("SimpleDateFormat")
val formatterCalendar = SimpleDateFormat("yyyy-MM-dd")
@SuppressLint("SimpleDateFormat")
val formatterLong = SimpleDateFormat("dd MMM yyyy HH:mm:ss")
@SuppressLint("SimpleDateFormat")
val formatterShort = SimpleDateFormat("dd MMM yyyy")

const val PRIVATE_MODE = 0
const val PREF_NAME = "Currency_Pref"
const val CURRENCY_PREF = "Currency"
const val FONT_PATH = "fonts/assassin.ttf"
const val BUTTON_DIALOG_SIZE_PRESSED = 16F
const val BUTTON_DIALOG_SIZE_UNPRESSED = 14F

fun View.trigger() {
    val anim1 = AnimationUtils.loadAnimation(this.context, R.anim.icontriggerleft)
    startAnimation(anim1)
}

fun showSnackBar(text: Int, view: View) {
    val textString = view.context.getString(text)
    val snackBar = Snackbar.make(
        view, textString, Snackbar.LENGTH_LONG
    ).setAction("Action", null)
    val sbView: View = snackBar.view
    sbView.setBackgroundColor(view.context.resources.getColor(R.color.PortPrimaryDarkVery))
    snackBar.show()
}

fun toggle(hide: Boolean, viewChild: View, viewGroup: ViewGroup) {

    val transition = Slide(Gravity.BOTTOM)
    transition.duration = 500
    transition.addTarget(viewChild)
    TransitionManager.beginDelayedTransition(viewGroup, transition)
    viewChild.visibility = if (hide) View.GONE else View.VISIBLE
}

fun hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = activity.currentFocus ?: View(activity)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}


fun AlertDialog.setCustomView() {

    window?.setBackgroundDrawableResource(R.color.colorSpinner)
    getButton(BUTTON_POSITIVE).gravity = Gravity.END
    getButton(BUTTON_NEGATIVE).gravity = Gravity.START
    getButton(BUTTON_POSITIVE).textSize = 18F
    getButton(BUTTON_NEGATIVE).textSize = 18F
    getButton(BUTTON_NEUTRAL).textSize = 18F
    getButton(BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    getButton(BUTTON_POSITIVE).setTextColor(Color.WHITE)
    getButton(BUTTON_NEUTRAL).setTextColor(Color.WHITE)
}

fun ImageView.setSvgColor(context: Context, color: Int) =
    this.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_IN)

fun openCalendarHighOrderFunc(
    context: Context, view: View, func: (String?) -> Unit
) {
    val calendar = Calendar.getInstance()
    val year = calendar[Calendar.YEAR]
    val month = calendar[Calendar.MONTH]
    val day = calendar[Calendar.DAY_OF_MONTH]
    val dialog: Dialog =
        DatePickerDialog(
            context, DatePickerDialog.OnDateSetListener { _, y, m, d ->
                val yr: String = y.toString()
                val mnt = if (m + 1 < 10) "0${m + 1}" else "${m + 1}"
                val dy = if (d < 10) "0$d" else d.toString()
                var dateForApiRequest: String? = "$yr-$mnt-$dy"

                var selectedDate = try {
                    dateForApiRequest?.let { formatterCalendar.parse(dateForApiRequest!!) }
                } catch (e: ParseException) {
                    null
                }

                selectedDate?.let {
                    if (!Date().after(selectedDate)) {
                        showSnackBar(R.string.InvalidInputCalendar, view)
                        return@OnDateSetListener
                    }
                    if (formatterCalendar.format(Date()) == dateForApiRequest) {
                        dateForApiRequest = null
                        selectedDate = null
                    }
                    func(dateForApiRequest)// in high order function
                }
            }, year, month, day
        )
    dialog.setCancelable(true)
    dialog.setOnCancelListener {
    }
    dialog.show()
}

@SuppressLint("InflateParams")
fun getDialogCurHighOrderFunc(context: Context, func: (String) -> Unit) {
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


    val text = context.getString(R.string.select)+ " " + context.getString(R.string.Currency)

    dialogBuilder.setTitle(text)
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
    alertDialog.setCustomView()
    alertDialog.window?.setBackgroundDrawableResource(R.color.colorSpinner)
    spinnerCur.performClick()
}

fun setBaseCurToSharedPref(sharedPref: SharedPreferences, baseCur: String) {
    val editor = sharedPref.edit()
    editor.putString(CURRENCY_PREF, baseCur)
    editor.apply()
}

fun TextSurface.playAnimation(textRes: Int, millSec: Int) {
    reset()
    val typeface = Typeface.createFromAsset(context.assets, FONT_PATH)
    val paint = Paint()
    paint.isAntiAlias = true
    paint.typeface = typeface
    val text = TextBuilder
        .create(context.getString(textRes))
        .setPaint(paint)
        .setSize(40f)
        .setColor(context.resources.getColor(android.R.color.white))
        .setPosition(Align.SURFACE_CENTER).build()
    play(
        Sequential(
            ShapeReveal.create(
                text, millSec, SideCut.show(Side.LEFT), false
            ),
            Parallel(
                ShapeReveal.create(
                    text, 1000, SideCut.hide(Side.LEFT), false
                )
            )
        )
    )
}

fun TextView.setFont(fontPath: String) {
    typeface = Typeface.createFromAsset(context.assets, fontPath)
}

fun Button.setCustomSizeVector(
    context: Context?,
    resLeft: Int? = null, sizeLeftdp: Int = 0, resTop: Int? = null, sizeTopdp: Int = 0,
    resRight: Int? = null, sizeRightdp: Int = 0, resBot: Int? = null, sizeBotdp: Int = 0
) {
    context?.let {
        val drawLeft = resLeft?.let { context.resources.getDrawable(it) }
        val drawTop = resTop?.let { context.resources.getDrawable(it) }
        val drawRight = resRight?.let { context.resources.getDrawable(it) }
        val drawBot = resBot?.let { context.resources.getDrawable(it) }

        drawLeft?.setCustomSizedp(context, sizeLeftdp)
        drawTop?.setCustomSizedp(context, sizeTopdp)
        drawRight?.setCustomSizedp(context, sizeRightdp)
        drawBot?.setCustomSizedp(context, sizeBotdp)

        this.setCompoundDrawables(drawLeft, drawTop, drawRight, drawBot)
    }
}

private fun Drawable.setCustomSizedp(context: Context, size: Int) {
    val pxSize = size * context.resources.displayMetrics.density.toInt()
    setBounds(0, 0, pxSize, pxSize)
}


//FILTERING
fun Button.setViewChecked(checked: Boolean, icon: Int?) {
    if (checked) {
        background = context.getDrawable(R.drawable.btn_option_checked)
        setCustomSizeVector(
            context,
            resLeft = icon,
            sizeLeftdp = 24,
            resRight = R.drawable.ic_check,
            sizeRightdp = 24
        )
        textSize = BUTTON_DIALOG_SIZE_PRESSED
        setTextColor(Color.WHITE)
    } else {
        background = context?.getDrawable(R.drawable.btn_expand)
        setCustomSizeVector(context, resLeft = icon, sizeLeftdp = 24)

        textSize = BUTTON_DIALOG_SIZE_UNPRESSED
        setTextColor(Color.BLACK)
    }
}

val Button.isChecked: Boolean
    get() = currentTextColor == Color.WHITE

