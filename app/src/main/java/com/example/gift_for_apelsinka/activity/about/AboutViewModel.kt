package com.example.gift_for_apelsinka.activity.about

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gift_for_apelsinka.R

class AboutViewModel : ViewModel() {
    private var textAboutApelsinka : MutableLiveData<String> = MutableLiveData()
    private var textGoodnight : MutableLiveData<String> = MutableLiveData()

    private var titleApelsinka : MutableLiveData<String> = MutableLiveData()
    private var titleOscar : MutableLiveData<String> = MutableLiveData()
    private var titleLera : MutableLiveData<String> = MutableLiveData()
    private var titleLexa : MutableLiveData<String> = MutableLiveData()

    private var imageOfLogo : MutableLiveData<List<Int>> = MutableLiveData()
    private var imagesOfOscar : MutableLiveData<List<Int>> = MutableLiveData()
    private var imagesOfLera : MutableLiveData<List<Int>> = MutableLiveData()
    private var imagesOfLexa : MutableLiveData<List<Int>> = MutableLiveData()

    private val KEY_INFO = "info"
    private val KEY_GOODNIGHT = "goodnight"
    private val KEY_TITLE_APELSINKA = "title apelsinka"
    private val KEY_TITLE_OSCAR = "title oscar"
    private val KEY_TITLE_LERA = "title lera"
    private val KEY_TITLE_LEXA = "title lexa"

    fun getImagesOfLogo() : List<Int> {
        if(imageOfLogo.value != null) return imagesOfOscar.value!!

        imageOfLogo.value = listOf(R.drawable.logo, R.drawable.logo2, R.drawable.logo3).shuffled()

        return imageOfLogo.value!!
    }

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
        return getWrapper(textAboutApelsinka, KEY_INFO, sharedPreferences,
            "Я, Быкова Ксения Александровна, " +
                "но меня ещё называют Цитрусовым Богом. Обучаюсь в Самарском университете. Также у меня есть собственный логотип.")
    }

    fun setTextAboutApelsinka(text : String, sharedPreferences : SharedPreferences) {
        setWrapper(textAboutApelsinka, text, KEY_INFO, sharedPreferences)
    }

    fun getTextGoodnight(sharedPreferences : SharedPreferences): String? {
        return getWrapper(textGoodnight, KEY_GOODNIGHT, sharedPreferences,
            "Желаю Вам спокойной ночи, 🌚" +
                "\nЧтобы не приснился Игорь в костюме горничной, \uD83D\uDC69\u200D" +
                "\nК которому пристаёт Левон \uD83D\uDD1E" +
                "\nИ Лев не спросил у них отличие между базой и базисом, \uD83C\uDD98" +
                "\nВ то время, когда их чекает Илюха со своей понамеры \uD83D\uDC41")
    }

    fun setTextGoodnight(text : String, sharedPreferences : SharedPreferences) {
        setWrapper(textGoodnight, text, KEY_GOODNIGHT, sharedPreferences)
    }

    private fun setWrapper(liveData: MutableLiveData<String>, text : String, KEY: String, sharedPreferences : SharedPreferences) {
        liveData.value = text
        sharedPreferences.edit().putString(KEY, text).apply()
    }

    private fun getWrapper(liveData: MutableLiveData<String>, KEY : String, sharedPreferences: SharedPreferences, default : String): String? {
        if(liveData.value != null) return liveData.value
        val text = sharedPreferences.getString(KEY, null)
        if(text != null) {
            liveData.value = text
            return liveData.value
        }
        liveData.value = default
        return liveData.value
    }

    fun getApelsinkaTitle(sharedPreferences: SharedPreferences): String? {
        return getWrapper(titleApelsinka, KEY_TITLE_APELSINKA, sharedPreferences, "Про меня 🍊")
    }

    fun setApelsinkaTitle(text : String, sharedPreferences: SharedPreferences) {
        setWrapper(titleApelsinka, text, KEY_TITLE_APELSINKA, sharedPreferences)
    }

    fun getOscarTitle(sharedPreferences: SharedPreferences): String? {
        return getWrapper(titleOscar, KEY_TITLE_OSCAR, sharedPreferences, "Немного меня и Оскара 🐕")
    }

    fun setOscarTitle(text : String, sharedPreferences: SharedPreferences) {
        setWrapper(titleOscar, text, KEY_TITLE_OSCAR, sharedPreferences)
    }

    fun getLeraTitle(sharedPreferences: SharedPreferences): String? {
        return getWrapper(titleLera, KEY_TITLE_LERA, sharedPreferences, "Немного меня и Леры 🍋")
    }

    fun setLeraTitle(text : String, sharedPreferences: SharedPreferences) {
        setWrapper(titleLera, text, KEY_TITLE_LERA, sharedPreferences)
    }

    fun getLexaTitle(sharedPreferences: SharedPreferences): String? {
        return getWrapper(titleLexa, KEY_TITLE_LEXA, sharedPreferences, "Немного меня и Лёши 🥨")
    }

    fun setLexaTitle(text : String, sharedPreferences: SharedPreferences) {
        setWrapper(titleLexa, text, KEY_TITLE_LEXA, sharedPreferences)
    }
}