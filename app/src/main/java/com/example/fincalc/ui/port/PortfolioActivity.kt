package com.example.fincalc.ui.port

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.fincalc.R
import com.example.fincalc.data.db.dep.Deposit
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.models.Banking
import com.example.fincalc.models.credit.LoanType
import com.example.fincalc.models.credit.getLoanTypeFromString
import com.example.fincalc.models.deposit.Frequency
import com.example.fincalc.models.deposit.getFreqFromString
import com.example.fincalc.ui.*
import com.example.fincalc.ui.dep.DepositActivity
import com.example.fincalc.ui.loan.LoanActivity
import com.example.fincalc.ui.port.home.HomeFragment
import com.example.fincalc.ui.port.loans.LoansFragment
import com.nightonke.boommenu.BoomButtons.BoomButton
import com.nightonke.boommenu.OnBoomListenerAdapter
import kotlinx.android.synthetic.main.activity_portfolio.*




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
