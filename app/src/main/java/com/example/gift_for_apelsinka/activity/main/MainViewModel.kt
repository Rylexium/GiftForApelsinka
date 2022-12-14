package com.example.gift_for_apelsinka.activity.main

import android.content.Context
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
import com.example.gift_for_apelsinka.util.DebugFunctions
import com.example.gift_for_apelsinka.util.Notifaction
import com.example.gift_for_apelsinka.util.WorkWithTime.getNowHour
import com.example.gift_for_apelsinka.util.wrapperNextPictures
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class MainViewModel : ViewModel() {
    private var mapOfHandbook : MutableLiveData<Map<String, String>> = MutableLiveData()
    private var listOfStatements : MutableLiveData<List<Statements>> = MutableLiveData()
    private var listOfPictures : MutableLiveData<List<Any>> = MutableLiveData()
    private var greetingText : MutableLiveData<String> = MutableLiveData()
    private val sizeOfStatements = 15
    companion object {
        var pageOfStatements = 0
        var pageOfMainPicture = 0
    }

    fun getStatements(): MutableLiveData<List<Statements>> = runBlocking {
        DebugFunctions.addDebug("MainViewModel","getStatements")
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
        DebugFunctions.addDebug("MainViewModel","getPictures")
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
        DebugFunctions.addDebug("MainViewModel","getHandbook")
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
        DebugFunctions.addDebug("MainViewModel","getImageOfTime")
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
        DebugFunctions.addDebug("MainViewModel","getGreetingsForApelsinka")
        if(greetingText.value != null && !flag) return null

        var result = ""
        when(getNowHour()) {
            23 -> result = "???????????? ????????"
            in 0..4 -> result = "???????????? ????????"
            in 5..11 -> {
                when(java.util.Random().nextInt(4)) {
                    0 -> result = "???????????? ????????"
                    1 -> result = "???????????? ??????????????"
                    2 -> result = "?????????????? ????????"
                    3 -> result = "?????????????? ??????????????"
                }
            }
            in 12..18 -> {
                when(java.util.Random().nextInt(2)) {
                    0 -> result = "???????????? ????????"
                    1 -> result = "?????????????????? ??????????????"
                }
            }
            in 19..22 -> result = "???????????? ??????????"
        }

        val value = java.util.Random().nextInt(Notifaction.nameOfApelsinka.size)
        if(value == Notifaction.nameOfApelsinka.size - 1)
            greetingText.value = "$result!"
        else
            greetingText.value =  result + ", " + Notifaction.nameOfApelsinka[value] + "!"

        return greetingText.value!!
    }

    suspend fun updateStatements() : Boolean {
        DebugFunctions.addDebug("MainViewModel","updateStatements")
        val statementsFromNetwork = NetworkStatements.getStatements(0, 1000)

        val statementsFromBD = statementRealization.getAll()


        val list1 = statementsFromNetwork.map { it.id }.subtract(statementsFromBD.map { it.id }.toSet()) //???????????? ?? ????????
        val list2 = statementsFromBD.map { it.id }.subtract(statementsFromNetwork.map { it.id }.toSet()) // ?? ???????? ?????? ??????
        if(list1.size < list2.size || list1.size > list2.size) {
            deleteStatementsFromDB()
            saveStatementsToDB(statementsFromNetwork)
            listOfStatements.value = statementsFromNetwork
            pageOfStatements = statementsFromNetwork.size / sizeOfStatements
        }
        return (list1.size < list2.size || list1.size > list2.size)//!isEquals //true ???????? ?????????? ????????????
    }

    suspend fun nextStatements() : Boolean {
        DebugFunctions.addDebug("MainViewModel","nextStatements")
        var statementsFromNetwork: List<Statements>
        var statementsFromBD : List<Statements>
        while(true) {
            statementsFromNetwork = NetworkStatements.getStatements(pageOfStatements, sizeOfStatements)
            val previosSize = statementRealization.getAll().size
            saveStatementsToDB(statementsFromNetwork)
            statementsFromBD = statementRealization.getAll()

            if(statementsFromNetwork.size == sizeOfStatements)
                pageOfStatements += 1

            if(previosSize != statementsFromBD.size) break //?????????? ??????, ???????? ????????????????????
            if(statementsFromNetwork.size < sizeOfStatements) break //???????????? ???????????? sizeOfStatements -> ?????? ??????????
        }
        if(statementsFromNetwork.isEmpty()) return false

        val result = listOfStatements.value as MutableList

        if(statementsFromNetwork.map { it.id }.subtract(statementsFromBD.map { it.id }.toSet()).isNotEmpty() ||
            statementsFromNetwork.subtract(result.toSet()).isNotEmpty()) { //???????? ?????????????? ???????????????????????? ?? ?? ???????? ???? ??????????????????, ???? ????????????
            result.addAll(statementsFromBD)
            listOfStatements.value = result.distinct()
        }
        if(statementsFromNetwork.size < sizeOfStatements) return false
        return true
    }

    suspend fun nextMainPictures(context : Context) : Boolean {
        DebugFunctions.addDebug("MainViewModel","nextMainPictures")
        val res = wrapperNextPictures(
            { NetworkPictures.getAllMainPicture(it) },
            { pictureRealization.mainPicture() },
            pageOfMainPicture, listOfPictures, defaultListOfMainPictures(), context)
        pageOfMainPicture = res.first
        return res.second
    }
    suspend fun updateDataOfDeveloper() : Map<String, String> {
        DebugFunctions.addDebug("MainViewModel","updateDataOfDeveloper")
        val dict = NetworkHandbook.getHandbook()
        staticHandbook = dict
        saveHandbookToDB(dict)
        return dict
    }

}