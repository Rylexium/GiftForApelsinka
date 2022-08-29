package com.example.gift_for_apelsinka.activity.about

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gift_for_apelsinka.cache.*
import com.example.gift_for_apelsinka.db.handbookRealization
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
        if(imageOfLogo.value != null) return@runBlocking imagesOfOscar

        val listFromDB = async { pictureRealization.logoPicture() }
        imageOfLogo.value = if(listFromDB.await().isNotEmpty()) union(defaultPicturesLogo(), listFromDB.getCompleted()).shuffled() else defaultPicturesLogo().shuffled()

        return@runBlocking imageOfLogo
    }

    fun getImageOfOscar(): MutableLiveData<List<Any>> = runBlocking {
        if(imagesOfOscar.value != null) return@runBlocking imagesOfOscar

        val listFromDB = async { pictureRealization.oscarPicture() }
        imagesOfOscar.value = if(listFromDB.await().isNotEmpty()) union(defaultPicturesOscar(), listFromDB.getCompleted()).shuffled() else defaultPicturesOscar().shuffled()

        return@runBlocking imagesOfOscar
    }

    fun getImageOfLera(): MutableLiveData<List<Any>> = runBlocking {
        if(imagesOfLera.value != null) return@runBlocking imagesOfLera

        val listFromDB = async { pictureRealization.leraPicture() }
        imagesOfLera.value = if(listFromDB.await().isNotEmpty()) union(defaultPicturesLera(), listFromDB.getCompleted()).shuffled() else defaultPicturesLera().shuffled()

        return@runBlocking imagesOfLera
    }

    fun getImageOfLexa(): MutableLiveData<List<Any>> = runBlocking {
        if(imagesOfLexa.value != null) return@runBlocking imagesOfLexa

        val listFromDB = async { pictureRealization.rylexiumPicture() }
        imagesOfLexa.value = if(listFromDB.await().isNotEmpty()) union(defaultPicturesLexa(), listFromDB.getCompleted()).shuffled() else defaultPicturesLexa().shuffled()

        return@runBlocking imagesOfLexa
    }

    private fun setWrapper(liveData: MutableLiveData<String>, text : String, KEY: String) {
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
        return getWrapper(titleApelsinka, KEY_TITLE_APELSINKA, "Про меня 🍊")
    }

    private fun setApelsinkaTitle(text : String) {
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
        val logoPicture = NetworkPictures.getAllLogoPicture(0)
        savePicturesToDB(logoPicture)
        var picturesFromDB = pictureRealization.logoPicture()
        imageOfLogo.value = union(defaultPicturesLogo(), picturesFromDB.shuffled())
        pageOfLogo = picturesFromDB.size / 10

        val oscarPicture = NetworkPictures.getAllOscarPicture(0)
        savePicturesToDB(oscarPicture)
        picturesFromDB = pictureRealization.oscarPicture()
        imagesOfOscar.value = union(defaultPicturesOscar(), picturesFromDB.shuffled())
        pageOfOscar = picturesFromDB.size / 10

        val leraPicture = NetworkPictures.getAllLeraPicture(0)
        savePicturesToDB(leraPicture)
        picturesFromDB = pictureRealization.leraPicture()
        imagesOfLera.value = union(defaultPicturesLera(), picturesFromDB.shuffled())
        pageOfLera = picturesFromDB.size / 10

        val lexaPicture = NetworkPictures.getAllRylexiumPicture(0)
        savePicturesToDB(lexaPicture)
        picturesFromDB = pictureRealization.rylexiumPicture()
        imagesOfLexa.value = union(defaultPicturesLexa(), picturesFromDB.shuffled())
        pageOfLexa = picturesFromDB.size / 10
    }
    private fun union(list1: MutableList<Any>, list2: List<FieldPhoto>) : MutableList<Any> {
        list1.addAll(list2)
        return list1
    }

    suspend fun nextPicturesOscar(): Boolean {
        var picturesFromNetwork : List<FieldPhoto>
        var picturesFromBD : List<FieldPhoto>
        while(true) {
            picturesFromNetwork = NetworkPictures.getAllOscarPicture(pageOfOscar)
            val previosSize = pictureRealization.oscarPicture().size
            savePicturesToDB(picturesFromNetwork.shuffled())
            picturesFromBD = pictureRealization.oscarPicture()

            if(picturesFromNetwork.size == 10)
                pageOfOscar += 1

            if(previosSize != picturesFromBD.size) break //таких нет, надо отобразить
            if(picturesFromNetwork.size < 10) break //пришло меньше 10 -> это конец
        }
        if(picturesFromNetwork.isEmpty()) return false

        val result = imagesOfOscar.value as MutableList

        if(picturesFromNetwork.map { it.id }.subtract(picturesFromBD.map { it.id }.toSet()).isNotEmpty() ||
            result.subtract(picturesFromNetwork.toSet()).size == defaultPicturesOscar().size) {
            result.addAll(picturesFromBD)
            imagesOfOscar.value = result.distinct()
        }
        if(picturesFromNetwork.size < 10) return false
        return true
    }

    suspend fun nextPicturesLogo(): Boolean {
        var picturesFromNetwork : List<FieldPhoto>

        var picturesFromBD : List<FieldPhoto>

        while(true) {
            picturesFromNetwork = NetworkPictures.getAllLogoPicture(pageOfLogo)
            val previosSize = pictureRealization.logoPicture().size
            savePicturesToDB(picturesFromNetwork.shuffled())
            picturesFromBD = pictureRealization.logoPicture()

            if(picturesFromNetwork.size == 10)
                pageOfLogo += 1

            if(previosSize != picturesFromBD.size) break //таких нет, надо отобразить
            if(picturesFromNetwork.size < 10) break //пришло меньше 10 -> это конец
        }
        if(picturesFromNetwork.isEmpty()) return false

        val result = imageOfLogo.value as MutableList

        if(picturesFromNetwork.map { it.id }.subtract(picturesFromBD.map { it.id }.toSet()).isNotEmpty() ||
            result.subtract(picturesFromNetwork.toSet()).size == defaultPicturesLogo().size) {
            result.addAll(picturesFromBD)
            imageOfLogo.value = result.distinct()
        }
        if(picturesFromNetwork.size < 10) return false
        return true
    }

    suspend fun nextPicturesLera(): Boolean {
        var picturesFromNetwork : List<FieldPhoto>

        var picturesFromBD : List<FieldPhoto>

        while(true) {
            picturesFromNetwork = NetworkPictures.getAllLeraPicture(pageOfLera)
            val previosSize = pictureRealization.leraPicture().size
            savePicturesToDB(picturesFromNetwork.shuffled())
            picturesFromBD = pictureRealization.leraPicture()

            if(picturesFromNetwork.size == 10)
                pageOfLogo += 1

            if(previosSize != picturesFromBD.size) break //таких нет, надо отобразить
            if(picturesFromNetwork.size < 10) break //пришло меньше 10 -> это конец
        }
        if(picturesFromNetwork.isEmpty()) return false

        val result = imagesOfLera.value as MutableList

        if(picturesFromNetwork.map { it.id }.subtract(picturesFromBD.map { it.id }.toSet()).isNotEmpty() ||
            result.subtract(picturesFromNetwork.toSet()).size == defaultPicturesLera().size) {
            result.addAll(picturesFromBD)
            imagesOfLera.value = result.distinct()
        }
        if(picturesFromNetwork.size < 10) return false
        return true
    }

    suspend fun nextPicturesLexa(): Boolean {
        var picturesFromNetwork : List<FieldPhoto>

        var picturesFromBD : List<FieldPhoto>

        while(true) {
            picturesFromNetwork = NetworkPictures.getAllRylexiumPicture(pageOfLera)
            val previosSize = pictureRealization.rylexiumPicture().size
            savePicturesToDB(picturesFromNetwork.shuffled())
            picturesFromBD = pictureRealization.rylexiumPicture()

            if(picturesFromNetwork.size == 10)
                pageOfLogo += 1

            if(previosSize != picturesFromBD.size) break //таких нет, надо отобразить
            if(picturesFromNetwork.size < 10) break //пришло меньше 10 -> это конец
        }
        if(picturesFromNetwork.isEmpty()) return false

        val result = imagesOfLexa.value as MutableList

        if(picturesFromNetwork.map { it.id }.subtract(picturesFromBD.map { it.id }.toSet()).isNotEmpty() ||
            result.subtract(picturesFromNetwork.toSet()).size == defaultPicturesLexa().size) {
            result.addAll(picturesFromBD)
            imagesOfLexa.value = result.distinct()
        }
        if(picturesFromNetwork.size < 10) return false
        return true
    }
}