package com.example.gift_for_apelsinka.service.receiver

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.cache.NOTIFICATION_CHANNEL_ID_GOOD_MORNING
import com.example.gift_for_apelsinka.cache.channelNameGoodMorning
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkMessage
import com.example.gift_for_apelsinka.util.InitView
import com.example.gift_for_apelsinka.util.Notifaction
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class GoodMorningReceiver : BroadcastReceiver() {
    private val KEY_TIMETABLE = "GoodMorningRandomTimetable"
    private val KEY_TITLE = "GoodMorningRandomTitle"
    private val KEY_TEXT = "GoodMorningRandomText"

    private val DELAY_FOR_NEXT_NOTIFICATION = 25 //minute

    private lateinit var ctx : Context
    private lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context?, intent: Intent?) {
        notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val sharedPreferences = context.getSharedPreferences("preference_key", Context.MODE_PRIVATE)
        ctx = context

        val timetable = Gson().fromJson(sharedPreferences?.getString(KEY_TIMETABLE, Gson().toJson(Calendar.getInstance())), Calendar::class.java)

        val nowCalendar = Calendar.getInstance()

        if(nowCalendar >= timetable) {

            var title = sharedPreferences?.getString(KEY_TITLE, Notifaction.generateTitleOfGoodMorning())
            var text = sharedPreferences?.getString(KEY_TEXT, Notifaction.generateTextOfGoodMorning())
            goodMorningNotification(title.toString(), text.toString())

            CoroutineScope(Dispatchers.IO).launch {

                //timetable.set(Calendar.DAY_OF_YEAR, nowCalendar.get(Calendar.DAY_OF_YEAR) + 1)

                timetable.timeInMillis = nowCalendar.timeInMillis
                timetable.set(Calendar.HOUR_OF_DAY, nowCalendar.get(Calendar.HOUR_OF_DAY))
                timetable.set(Calendar.MINUTE, nowCalendar.get(Calendar.MINUTE) + DELAY_FOR_NEXT_NOTIFICATION)
                timetable.set(Calendar.SECOND, 0)


                val previous = "Текущие доброе утро : $title : $text"

                title = Notifaction.generateTitleOfGoodMorning()
                text = Notifaction.generateTextOfGoodMorning()

                NetworkMessage.sendMessage(2, 2,
                    "$previous\nСледующие доброе утро : ${timetable.get(Calendar.HOUR_OF_DAY)} : ${timetable.get(
                        Calendar.MINUTE)}, $title : $text")
                val timetableJson = Gson().toJson(timetable)
                sharedPreferences?.edit()
                    ?.putString(KEY_TIMETABLE, timetableJson)
                    ?.putString(KEY_TITLE, title)
                    ?.putString(KEY_TEXT, text)
                    ?.apply()

                val alarmManager = context.getSystemService(Service.ALARM_SERVICE) as AlarmManager

                val pendingIntent = PendingIntent.getBroadcast(context, 2, Intent(context, GoodMorningReceiver::class.java), 0)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timetable.timeInMillis, pendingIntent)
            }
        }
    }

    private fun goodMorningNotification(title : String, text : String) {
        CoroutineScope(Dispatchers.IO).launch {
            val circleImageApelsinka = InitView.getCircleImage(R.drawable.mouse_of_apelsinka, ctx)
            val notificationGoodMorning = NotificationCompat.Builder(ctx, NOTIFICATION_CHANNEL_ID_GOOD_MORNING)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_baseline_wb_sunny_24)
                .setLargeIcon(circleImageApelsinka)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build()
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notifChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID_GOOD_MORNING, channelNameGoodMorning, NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(notifChannel)
            }
            notificationManager.notify(5, notificationGoodMorning)
        }
    }
}