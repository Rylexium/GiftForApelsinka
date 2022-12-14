package com.example.gift_for_apelsinka.service.socket

import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.gift_for_apelsinka.cache.HEROKU_URL
import com.example.gift_for_apelsinka.cache.androidId
import com.example.gift_for_apelsinka.cache.setAndroidId
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkNotifications
import com.example.gift_for_apelsinka.retrofit.requestmodel.NotificationDelivered
import com.example.gift_for_apelsinka.retrofit.requestmodel.response.NotificationList
import com.example.gift_for_apelsinka.service.NotificationFromServerService
import com.example.gift_for_apelsinka.service.receiver.background.NetworkChangeReceiver
import com.example.gift_for_apelsinka.service.receiver.NotificationFromServerReceiver.Companion.showNotifications
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage

object NotificationFromServerSocket {
    const val SOCKET_URL = "$HEROKU_URL/v1/notifications/websocket"
    const val CHAT_TOPIC = "/user/topic/notifications"
    const val CHAT_LINK_SOCKET = "/api/v1/notifications/sock"

    private var mStompClient: StompClient? = null
    private var compositeDisposable: CompositeDisposable? = null
    private var backgroundThread : Thread? = null

    fun initSocket(context : Context) {
        try {
            mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, SOCKET_URL)
                .withServerHeartbeat(2 * 7_200_000) // 2 hour = 7_200_000
            resetSubscriptions()
            setAndroidId(context)

            if (mStompClient == null) {
                Log.e(TAG, "mStompClient is null!")
                return
            }

            val topicSubscribe = mStompClient!!.topic(CHAT_TOPIC)
                .subscribeOn(Schedulers.io(), false)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ topicMessage: StompMessage ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val listNotifications = Gson().fromJson(topicMessage.payload, NotificationList::class.java).getNotifications()
                        // ?????????????????? ??????????????
                        for(notification in listNotifications)
                            NetworkNotifications.notificationDelivered(NotificationDelivered(androidId.toString(), notification.id))
                        // ???????????????????? ??????????????????
                        showNotifications(
                            listNotifications,
                            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager,
                            context)
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
                            initSocket(context)
                        }
                        LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT,
                        LifecycleEvent.Type.CLOSED -> {
                            Log.d(TAG, "Stomp connection closed")
                            NotificationFromServerService.running = false
                            NotificationFromServerService.isKillOS = false
                            context.stopService(Intent(context, NotificationFromServerService::class.java))
                            if(NetworkChangeReceiver.isOnline(context))
                                context.startService(Intent(context, NotificationFromServerService::class.java))
                        }
                    }
                }

            compositeDisposable!!.add(lifecycleSubscribe)
            compositeDisposable!!.add(topicSubscribe)

            if (!mStompClient!!.isConnected && NetworkChangeReceiver.isOnline(context)) {
                mStompClient!!.connect()
                backgroundThread = Thread {
                    while (!mStompClient!!.isConnected) { }
                    sendMessage(androidId.toString())
                }
                backgroundThread!!.start()
            }

        } catch (e : Exception) {
            Log.e("exception", e.message.toString())
        }
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