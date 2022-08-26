package com.example.gift_for_apelsinka.activity.about

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.cache.aboutApelsinka
import com.example.gift_for_apelsinka.cache.defaultHandbook
import com.example.gift_for_apelsinka.cache.staticHandbook
import com.example.gift_for_apelsinka.db.saveHandbookToDB
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkHandbook

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

    var handbook: MutableMap<String, String>? = null
        get() {
            if(field == null) return defaultHandbook
            return field
        }

    private val KEY_ABOUT_APELSINKA = "about_apelsinka"
    private val KEY_WISH_GOODNIGHT = "wish_goodnight"
    private val KEY_TITLE_APELSINKA = "title_apelsinka"
    private val KEY_TITLE_OSCAR = "title_oscar"
    private val KEY_TITLE_LERA = "title_lera"
    private val KEY_TITLE_LEXA = "title_lexa"

    fun getImagesOfLogo() : List<Int> {
        if(imageOfLogo.value != null) return imagesOfOscar.value!!

        imageOfLogo.value = listOf(R.drawable.logo, R.drawable.logo2).shuffled()

        return imageOfLogo.value!!
    }

    fun getImageOfOscar(): List<Int> {
        if(imagesOfOscar.value != null) return imagesOfOscar.value!!

        imagesOfOscar.value = listOf(
            R.drawable.oscar, R.drawable.oscar1, R.drawable.oscar2,
            R.drawable.oscar3, R.drawable.oscar4, R.drawable.oscar5).shuffled()

        return imagesOfOscar.value!!
    }

    fun getImageOfLera(): List<Int> {
        if(imagesOfLera.value != null) return imagesOfLera.value!!

        imagesOfLera.value = listOf(R.drawable.lera_ksixa1, R.drawable.lera1, R.drawable.lera2).shuffled()

        return imagesOfLera.value!!
    }

    fun getImageOfLexa(): List<Int> {
        if(imagesOfLexa.value != null) return imagesOfLexa.value!!

        imagesOfLexa.value = listOf(R.drawable.lexa1, R.drawable.lexa2,
            R.drawable.lexa_ksixa, R.drawable.lexa_ksixa2).shuffled()

        return imagesOfLexa.value!!
    }

    private fun setWrapper(liveData: MutableLiveData<String>, text : String, KEY: String) {
        liveData.value = text
        handbook?.set(KEY, text)
    }

    private fun getWrapper(liveData: MutableLiveData<String>, KEY : String, default : String): MutableLiveData<String> {
        if(liveData.value != null) return liveData
        val text = handbook?.get(KEY)
        if(text != null) {
            liveData.value = text
            return liveData
        }
        liveData.value = default
        return liveData
    }

    fun getApelsinkaTitle():  MutableLiveData<String> {
        return getWrapper(titleApelsinka, KEY_TITLE_APELSINKA, "Про меня 🍊")
    }

    fun setApelsinkaTitle(text : String) {
        setWrapper(titleApelsinka, text, KEY_TITLE_APELSINKA)
    }

    fun getTextAboutApelsinka(): MutableLiveData<String> {
        return getWrapper(textAboutApelsinka, KEY_ABOUT_APELSINKA, defaultHandbook[KEY_ABOUT_APELSINKA].toString())
    }
    fun setTextAboutApelsinka(text : String) {
        setWrapper(textAboutApelsinka, text, KEY_ABOUT_APELSINKA)
    }

    fun getOscarTitle(): MutableLiveData<String> {
        return getWrapper(titleOscar, KEY_TITLE_OSCAR, defaultHandbook[KEY_TITLE_OSCAR].toString())
    }
    fun setOscarTitle(text : String) {
        setWrapper(titleOscar, text, KEY_TITLE_OSCAR)
    }

    fun getLeraTitle(): MutableLiveData<String> {
        return getWrapper(titleLera, KEY_TITLE_LERA, defaultHandbook[KEY_TITLE_LERA].toString())
    }
    fun setLeraTitle(text : String) {
        setWrapper(titleLera, text, KEY_TITLE_LERA)
    }

    fun getLexaTitle(): MutableLiveData<String> {
        return getWrapper(titleLexa, KEY_TITLE_LEXA, defaultHandbook[KEY_TITLE_LEXA].toString())
    }
    fun setLexaTitle(text : String) {
        setWrapper(titleLexa, text, KEY_TITLE_LEXA)
    }

    fun getTextGoodnight(): MutableLiveData<String> {
        return getWrapper(textGoodnight, KEY_WISH_GOODNIGHT, defaultHandbook[KEY_WISH_GOODNIGHT].toString())
    }
    fun setTextGoodnight(text : String) {
        setWrapper(textGoodnight, text, KEY_WISH_GOODNIGHT)
    }

    suspend fun updateHandbook(): MutableMap<String, String> {
        val dict = NetworkHandbook.getHandbook()
        staticHandbook = dict
        setApelsinkaTitle(staticHandbook[KEY_TITLE_APELSINKA].toString())
        setTextAboutApelsinka(staticHandbook[KEY_ABOUT_APELSINKA].toString())
        setOscarTitle(staticHandbook[KEY_TITLE_OSCAR].toString())
        setLeraTitle(staticHandbook[KEY_TITLE_LERA].toString())
        setLexaTitle(staticHandbook[KEY_TITLE_LEXA].toString())
        setTextGoodnight(staticHandbook[KEY_WISH_GOODNIGHT].toString())
        saveHandbookToDB(dict)
        return dict
    }
}