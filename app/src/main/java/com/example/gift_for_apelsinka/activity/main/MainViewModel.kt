package com.example.gift_for_apelsinka.activity.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gift_for_apelsinka.model.Statement
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

    fun getGreetingsForApelsinka(): String? {
        if(greetingText.value != null) return null
        val timeFormat: DateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val timeText: String = timeFormat.format(Date())
        val listTime = timeText.split(":")
        val nameOfApelsinka =
            listOf( "Apelsinka", "Ксюша", "Ксения", "Ксюшенька", "солнышко",
                    "Мышпаклевка", "Ксения Александровна", "Б.К. Александровна",
                    "Апельсиновый Бог", "Бог", "Апельсин", "Цитрусовый Бог")
        var result = ""
        when(listTime[0].toInt()) {
            23 -> result = "Доброй ночи, "
            in 0..4 -> result = "Доброй ночи, "
            in 5..11 -> {
                when((1..2).random()) {
                    1 -> result = "Доброе утро, "
                    2 -> result = "Доброй утречка, "
                }
            }
            in 12..18 -> result = "Доброй день, "
            in 18..22 -> result = "Доброй вечер, "
        }
        greetingText.value = result + nameOfApelsinka[(System.currentTimeMillis() % nameOfApelsinka.size).toInt()] + "!"
        return greetingText.value!!
    }
}