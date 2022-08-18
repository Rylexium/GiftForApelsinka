package com.example.gift_for_apelsinka.util

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.TranslateAnimation
import androidx.core.view.ViewCompat


object AnimView {
    fun animVisible(view : View, time : Long){
        ViewCompat.animate(view)
            .withStartAction { view.visibility = View.VISIBLE }
            .alpha(1f)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setDuration(time)
            .start()
    }

    fun animGone(view : View, time : Long) {
        ViewCompat.animate(view)
            .withStartAction { view.visibility = View.GONE }
            .alpha(1f)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setDuration(time)
            .start()
    }

    fun animTransitionUp(view : View) {
        val animate = TranslateAnimation(0f, 0f, 0f, (-view.height / 2).toFloat())
        animate.duration = 500
        view.startAnimation(animate)
        view.visibility = View.INVISIBLE
        Handler(Looper.getMainLooper()).postDelayed({ view.visibility = View.GONE }, animate.duration)
    }
}