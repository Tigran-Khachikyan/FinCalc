package com.example.fincalc.ui.port

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.fincalc.R

class PortfolioActivity : AppCompatActivity() {

    private lateinit var naviViewModel: NavViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portfolio)
        val navView: BottomNavigationView = findViewById(R.id.nav_viewPort)

        val navController = findNavController(R.id.fragment_conainer_nav_host)

        navView.setupWithNavController(navController)

        naviViewModel = ViewModelProvider(this).get(NavViewModel::class.java)
        naviViewModel.isSelectedLoan().observe(this, Observer {

            it?.let {
                if (it == NavSwitcher.LOANS)
                    navController.navigate(R.id.navigation_loans)
                else
                    navController.navigate(R.id.navigation_deps)
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animatoo.animateSwipeRight(this)
    }
}