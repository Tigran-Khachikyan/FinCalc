package com.example.fincalc.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.fincalc.R
import com.example.fincalc.ui.dep.DepositActivity
import com.example.fincalc.ui.loan.LoanActivity
import com.example.fincalc.ui.port.PortfolioActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        animOptions(this)

    }

    private fun animOptions(context: Context) {
        val fromLeftAppear = AnimationUtils.loadAnimation(context, R.anim.cardfromleftappear)
        val fromRightAppear = AnimationUtils.loadAnimation(context, R.anim.cardfromrightappear)

        layoutCardLoanButton.startAnimation(fromLeftAppear)
        layoutCardDepButton.startAnimation(fromRightAppear)
        layoutCardCurButton.startAnimation(fromLeftAppear)
        layoutCardPortButton.startAnimation(fromRightAppear)

        layoutCardLoanButton.setOnClickListener {
            //  setClickability(false)
            val intent = Intent(this, LoanActivity::class.java)
            startActivity(intent)
            Animatoo.animateSwipeRight(this)
        }

        layoutCardDepButton.setOnClickListener {
            //  setClickability(false)
            val intent = Intent(this, DepositActivity::class.java)
            startActivity(intent)
            Animatoo.animateInAndOut(this)
        }

        /*layoutCardCurButton.setOnClickListener {
         //   setClickability(false)
            val intent = Intent(this, CurrencyActivity::class.java)
            startActivity(intent)
            Animatoo.animateSlideDown(this)
        }*/

        layoutCardPortButton.setOnClickListener {
            //   setClickability(false)
            val intent = Intent(this, PortfolioActivity::class.java)
            startActivity(intent)
            Animatoo.animateFade(this)
        }
    }

    override fun onResume() {
        super.onResume()
        //      setClickability(true)
    }

    /*private fun setClickability(param: Boolean) {
        when (param) {
            false -> {
                layoutInsideCardLoan.isFocusable = false
                layoutInsideCardDep.isFocusable = false
                layoutInsideCardCur.isFocusable = false
                layoutInsideCardPortf.isFocusable = false

                layoutInsideCardLoan.isClickable = false
                layoutInsideCardDep.isClickable = false
                layoutInsideCardCur.isClickable = false
                layoutInsideCardPortf.isClickable = false
            }
            true -> {
                layoutInsideCardLoan.isFocusable = true
                layoutInsideCardDep.isFocusable = true
                layoutInsideCardCur.isFocusable = true
                layoutInsideCardPortf.isFocusable = true

                layoutInsideCardLoan.isClickable = true
                layoutInsideCardDep.isClickable = true
                layoutInsideCardCur.isClickable = true
                layoutInsideCardPortf.isClickable = true
            }
        }
    }*/
}


