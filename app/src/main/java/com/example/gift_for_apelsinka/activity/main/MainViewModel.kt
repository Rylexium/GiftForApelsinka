package com.example.gift_for_apelsinka.activity.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.db.model.Statements
import com.example.gift_for_apelsinka.db.statementRealization
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkHandbook
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkStatements
import com.example.gift_for_apelsinka.util.Notifaction
import com.example.gift_for_apelsinka.util.WorkWithTime.getNowHour
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private var listOfStatements : MutableLiveData<List<Statements>> = MutableLiveData()
    private var listOfPictures : MutableLiveData<List<Int>> = MutableLiveData()
    private var greetingText : MutableLiveData<String> = MutableLiveData()

    fun getStatements(): List<Statements> {
        if(listOfStatements.value != null) return listOfStatements.value!!
        val list = listOf(
            Statements(1, "Не вздумай думать", "Автор : Rylexium"),
            Statements(2, "Как же хорошо жить, чтобы хорошо есть!", "Автор : Apelsinka"),
            Statements(3, "Не каждому дано понять, как думать...", "Автор : Apelsinka"),
            Statements(4, "Пердечный сриступ", "Автор : Apelsinka"),
            Statements(5, "Мы не выбираем, в каком измерении родиться", "Автор : MrSiAWTF"),
            Statements(6, "Все русские, все жёсткие", "Автор : Какой-то чел с ММ"),
            Statements(7, "Лучше быть живой знакомой, чем мёртвой подругой", "Автор : Rylexium"),
            Statements(8, "Ты прикалываешься или рофлишь?", "Автор : Rylexium"),
            Statements(9, "Раньше было раньше", "Автор : Rylexium"),
            Statements(10, "Силы тратятся во время тренировки. Ману тратить не хочется", "Автор : Илья Каргин"),
            Statements(11, "Я не ленивая. Просто я храню энергию для того момента, когда она мне будет необходима.", "Автор : \"О Лизе\" в Genshin Impact"),
            Statements(12, "Союз Советских Соединённых Штатов Российской Федериации", "Автор : Какой-то чел из ВК")
        )
            .shuffled()
        listOfStatements.value = list
        return listOfStatements.value!!
    }

    fun getPictures(): List<Int> {
        if(listOfPictures.value != null) return listOfPictures.value!!
        val list = mutableListOf(R.drawable.apelsinka, R.drawable.cat1, R.drawable.cat3)
        listOfPictures.value = list
        return listOfPictures.value!!
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

    suspend fun updateData() {
        val statements = NetworkStatements.getStatements()
        val handbook = NetworkHandbook.getHandbook()
        Log.e("statements", statements.toString())
        Log.e("handbook", handbook.toString())
        Log.e("mail", handbook["mail"].toString())
    }
}