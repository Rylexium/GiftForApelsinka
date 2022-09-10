package com.example.gift_for_apelsinka.service.socket

import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.core.content.getSystemService
import com.example.gift_for_apelsinka.cache.androidId
import com.example.gift_for_apelsinka.retrofit.requestmodel.response.NotificationList
import com.example.gift_for_apelsinka.service.receiver.NotificationFromServerReceiver
import com.example.gift_for_apelsinka.service.receiver.RandomQuestionReceiver
import com.example.gift_for_apelsinka.util.WorkWithServices.alarmTask
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.*
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage

object NotificationFromServerSocket {
    const val LOCALHOST_URL = "http://10.0.2.2:8080/api/v1/notifications/websocket"
    const val HEROKU_URL = "https://gift-apelsinka-app.herokuapp.com/api/v1/notifications/websocket"
    const val SOCKET_URL = HEROKU_URL
    const val CHAT_TOPIC = "/topic/notifications"
    const val CHAT_LINK_SOCKET = "/api/v1/notifications/sock"

    private var mStompClient: StompClient? = null
    private var compositeDisposable: CompositeDisposable? = null
    private var backgroundThread : Thread? = null

    fun initSocket(context : Context) {
        try {
            mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, SOCKET_URL)
                .withServerHeartbeat(7_200_000) // 2 hour = 7_200_000
            resetSubscriptions()

            if (mStompClient == null) {
                Log.e(TAG, "mStompClient is null!")
                return
            }

            val topicSubscribe = mStompClient!!.topic(CHAT_TOPIC)
                .subscribeOn(Schedulers.io(), false)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ topicMessage: StompMessage ->
                    CoroutineScope(Dispatchers.IO).launch {
                        NotificationFromServerReceiver.showNotifications(
                            Gson().fromJson(topicMessage.payload, NotificationList::class.java).getNotifications(),
                            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager,
                            context
                        )
                    }
                },
                    {
                        Log.e(TAG, "Error!", it)
                    }
                )

            val lifecycleSubscribe = mStompClient!!.lifecycle()
                .subscribeOn(Schedulers.io(), false)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { lifecycleEvent: LifecycleEvent ->
                    when (lifecycleEvent.type!!) {
                        LifecycleEvent.Type.OPENED -> Log.d(TAG, "Stomp connection opened")
                        LifecycleEvent.Type.ERROR -> {
                            Log.e(TAG, "Error", lifecycleEvent.exception)
                        }
                        LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT,
                        LifecycleEvent.Type.CLOSED -> {
                            Log.d(TAG, "Stomp connection closed")
                        }
                    }
                }

            compositeDisposable!!.add(lifecycleSubscribe)
            compositeDisposable!!.add(topicSubscribe)

            if (!mStompClient!!.isConnected) {
                mStompClient!!.connect()
                backgroundThread = Thread {
                    while (!mStompClient!!.isConnected) { }
                    while (mStompClient!!.isConnected) {
                        sendMessage(androidId.toString())
                        Thread.sleep(5_000L)
                    }
                }
                backgroundThread!!.start()
            }

        } catch (e : Exception) {}
    }

    fun resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable!!.dispose()
        }
        compositeDisposable = CompositeDisposable()
    }
    private fun sendCompletable(request: Completable) {
        compositeDisposable?.add(
            request.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d(TAG, "Stomp sended")
                    },
                    {
                        Log.e(TAG, "Stomp error", it)
                    }
                )
        )
    }
    private fun sendMessage(text: String) {
        sendCompletable(mStompClient!!.send(CHAT_LINK_SOCKET, text))
    }
}