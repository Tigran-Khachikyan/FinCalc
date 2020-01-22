@file:Suppress("DEPRECATION")

package com.example.fincalc.ui

import android.app.Activity
import android.graphics.Color
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import com.example.fincalc.R
import com.google.android.material.snackbar.Snackbar

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