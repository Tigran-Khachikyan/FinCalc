package com.my_1st.fincalc.ui.rates

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.my_1st.fincalc.R

class RatesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rates)
        val navView: BottomNavigationView = findViewById(R.id.nav_viewCur)

        val navController = findNavController(R.id.nav_host_fragment_rates)
        navView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animatoo.animateSlideDown(this)
    }
}
