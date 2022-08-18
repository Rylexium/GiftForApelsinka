package com.example.gift_for_apelsinka.activity.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.activity.main.model.Statement
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {
    private var listOfStatements : MutableLiveData<MutableList<Statement>> = MutableLiveData()
    private var greetingText : MutableLiveData<String> = MutableLiveData()

    fun getStatements(): MutableList<Statement> {
        val list = mutableListOf( Statement(1, "Не вздумай думать", "Rylexium"),
            Statement(2, "Хорошо жить, чтобы хорош есть", "Apelsinka"),
            Statement(3, "Не вздумай думать", "Rylexium"),
            Statement(4, "Пердечный сриступ", "Apelsinka"),
            Statement(5, "Не вздумай думать", "Rylexium"),
            Statement(6, "Хорошо жить, чтобы хорош есть", "Apelsinka"))
        listOfStatements.value = list
        return listOfStatements.value!!
    }

    fun getNowHour() : Int {
        val timeFormat: DateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val timeText: String = timeFormat.format(Date())
        val listTime = timeText.split(":")
        return listTime[0].toInt()
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

    fun getGreetingsForApelsinka(): String? {
        if(greetingText.value != null) return null

        val nameOfApelsinka =
            listOf( "Apelsinka", "Ксюша", "Ксения", "Ксюшенька", "солнышко",
                    "Мышпаклевка", "Ксения Александровна", "Б.К. Александровна",
                    "Апельсиновый Бог", "Бог", "Апельсин", "Цитрусовый Бог")
        var result = ""
        when(getNowHour()) {
            23 -> result = "Доброй ночи, "
            in 0..4 -> result = "Доброй ночи, "
            in 5..11 -> {
                when((1..2).random()) {
                    1 -> result = "Доброе утро, "
                    2 -> result = "Доброе утречка, "
                }
            }
            in 12..18 -> result = "Добрый день, "
            in 19..22 -> result = "Добрый вечер, "
        }
        greetingText.value = result + nameOfApelsinka[(System.currentTimeMillis() % nameOfApelsinka.size).toInt()] + "!"
        return greetingText.value!!
    }
}