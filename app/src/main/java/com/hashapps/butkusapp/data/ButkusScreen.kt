package com.hashapps.butkusapp.data

import androidx.annotation.StringRes
import com.hashapps.butkusapp.R

/** All screens of the app */
enum class ButkusScreen(@StringRes val title: Int) {
    Encode(title = R.string.encode),
    Decode(title = R.string.decode),
    Settings(title = R.string.settings),
}