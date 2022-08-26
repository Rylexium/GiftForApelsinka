package com.example.gift_for_apelsinka.activity.about

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gift_for_apelsinka.cache.*
import com.example.gift_for_apelsinka.db.model.FieldPhoto
import com.example.gift_for_apelsinka.db.model.Handbook
import com.example.gift_for_apelsinka.db.pictureRealization
import com.example.gift_for_apelsinka.db.saveHandbookToDB
import com.example.gift_for_apelsinka.db.savePicturesToDB
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkHandbook
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkPictures
import kotlinx.coroutines.*

class AboutViewModel : ViewModel() {
    private var textAboutApelsinka : MutableLiveData<String> = MutableLiveData()
    private var textGoodnight : MutableLiveData<String> = MutableLiveData()

    private var titleApelsinka : MutableLiveData<String> = MutableLiveData()
    private var titleOscar : MutableLiveData<String> = MutableLiveData()
    private var titleLera : MutableLiveData<String> = MutableLiveData()
    private var titleLexa : MutableLiveData<String> = MutableLiveData()

    private var imageOfLogo : MutableLiveData<List<Any>> = MutableLiveData()
    private var imagesOfOscar : MutableLiveData<List<Any>> = MutableLiveData()
    private var imagesOfLera : MutableLiveData<List<Any>> = MutableLiveData()
    private var imagesOfLexa : MutableLiveData<List<Any>> = MutableLiveData()

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

    fun getImagesOfLogo() : MutableLiveData<List<Any>> = runBlocking {
        if(imageOfLogo.value != null) return@runBlocking imagesOfOscar

        val listFromDB = async { pictureRealization.logoPicture() }
        imageOfLogo.value = if(listFromDB.await().isNotEmpty()) union(defaultPicturesLogo(), listFromDB.getCompleted()) else defaultPicturesLogo()

        return@runBlocking imageOfLogo
    }

    fun getImageOfOscar(): MutableLiveData<List<Any>> = runBlocking {
        if(imagesOfOscar.value != null) return@runBlocking imagesOfOscar

        val listFromDB = async { pictureRealization.oscarPicture() }
        imagesOfOscar.value = if(listFromDB.await().isNotEmpty()) union(defaultPicturesOscar(), listFromDB.getCompleted()) else defaultPicturesOscar()

        return@runBlocking imagesOfOscar
    }

    fun getImageOfLera(): MutableLiveData<List<Any>> = runBlocking {
        if(imagesOfLera.value != null) return@runBlocking imagesOfLera

        val listFromDB = async { pictureRealization.leraPicture() }
        imagesOfLera.value = if(listFromDB.await().isNotEmpty()) union(defaultPicturesLera(), listFromDB.getCompleted()) else defaultPicturesLera()

        return@runBlocking imagesOfLera
    }

    fun getImageOfLexa(): MutableLiveData<List<Any>> = runBlocking {
        if(imagesOfLexa.value != null) return@runBlocking imagesOfLexa

        val listFromDB = async { pictureRealization.rylexiumPicture() }
        imagesOfLexa.value = if(listFromDB.await().isNotEmpty()) union(defaultPicturesLexa(), listFromDB.getCompleted()) else defaultPicturesLexa()

        return@runBlocking imagesOfLexa
    }

    private fun setWrapper(liveData: MutableLiveData<String>, text : String, KEY: String) {
        liveData.value = text
        handbook?.set(KEY, text)
        CoroutineScope(Dispatchers.IO).launch { NetworkHandbook.postHandbook(KEY, text) }
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
    suspend fun updatePhotos() {
        val logoPicture = NetworkPictures.getAllLogoPicture()
        savePicturesToDB(logoPicture)
        imageOfLogo.value = union(defaultPicturesLogo(), pictureRealization.logoPicture()).shuffled()

        val oscarPicture = NetworkPictures.getAllOscarPicture()
        savePicturesToDB(oscarPicture)
        imagesOfOscar.value = union(defaultPicturesOscar(), pictureRealization.oscarPicture()).shuffled()

        val leraPicture = NetworkPictures.getAllLeraPicture()
        savePicturesToDB(leraPicture)
        imagesOfLera.value = union(defaultPicturesLera(), pictureRealization.leraPicture()).shuffled()

        val lexaPicture = NetworkPictures.getAllRylexiumPicture()
        savePicturesToDB(lexaPicture)
        imagesOfLexa.value = union(defaultPicturesLexa(), pictureRealization.rylexiumPicture()).shuffled()
    }
    private fun union(list1: MutableList<Any>, list2: List<FieldPhoto>) : MutableList<Any> {
        list1.addAll(list2)
        return list1
    }
}