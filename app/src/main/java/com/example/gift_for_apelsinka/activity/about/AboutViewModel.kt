package com.example.gift_for_apelsinka.activity.about

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gift_for_apelsinka.R

class AboutViewModel : ViewModel() {
    private var textAboutApelsinka : MutableLiveData<String> = MutableLiveData()
    private var imagesOfOscar : MutableLiveData<List<Int>> = MutableLiveData()
    private var imagesOfLera : MutableLiveData<List<Int>> = MutableLiveData()
    private var imagesOfLexa : MutableLiveData<List<Int>> = MutableLiveData()

    private val KEY_INFO = "info"

    fun getImageOfOscar(): List<Int> {
        if(imagesOfOscar.value != null) return imagesOfOscar.value!!

        imagesOfOscar.value = listOf( R.drawable.oscar, R.drawable.oscar1, R.drawable.oscar2,
            R.drawable.oscar3, R.drawable.oscar4, R.drawable.oscar5,
            R.drawable.oscar6, R.drawable.oscar7, R.drawable.oscar8, R.drawable.oscar9).shuffled()

        return imagesOfOscar.value!!
    }

    fun getImageOfLera(): List<Int> {
        if(imagesOfLera.value != null) return imagesOfLera.value!!

        imagesOfLera.value = listOf(
            R.drawable.lera_ksixa, R.drawable.lera_ksixa2, R.drawable.lera_ksixa3,
            R.drawable.lera1, R.drawable.lera2)
            .shuffled()

        return imagesOfLera.value!!
    }

    fun getImageOfLexa(): List<Int> {
        if(imagesOfLexa.value != null) return imagesOfLexa.value!!

        imagesOfLexa.value = listOf(R.drawable.lexa1, R.drawable.lexa2, R.drawable.lexa3, R.drawable.lexa4,
            R.drawable.lexa_ksixa, R.drawable.lexa_ksixa2, R.drawable.lexa_ksixa3, R.drawable.lexa_ksixa4).shuffled()

        return imagesOfLexa.value!!
    }

    fun getTextAboutApelsinka(sharedPreferences : SharedPreferences): String? {
        if(textAboutApelsinka.value != null) return textAboutApelsinka.value
        val text = sharedPreferences.getString(KEY_INFO, null)
        if(text != null) {
            textAboutApelsinka.value = text
            return textAboutApelsinka.value
        }
        textAboutApelsinka.value = "Я,авфыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыы" +
                "ыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыавфы Быкова Ксения Александровна, " +
                "но меня ещё называют Цитрусовым Богом. Обучаюсь в Самарском университете. Также у меня есть собственный логотип."
        return textAboutApelsinka.value
    }
    fun setTextAboutApelsinka(text : String, sharedPreferences : SharedPreferences) {
        textAboutApelsinka.value = text
        sharedPreferences.edit().putString(KEY_INFO, text).apply()
    }
}