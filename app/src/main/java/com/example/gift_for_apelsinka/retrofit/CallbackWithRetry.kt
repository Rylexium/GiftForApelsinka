package com.example.gift_for_apelsinka.retrofit

import android.util.Log
import retrofit2.Call
import retrofit2.Callback

abstract class CallbackWithRetry<T>(call: Call<T>) : Callback<T> {
    private val call: Call<T>
    private var retryCount = 0
    fun onFailure(t: Throwable) {
        t.localizedMessage?.let { Log.e(TAG, it) }
        if (retryCount++ < TOTAL_RETRIES) {
            Log.v(TAG, "Retrying... ($retryCount out of $TOTAL_RETRIES)")
            retry()
        }
    }

    private fun retry() {
        call.clone().enqueue(this)
    }

    companion object {
        private const val TOTAL_RETRIES = 3
        private val TAG = CallbackWithRetry::class.java.simpleName
    }

    init {
        this.call = call
    }
}