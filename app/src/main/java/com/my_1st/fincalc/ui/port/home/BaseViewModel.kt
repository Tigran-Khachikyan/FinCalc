package com.my_1st.fincalc.ui.port.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.my_1st.fincalc.data.Repository

open class BaseViewModel(application: Application): AndroidViewModel(application) {
    protected val repository = Repository.getInstance(application)
}