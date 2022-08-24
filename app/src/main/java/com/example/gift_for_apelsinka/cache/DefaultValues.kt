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

val defaultHandbook = mutableMapOf<String, String>().also {
    it["VK"] = "https://vk.com/rylexium"
    it["phone"] = "8-937-17-27-345"
    it["discord"] = "Rylexium#4763"
    it["address"] = "г.Самара, ул.Енисейская, 41, 8"
    it["mail"] = "aleksei.azizov@mail.ru"
    it["url_address_developer"] = "https://www.google.com/maps/place/%D1%83%D0%BB.+%D0%95%D0%BD%D0%B8%D1%81%D0%B5%D0%B9%D1%81%D0%BA%D0%B0%D1%8F,+41,+%D0%A1%D0%B0%D0%BC%D0%B0%D1%80%D0%B0,+%D0%A1%D0%B0%D0%BC%D0%B0%D1%80%D1%81%D0%BA%D0%B0%D1%8F+%D0%BE%D0%B1%D0%BB.,+443034/@53.2326306,50.26828,18.25z/data=!4m5!3m4!1s0x41661bd65a8f0227:0x11132f830c4e06a1!8m2!3d53.2326652!4d50.2699214?hl=ru"
    it["about_apelsinka"] =
        "Я, Быкова Ксения Александровна, но меня ещё называют Цитрусовым Богом. Обучаюсь в Самарском университете. Также у есть собственный логотип."
    it["about_oscar"] = "Немного меня и Оскара \uD83D\uDC15"
    it["about_lera"] = "Немного меня и Леры \uD83C\uDF4B"
    it["about_lexa"] = "Немного меня и Лёши \uD83E\uDD68"
    it["wish_goodnight"] = "Желаю Вам спокойной ночи, \uD83C\uDF1A\n" +
            "Чтобы не приснился Игорь в костюме горничной, \uD83D\uDC69\n" +
            "К которому пристаёт Левон \uD83D\uDD1E\n" +
            "И Лев не спросил у них отличие между базой и базисом, \uD83C\uDD98\n" +
            "В то время, когда их чекает Илюха со своей понамеры \uD83D\uDC41️"
}