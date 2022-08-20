package com.example.gift_for_apelsinka.util

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object WorkWithTime {
    private fun getNow(): List<String> {
        val timeFormat: DateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val timeText: String = timeFormat.format(Date())
        return timeText.split(":")
    }
    fun getNowHour() : Int {
        return getNow()[0].toInt()
    }
    fun getNowMinute() : Int {
        return getNow()[1].toInt()
    }
}