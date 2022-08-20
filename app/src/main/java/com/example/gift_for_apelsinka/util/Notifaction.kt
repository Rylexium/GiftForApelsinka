package com.example.gift_for_apelsinka.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.gift_for_apelsinka.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object Notifaction {
    val nameOfApelsinka =
        listOf(
            "Apelsinka", "Ксюша", "Ксения", "Ксюшенька", "солнышко",
            "Мышпаклевка", "\nКсения Александровна", "\nБ.К. Александровна",
            "\nАпельсиновый Бог", "Бог", "Апельсин", "\nЦитрусовый Бог", "")

    fun generateTitleOfGoodMorning() : String {
        var res = ""
        when((1..4).random()) {
            1 -> res = "Доброе утро, "
            2 -> res = "Доброе утречка, "
            3 -> res = "Доброго утра, "
            4 -> res = "Доброго утречка, "
        }
        val value = (System.currentTimeMillis() % nameOfApelsinka.size).toInt()
        if(value == nameOfApelsinka.size - 1)
            return res.subSequence(0, res.length - 2).toString() + "!"
        return res + nameOfApelsinka[value] + "!"
    }

    fun generateTextOfGoodMorning(): String {
        val list = listOf(
           "Как спалось?", "Хорошего дня!", "Сегодня твой день!", "Желаю тебе насыщенного дня!",
            "У тебя сегодня будет интересный день", "Какие на сегодня планы?", "Может сегодня по пиву?",
            "Чую тебя сегодня ждёт успех.", "Не отчаивайся, сегодня всё получиться", "Ты там не забыла про меня?",
            "У тебя сегодня всё получиться!!!", "Сегодня нужно ебашить!!!!", "Апельсинам всегда везёт",
            "Тебе сегодня обязательно повезёт", "Сегодня будет всё чётко!!!",
            "Мне сегодня приснился твой успех", "Сегодня будет море позитива!", "Всё будет хорошо!⭐")

        val emoticons = listOf(
            "\uD83D\uDE04", "\uD83E\uDD29", "\uD83D\uDC4D", "☕", "\uD83E\uDD19", "\uD83E\uDD2F",
            "\uD83D\uDE1C", "\uD83D\uDE0E", "\uD83E\uDD19", "\uD83D\uDE0E", "\uD83C\uDF6A",
            "\uD83D\uDE3B", "\uD83E\uDEF6", "\uD83D\uDE0E", "⭐")
        return list[(System.currentTimeMillis() % list.size).toInt()] + emoticons[(System.currentTimeMillis() % emoticons.size).toInt()]
    }
}