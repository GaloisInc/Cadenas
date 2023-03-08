package com.hashapps.butkusapp.ui.models

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.butkusapp.Butkus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ButkusViewModel(app: Application) : AndroidViewModel(app) {
    var butkusInitialized by mutableStateOf(false)
        private set

    val encode = EncodeViewModel()
    val decode = DecodeViewModel()

    // Since this is an AndroidViewModel, we can access application context
    // It's probably worth figuring out if we can avoid this, since it goes
    // against recommended practices
    init {
        viewModelScope.launch(Dispatchers.IO) {
            Butkus.initialize(app.applicationContext)
            butkusInitialized = true
        }
    }
}