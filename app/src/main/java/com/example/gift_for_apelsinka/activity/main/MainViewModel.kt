package com.example.gift_for_apelsinka.activity.main

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.cache.defaultHandbook
import com.example.gift_for_apelsinka.cache.defaultListOfMainPictures
import com.example.gift_for_apelsinka.cache.defaultListOfStatements
import com.example.gift_for_apelsinka.cache.staticHandbook
import com.example.gift_for_apelsinka.db.*
import com.example.gift_for_apelsinka.db.model.Statements
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkHandbook
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkPictures
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkStatements
import com.example.gift_for_apelsinka.util.Notifaction
import com.example.gift_for_apelsinka.util.WorkWithTime.getNowHour
import kotlinx.coroutines.*

class MainViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private var mapOfHandbook : MutableLiveData<Map<String, String>> = MutableLiveData()
    private var listOfStatements : MutableLiveData<List<Statements>> = MutableLiveData()
    private var listOfPictures : MutableLiveData<List<Any>> = MutableLiveData()
    private var greetingText : MutableLiveData<String> = MutableLiveData()
    companion object {
        var KEY_PAGE_OF_PICTURE = "pageOfPicture"
        var KEY_PAGE_OF_STATEMENTS = "pageOfStatements"
    }

    fun getStatements(): MutableLiveData<List<Statements>> = runBlocking {
        if(listOfStatements.value != null) return@runBlocking listOfStatements
        val list = async { statementRealization.getAll() }
        val res = if(list.await().isEmpty())
            defaultListOfStatements
        else
            list.getCompleted()
        listOfStatements.value = res.shuffled()
        return@runBlocking listOfStatements
    }

    fun getPictures(): MutableLiveData<List<Any>> = runBlocking {
        if(listOfPictures.value != null) return@runBlocking listOfPictures
        val list = defaultListOfMainPictures()
        val task = async { pictureRealization.mainPicture() }
        if(task.await().isNotEmpty()) {
            val picturesFromDB = task.getCompleted().shuffled()
            for (item in picturesFromDB)
                list.add(item)
        }
        listOfPictures.value = list
        return@runBlocking listOfPictures
    }

    fun getHandbook(): MutableLiveData<Map<String, String>> = runBlocking {
        if(mapOfHandbook.value != null) return@runBlocking mapOfHandbook
        val task = async { handbookRealization.allHandbook() }
        val res =
            if(task.await().isEmpty()) defaultHandbook
            else task.getCompleted()
        staticHandbook = res
        mapOfHandbook.value = res
        return@runBlocking mapOfHandbook
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
            23 -> result = "Доброй ночи"
            in 0..4 -> result = "Доброй ночи"
            in 5..11 -> {
                when((1..4).random()) {
                    1 -> result = "Доброе утро"
                    2 -> result = "Доброе утречка"
                    3 -> result = "Доброго утра"
                    4 -> result = "Доброго утречка"
                }
            }
            in 12..18 -> {
                when((1..2).random()) {
                    1 -> result = "Добрый день"
                    2 -> result = "Добрейший денёчек"
                }
            }
            in 19..22 -> result = "Добрый вечер"
        }

        val value = (System.currentTimeMillis() % Notifaction.nameOfApelsinka.size).toInt()
        if(value == Notifaction.nameOfApelsinka.size - 1)
            greetingText.value = "$result!"
        else
            greetingText.value =  result + ", " + Notifaction.nameOfApelsinka[value] + "!"

        return greetingText.value!!
    }

    suspend fun updateStatements() : Boolean {
        val statementsFromNetwork = NetworkStatements.getStatements(0, 1000)

        val listFromDb = statementRealization.getAll()

        var isEquals = true
        for(item in listFromDb) {
            var count = 0
            for(item1 in statementsFromNetwork) {
                if (item.id == item1.id)
                    break
                count += 1
            }
            if(count == statementsFromNetwork.size) {
                isEquals = false
                break
            }
        }
        if(!isEquals) {
            deleteStatementsFromDB()
            saveStatementsToDB(statementsFromNetwork)
            listOfStatements.value = statementRealization.getAll().shuffled()
        }
        return !isEquals //true была новая запись
    }

    suspend fun nextStatements() : Boolean {
        var page = sharedPreferences.getInt(KEY_PAGE_OF_STATEMENTS, 0)
        var statementsFromNetwork: List<Statements>

        while(true) {
            statementsFromNetwork = NetworkStatements.getStatements(page)
            val previosSize = statementRealization.getAll().size
            saveStatementsToDB(statementsFromNetwork)
            val nowSize = statementRealization.getAll().size

            if(statementsFromNetwork.size == 10)
                page += 1

            if(previosSize != nowSize) break //таких нет, надо отобразить
            if(statementsFromNetwork.size < 10) break //пришло меньше 10 -> это конец
        }
        if(statementsFromNetwork.isEmpty()) return false
        sharedPreferences.edit().putInt(KEY_PAGE_OF_STATEMENTS, page).apply()

        val result = listOfStatements.value as MutableList

        result.addAll(statementRealization.getAll())

        listOfStatements.value = result.distinct()
        if(statementsFromNetwork.size < 10) return false
        return true
    }

    suspend fun nextMainPictures() : Boolean {
        var page = sharedPreferences.getInt(KEY_PAGE_OF_PICTURE, 0)
        val picturesFromNetwork = NetworkPictures.getAllMainPicture(page)
        if(picturesFromNetwork.isEmpty()) return false
        if(picturesFromNetwork.size == 10) {
            page += 1
            sharedPreferences.edit().putInt(KEY_PAGE_OF_PICTURE, page).apply()
        }
        val result = listOfPictures.value as MutableList

        savePicturesToDB(picturesFromNetwork)
        result.addAll(pictureRealization.mainPicture())

        listOfPictures.value = result.distinct()
        if(picturesFromNetwork.size < 10) return false
        return true
    }
    suspend fun updateDataOfDeveloper() : Map<String, String> {
        val dict = NetworkHandbook.getHandbook()
        staticHandbook = dict
        saveHandbookToDB(dict)
        return dict
    }

}