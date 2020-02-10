package com.example.fincalc.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.fincalc.R
import com.example.fincalc.ui.rates.RatesActivity
import com.example.fincalc.ui.dep.DepositActivity
import com.example.fincalc.ui.loan.LoanActivity
import com.example.fincalc.ui.port.PortfolioActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btnLoanMainAc.setOnClickListener {
            val intent = Intent(this, LoanActivity::class.java)
            startActivity(intent)
            Animatoo.animateSwipeRight(this)
        }
        btnDepMainAc.setOnClickListener {
            val intent = Intent(this, DepositActivity::class.java)
            startActivity(intent)
            Animatoo.animateSlideDown(this)
        }
        btnCurMainAc.setOnClickListener {
            val intent = Intent(this, RatesActivity::class.java)
            startActivity(intent)
            Animatoo.animateSlideUp(this)
        }
        btnPortfolioMainAc.setOnClickListener {
            val intent = Intent(this, PortfolioActivity::class.java)
            startActivity(intent)
            Animatoo.animateSwipeLeft(this)
        }
    }
}


