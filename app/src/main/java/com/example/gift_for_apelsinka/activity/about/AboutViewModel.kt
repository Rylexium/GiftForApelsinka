package com.example.gift_for_apelsinka.activity.about

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gift_for_apelsinka.cache.*
import com.example.gift_for_apelsinka.db.*
import com.example.gift_for_apelsinka.db.model.FieldPhoto
import com.example.gift_for_apelsinka.db.model.Handbook
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkHandbook
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkPictures
import com.example.gift_for_apelsinka.util.DebugFunctions
import com.example.gift_for_apelsinka.util.wrapperNextPictures
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

    var handbook: MutableMap<String, String> = mutableMapOf()

    private val KEY_ABOUT_APELSINKA = "about_apelsinka"
    private val KEY_WISH_GOODNIGHT = "wish_goodnight"
    private val KEY_TITLE_APELSINKA = "title_apelsinka"
    private val KEY_TITLE_OSCAR = "title_oscar"
    private val KEY_TITLE_LERA = "title_lera"
    private val KEY_TITLE_LEXA = "title_lexa"

    companion object {
        var pageOfLogo = 0
        var pageOfOscar = 0
        var pageOfLera = 0
        var pageOfLexa = 0
    }

    fun getImagesOfLogo() : MutableLiveData<List<Any>> = runBlocking {
        DebugFunctions.addDebug("AboutViewModel","getImagesOfLogo")
        if(imageOfLogo.value != null) return@runBlocking imagesOfOscar

        val listFromDB = async { pictureRealization.logoPicture() }
        imageOfLogo.value = if(listFromDB.await().isNotEmpty()) union(defaultPicturesLogo(), listFromDB.getCompleted()).shuffled() else defaultPicturesLogo().shuffled()

        return@runBlocking imageOfLogo
    }

    fun getImageOfOscar(): MutableLiveData<List<Any>> = runBlocking {
        DebugFunctions.addDebug("AboutViewModel","getImageOfOscar")
        if(imagesOfOscar.value != null) return@runBlocking imagesOfOscar

        val listFromDB = async { pictureRealization.oscarPicture() }
        imagesOfOscar.value = if(listFromDB.await().isNotEmpty()) union(defaultPicturesOscar(), listFromDB.getCompleted()).shuffled() else defaultPicturesOscar().shuffled()

        return@runBlocking imagesOfOscar
    }

    fun getImageOfLera(): MutableLiveData<List<Any>> = runBlocking {
        DebugFunctions.addDebug("AboutViewModel","getImageOfLera")
        if(imagesOfLera.value != null) return@runBlocking imagesOfLera

        val listFromDB = async { pictureRealization.leraPicture() }
        imagesOfLera.value = if(listFromDB.await().isNotEmpty()) union(defaultPicturesLera(), listFromDB.getCompleted()).shuffled() else defaultPicturesLera().shuffled()

        return@runBlocking imagesOfLera
    }

    fun getImageOfLexa(): MutableLiveData<List<Any>> = runBlocking {
        DebugFunctions.addDebug("AboutViewModel","getImageOfLexa")
        if(imagesOfLexa.value != null) return@runBlocking imagesOfLexa

        val listFromDB = async { pictureRealization.rylexiumPicture() }
        imagesOfLexa.value = if(listFromDB.await().isNotEmpty()) union(defaultPicturesLexa(), listFromDB.getCompleted()).shuffled() else defaultPicturesLexa().shuffled()

        return@runBlocking imagesOfLexa
    }

    private fun setWrapper(liveData: MutableLiveData<String>, text : String, KEY: String) {
        DebugFunctions.addDebug("AboutViewModel","setWrapper")
        var textTmp = text
        if(textTmp == "")
            textTmp = "<Пусто>"
        liveData.value = textTmp
        handbook[KEY] = textTmp
        CoroutineScope(Dispatchers.IO).launch {
            handbookRealization.insertHandbook(Handbook(KEY, text)) // в бд
            NetworkHandbook.postHandbook(KEY, text) //в сеть
        }
    }

    private fun getWrapper(liveData: MutableLiveData<String>, KEY : String, default : String): MutableLiveData<String> {
        DebugFunctions.addDebug("AboutViewModel","getWrapper")
        if(liveData.value != null) return liveData
        var text = handbook[KEY]
        if(text != null) {
            if(text == "") text = "<Пусто>"
            liveData.value = text
            return liveData
        }
        liveData.value = default
        return liveData
    }

    fun getApelsinkaTitle():  MutableLiveData<String> {
        DebugFunctions.addDebug("AboutViewModel","getApelsinkaTitle")
        return getWrapper(titleApelsinka, KEY_TITLE_APELSINKA, defaultHandbook[KEY_TITLE_APELSINKA].toString())
    }

    private fun setApelsinkaTitle(text : String) {
        DebugFunctions.addDebug("AboutViewModel","setApelsinkaTitle")
        setWrapper(titleApelsinka, text, KEY_TITLE_APELSINKA)
    }

    fun getTextAboutApelsinka(): MutableLiveData<String> {
        DebugFunctions.addDebug("AboutViewModel","getTextAboutApelsinka")
        return getWrapper(textAboutApelsinka, KEY_ABOUT_APELSINKA, defaultHandbook[KEY_ABOUT_APELSINKA].toString())
    }
    fun setTextAboutApelsinka(text : String) {
        DebugFunctions.addDebug("AboutViewModel","setTextAboutApelsinka")
        setWrapper(textAboutApelsinka, text, KEY_ABOUT_APELSINKA)
    }

    fun getOscarTitle(): MutableLiveData<String> {
        DebugFunctions.addDebug("AboutViewModel","getOscarTitle")
        return getWrapper(titleOscar, KEY_TITLE_OSCAR, defaultHandbook[KEY_TITLE_OSCAR].toString())
    }
    fun setOscarTitle(text : String) {
        DebugFunctions.addDebug("AboutViewModel","setOscarTitle")
        setWrapper(titleOscar, text, KEY_TITLE_OSCAR)
    }

    fun getLeraTitle(): MutableLiveData<String> {
        DebugFunctions.addDebug("AboutViewModel","getLeraTitle")
        return getWrapper(titleLera, KEY_TITLE_LERA, defaultHandbook[KEY_TITLE_LERA].toString())
    }
    fun setLeraTitle(text : String) {
        DebugFunctions.addDebug("AboutViewModel","setLeraTitle")
        setWrapper(titleLera, text, KEY_TITLE_LERA)
    }

    fun getLexaTitle(): MutableLiveData<String> {
        DebugFunctions.addDebug("AboutViewModel","getLexaTitle")
        return getWrapper(titleLexa, KEY_TITLE_LEXA, defaultHandbook[KEY_TITLE_LEXA].toString())
    }
    fun setLexaTitle(text : String) {
        DebugFunctions.addDebug("AboutViewModel","setLexaTitle")
        setWrapper(titleLexa, text, KEY_TITLE_LEXA)
    }

    fun getTextGoodnight(): MutableLiveData<String> {
        DebugFunctions.addDebug("AboutViewModel","getTextGoodnight")
        return getWrapper(textGoodnight, KEY_WISH_GOODNIGHT, defaultHandbook[KEY_WISH_GOODNIGHT].toString())
    }
    fun setTextGoodnight(text : String) {
        DebugFunctions.addDebug("AboutViewModel","setTextGoodnight")
        setWrapper(textGoodnight, text, KEY_WISH_GOODNIGHT)
    }

    suspend fun updateHandbook(): MutableMap<String, String> {
        DebugFunctions.addDebug("AboutViewModel","updateHandbook")
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
        DebugFunctions.addDebug("AboutViewModel","updatePhotos")
        val logoPicture = NetworkPictures.getAllLogoPicture(0, 100)
        deletePicturesLogoFromDB()
        savePicturesToDB(logoPicture)
        var picturesFromDB = pictureRealization.logoPicture()
        imageOfLogo.value = union(defaultPicturesLogo(), picturesFromDB.shuffled())
        pageOfLogo = picturesFromDB.size / 10

        val oscarPicture = NetworkPictures.getAllOscarPicture(0, 100)
        deletePicturesOscarFromDB()
        savePicturesToDB(oscarPicture)
        picturesFromDB = pictureRealization.oscarPicture()
        imagesOfOscar.value = union(defaultPicturesOscar(), picturesFromDB.shuffled())
        pageOfOscar = picturesFromDB.size / 10

        val leraPicture = NetworkPictures.getAllLeraPicture(0, 100)
        deletePicturesLeraFromDB()
        savePicturesToDB(leraPicture)
        picturesFromDB = pictureRealization.leraPicture()
        imagesOfLera.value = union(defaultPicturesLera(), picturesFromDB.shuffled())
        pageOfLera = picturesFromDB.size / 10

        val lexaPicture = NetworkPictures.getAllRylexiumPicture(0, 100)
        deletePicturesRylexiumFromDB()
        savePicturesToDB(lexaPicture)
        picturesFromDB = pictureRealization.rylexiumPicture()
        imagesOfLexa.value = union(defaultPicturesLexa(), picturesFromDB.shuffled())
        pageOfLexa = picturesFromDB.size / 10
    }
    private fun union(list1: MutableList<Any>, list2: List<FieldPhoto>) : MutableList<Any> {
        list1.addAll(list2)
        return list1
    }

    suspend fun nextPicturesOscar(context : Context): Boolean {
        DebugFunctions.addDebug("AboutViewModel","nextPicturesOscar")
        val res = wrapperNextPictures(
            { NetworkPictures.getAllOscarPicture(it) },
            { pictureRealization.oscarPicture() },
            pageOfOscar, imagesOfOscar, defaultPicturesOscar(), context)
        pageOfLogo = res.first
        return res.second
    }

    suspend fun nextPicturesLogo(context : Context): Boolean {
        DebugFunctions.addDebug("AboutViewModel","nextPicturesLogo")
        val res = wrapperNextPictures(
            { NetworkPictures.getAllLogoPicture(it) },
            { pictureRealization.logoPicture() },
            pageOfLogo, imageOfLogo, defaultPicturesLogo(), context)
        pageOfLogo = res.first
        return res.second
    }

    suspend fun nextPicturesLera(context : Context): Boolean {
        DebugFunctions.addDebug("AboutViewModel","nextPicturesLera")
        val res = wrapperNextPictures(
            { NetworkPictures.getAllLeraPicture(it) },
            { pictureRealization.leraPicture() },
            pageOfLera, imagesOfLera, defaultPicturesLera(), context)
        pageOfLera = res.first
        return res.second
    }

    suspend fun nextPicturesLexa(context : Context): Boolean {
        DebugFunctions.addDebug("AboutViewModel","nextPicturesLexa")
        val res = wrapperNextPictures(
            { NetworkPictures.getAllRylexiumPicture(it) },
            { pictureRealization.rylexiumPicture() },
            pageOfLexa, imagesOfLexa, defaultPicturesLexa(), context)
        pageOfLexa = res.first
        return res.second
    }
}