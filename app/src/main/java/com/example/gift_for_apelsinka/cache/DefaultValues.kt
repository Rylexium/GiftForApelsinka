package com.example.gift_for_apelsinka.cache

import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.db.model.Statements

val defaultListOfStatements = listOf(
    Statements(1, "Не вздумай думать", "Rylexium"),
    Statements(2, "Как же хорошо жить, чтобы хорошо есть!", "Apelsinka"),
    Statements(3, "Не каждому дано понять, как думать...", "Apelsinka"),
    Statements(4, "Пердечный сриступ", "Apelsinka"),
    Statements(5, "Мы не выбираем, в каком измерении родиться", "MrSiAWTF"),
    Statements(6, "Все русские, все жёсткие", "Какой-то чел с ММ"),
    Statements(7, "Лучше быть живой знакомой, чем мёртвой подругой", "Rylexium"),
    Statements(8, "Ты прикалываешься или рофлишь?", "Rylexium"),
    Statements(9, "Раньше было раньше", "Rylexium"),
    Statements(10, "Силы тратятся во время тренировки. Ману тратить не хочется", "Илья Каргин"),
    Statements(11, "Я не ленивая. Просто я храню энергию для того момента, когда она мне будет необходима.", "\"О Лизе\" в Genshin Impact"),
    Statements(12, "Союз Советских Соединённых Штатов Российской Федериации", "Какой-то чел из ВК"))

val defaultListOfMainPictures = listOf(R.drawable.apelsinka, R.drawable.cat1, R.drawable.cat3)

fun defaultListOfMainPictures(): MutableList<Any> {
    val result = mutableListOf<Any>()
    for(item in defaultListOfMainPictures)
        result.add(item)
    return result
}