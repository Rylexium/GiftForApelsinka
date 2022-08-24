package com.example.gift_for_apelsinka.activity.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.cache.defaultListOfMainPictures
import com.example.gift_for_apelsinka.cache.defaultListOfStatements
import com.example.gift_for_apelsinka.db.model.FieldPhoto
import com.example.gift_for_apelsinka.db.model.Statements
import com.example.gift_for_apelsinka.db.pictureRealization
import com.example.gift_for_apelsinka.db.statementRealization
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkPictures
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkStatements
import com.example.gift_for_apelsinka.util.Notifaction
import com.example.gift_for_apelsinka.util.WorkWithTime.getNowHour
import kotlinx.coroutines.*

class MainViewModel : ViewModel() {
    private var listOfStatements : MutableLiveData<List<Statements>> = MutableLiveData()
    private var listOfPictures : MutableLiveData<List<Any>> = MutableLiveData()
    private var greetingText : MutableLiveData<String> = MutableLiveData()

    fun getStatements(): List<Statements> = runBlocking {
        if(listOfStatements.value != null) return@runBlocking listOfStatements.value!!
        val list = async { statementRealization.getAll() }
        val res = if(list.await().isEmpty())
            defaultListOfStatements
        else
            list.getCompleted()
        listOfStatements.value = res.shuffled()
        return@runBlocking listOfStatements.value!!
    }

    fun getPictures(): List<Any> = runBlocking {
        if(listOfPictures.value != null) return@runBlocking listOfPictures.value!!
        val list = defaultListOfMainPictures()
        val task = async { pictureRealization.mainPicture() }
        if(task.await().isNotEmpty()) {
            val picturesFromDB = task.getCompleted().shuffled()
            for (item in picturesFromDB)
                list.add(item)
        }
        listOfPictures.value = list
        return@runBlocking listOfPictures.value!!
    }

    fun getImageOfTime(): Int {
        return when(getNowHour()) {
            23 -> R.drawable.ic_baseline_nights_stay_24
            in 0..4 -> R.drawable.ic_baseline_nights_stay_24
            in 5..11 -> R.drawable.ic_baseline_wb_sunny_24
            in 12..18 -> R.drawable.ic_baseline_cloud_24
            in 19..22 -> R.drawable.ic_baseline_tapas_24
            else -> 0
        }
    }

    fun getGreetingsForApelsinka(flag : Boolean): String? {
        if(greetingText.value != null && !flag) return null

        var result = ""
        when(getNowHour()) {
            23 -> result = "Доброй ночи, "
            in 0..4 -> result = "Доброй ночи, "
            in 5..11 -> {
                when((1..4).random()) {
                    1 -> result = "Доброе утро, "
                    2 -> result = "Доброе утречка, "
                    3 -> result = "Доброго утра, "
                    4 -> result = "Доброго утречка, "
                }
            }
            in 12..18 -> {
                when((1..2).random()) {
                    1 -> result = "Добрый день, "
                    2 -> result = "Добрейший денёчек, "
                }
            }
            in 19..22 -> result = "Добрый вечер, "
        }

        val value = (System.currentTimeMillis() % Notifaction.nameOfApelsinka.size).toInt()
        if(value == Notifaction.nameOfApelsinka.size)
            greetingText.value = "${result.subSequence(0, result.length - 2)}!"
        else
            greetingText.value =  result + Notifaction.nameOfApelsinka[value] + "!"

        return greetingText.value!!
    }

    suspend fun updateStatements(): List<Statements> {
        val statements = NetworkStatements.getStatements()
        for (statement in statements)
            statementRealization.insertStatement(Statements(statement.id, statement.text, statement.author))
        listOfStatements.value = statementRealization.getAll()

        return  if(listOfStatements.value == null) emptyList()
                else listOfStatements.value!!
    }

    suspend fun updateMainPictures(): List<Any> {
        val pictures = NetworkPictures.getAllMainPicture()

        val result = defaultListOfMainPictures()
        val downloadPictures = mutableListOf<FieldPhoto>()
        for(picture in pictures) {
            val fieldPhoto = FieldPhoto(picture.id, picture.picture, if(picture.title == null) "" else picture.title, picture.belongs)
            downloadPictures.add(fieldPhoto)
            pictureRealization.insertFieldPhoto(fieldPhoto)
        }
        result.addAll(downloadPictures.shuffled())
        listOfPictures.value = result

        return  if(listOfPictures.value == null) emptyList()
                else listOfPictures.value!!
    }
}