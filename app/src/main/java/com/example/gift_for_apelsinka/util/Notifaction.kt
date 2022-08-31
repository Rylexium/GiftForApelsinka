package com.example.gift_for_apelsinka.util

import java.util.*

object Notifaction {
    val nameOfApelsinka =
        listOf(
            "Apelsinka", "Ксюша", "Ксения", "Ксюшенька", "солнышко",
            "Мышпаклевка", "\nКсения Александровна", "\nБ.К. Александровна",
            "\nАпельсиновый Бог", "Бог", "Апельсин", "\nЦитрусовый Бог", "")

    fun generateTitleOfGoodMorning() : String {
        var res = ""
        when(Random().nextInt(4)) {
            0 -> res = "Доброе утро"
            1 -> res = "Доброе утречка"
            2 -> res = "Доброго утра"
            3 -> res = "Доброго утречка"
        }
        val value = Random().nextInt(nameOfApelsinka.size)
        return if(value == nameOfApelsinka.size - 1) "$res!" else res + ", " + nameOfApelsinka[value] + "!"
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
        return list[Random().nextInt(list.size)] + emoticons[Random().nextInt(emoticons.size)]
    }

    fun generateTextOfEquation(): String {
        val list = listOf(
            "Что делаешь?",
            "Го болтать?", "Пошли болтать", "Пошли в дискорд", "Пойдешь в дискорд?",
            "Можем в дискорд пойти", "Пошли на канал?", "Пойдёшь в дискорд болтать?", "Что? Чего? Кого?",
            "Го в фазму", "Го в пиратов", "Пошли в фазму", "Пошли в пиратов", "Пойдешь в фазму?", "Пойдешь в пиратов?")
        return list[Random().nextInt(list.size)]
    }
}