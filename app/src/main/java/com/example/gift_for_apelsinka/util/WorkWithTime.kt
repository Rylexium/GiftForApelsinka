package com.example.gift_for_apelsinka.util

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object WorkWithTime {
    fun getNowHour() : Int {
        val timeFormat: DateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val timeText: String = timeFormat.format(Date())
        val listTime = timeText.split(":")
        return listTime[0].toInt()
    }
}