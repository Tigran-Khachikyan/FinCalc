package com.example.fincalc.ui.port

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.fincalc.R

class PortfolioActivity : AppCompatActivity() {

    private lateinit var naviViewModel: NaviViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portfolio)
        val navView: BottomNavigationView = findViewById(R.id.nav_viewPort)

        val navController = findNavController(R.id.fragment_conainer_nav_host)

        navView.setupWithNavController(navController)

        naviViewModel = ViewModelProvider(this).get(NaviViewModel::class.java)
        naviViewModel.isSelectedLoan().observe(this, Observer {

            it?.let {
                if (it)
                    navController.navigate(R.id.navigation_loans)
                else
                    navController.navigate(R.id.navigation_deps)
            }
        })
        /* val onNavigationItemSelectedListener =
             BottomNavigationView.OnNavigationItemSelectedListener { item ->
                 when (item.itemId) {
                     R.id.navigation_balance -> {
                         navController.navigate(R.id.navigation_deps)
                         Log.d("nvv","navigation: ${item.itemId}")
                         return@OnNavigationItemSelectedListener true
                     }
                     R.id.navigation_loans -> {
                         navController.navigate(R.id.navigation_loans)
                         Log.d("nvv","navigation: ${item.itemId}")
                         return@OnNavigationItemSelectedListener true
                     }
                     R.id.navigation_deps -> {
                         Log.d("nvv","navigation: ${item.itemId}")
                         navController.navigate(R.id.navigation_balance)
                         return@OnNavigationItemSelectedListener true
                     }
                 }
                 false
             }

         navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)*/

    }
}


////View view = bottomNavigationView.findViewById(R.id.menu_action_item);
//                //view.performClick();

