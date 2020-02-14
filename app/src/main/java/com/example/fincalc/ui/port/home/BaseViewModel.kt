package com.example.fincalc.ui.port.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.fincalc.data.Repository

open class BaseViewModel(application: Application): AndroidViewModel(application) {
    protected val repository = Repository.getInstance(application)
}