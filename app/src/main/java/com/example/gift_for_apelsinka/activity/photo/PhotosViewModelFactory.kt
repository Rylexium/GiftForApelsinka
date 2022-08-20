package com.example.gift_for_apelsinka.activity.photo

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PhotosViewModelFactory(private val sharedPreferences: SharedPreferences) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PhotosViewModel(sharedPreferences) as T
    }
}