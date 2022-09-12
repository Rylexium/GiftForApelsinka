package com.example.gift_for_apelsinka.cache

import android.graphics.Color

var staticHandbook : MutableMap<String, String> = mutableMapOf()

val colorPrimary = Color.parseColor("#ffffbb33")
val colorPrimaryVariant = Color.parseColor("#ffff8800")

val NOTIFICATION_CHANNEL_ID_GOOD_MORNING = "Канал доброго утра"
val channelNameGoodMorning = "Канал доброго утра"

val NOTIFICATION_CHANNEL_ID_RANDOM_QUESTION = "Канал случайных вопросов"
val channelNameRandomQuestion = "Канал случайных вопросов"

val NOTIFICATION_CHANNEL_ID_NOTIFICATION_FROM_SERVER = "Канал уведомлений от сервера"
val channelNameNotificationFromServer = "Канал уведомлений от сервера"

const val LOCALHOST_URL = "http://10.0.2.2:8080/api/"
const val HEROKU_URL = "https://gift-apelsinka-app.herokuapp.com/api/"
