package com.example.gift_for_apelsinka.activity.about

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AboutViewModelFactory(
    private val sharedPreferences: SharedPreferences) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AboutViewModel(sharedPreferences) as T
    }
}