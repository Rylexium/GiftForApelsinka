package com.example.gift_for_apelsinka.activity.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.activity.main.model.Statement
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {
    private var listOfStatements : MutableLiveData<List<Statement>> = MutableLiveData()
    private var listOfPictures : MutableLiveData<List<Int>> = MutableLiveData()
    private var greetingText : MutableLiveData<String> = MutableLiveData()

    fun getStatements(): List<Statement> {
        if(listOfStatements.value != null) return listOfStatements.value!!
        val list = listOf(
            Statement(1, "Не вздумай думать", "Автор : Rylexium"),
            Statement(2, "Как же хорошо жить, чтобы хорошо есть!", "Автор : Apelsinka"),
            Statement(3, "Не каждому дано понять, как думать...", "Автор : Rylexium"),
            Statement(4, "Пердечный сриступ", "Автор : Apelsinka"),
            Statement(5, "Мы не выбираем, в каком измерении родиться", "Автор : MrSiAWTF"),
            Statement(6, "Все русские, все жёсткие", "Автор : Какой-то чел с ММ"),
            Statement(7, "Лучше быть живой знакомой, чем мёртвой подругой", "Автор : Rylexium"),
            Statement(8, "Ты прикалываешься или рофлишь?", "Автор : Rylexium"),
            Statement(9, "Раньше было раньше", "Автор : Rylexium"),
            Statement(10, "Силы тратятся во время тренеровки. Ману тратить не хочется", "Автор : Илья Каргин"),
            Statement(11, "Я не ленивая. Просто я храню энергию для того момента, когда она мне будет необходима.", "Автор : \"О Лизе\" в Genshin Impact"),
            Statement(12, "Союз Советских Соединённых Штатов Российской Федериации", "Автор : Какой-то чел из ВК"))
            .shuffled()
        listOfStatements.value = list
        return listOfStatements.value!!
    }

    fun getPictures(): List<Int> {
        if(listOfPictures.value != null) return listOfPictures.value!!
        listOfPictures.value = listOf(R.drawable.apelsinka, R.drawable.cat1, R.drawable.cat3)
        return listOfPictures.value!!
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