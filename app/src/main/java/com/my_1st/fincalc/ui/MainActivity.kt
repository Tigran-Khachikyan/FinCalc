package com.my_1st.fincalc.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.my_1st.fincalc.R
import com.my_1st.fincalc.ui.dep.DepositActivity
import com.my_1st.fincalc.ui.loan.LoanActivity
import com.my_1st.fincalc.ui.port.PortfolioActivity
import com.my_1st.fincalc.ui.rates.RatesActivity
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this)

        tvStatusMain.setFont(FONT_PATH)
        btnLoanMainAc.setFont(FONT_PATH)
        btnDepMainAc.setFont(FONT_PATH)
        btnCurMainAc.setFont(FONT_PATH)
        btnPortfolioMainAc.setFont(FONT_PATH)

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


