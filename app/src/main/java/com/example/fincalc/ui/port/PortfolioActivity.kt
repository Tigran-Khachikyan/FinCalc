package com.example.fincalc.ui.port

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.fincalc.R
import com.example.fincalc.ui.port.home.HomeFragment


@Suppress("UNCHECKED_CAST")
class PortfolioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portfolio)

        supportFragmentManager.beginTransaction()
            .add(R.id.FragmentContainerPort, HomeFragment()).commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animatoo.animateSwipeRight(this)
    }
}
